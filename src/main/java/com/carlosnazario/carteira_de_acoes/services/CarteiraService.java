package com.carlosnazario.carteira_de_acoes.services;

import com.carlosnazario.carteira_de_acoes.api.BrapiClient;
import com.carlosnazario.carteira_de_acoes.api.Cotacao;
import com.carlosnazario.carteira_de_acoes.enteties.*;
import com.carlosnazario.carteira_de_acoes.exceptions.carteiras.CarteiraNaoEncontradaException;
import com.carlosnazario.carteira_de_acoes.exceptions.carteiras.QuantidadeInsuficienteException;
import com.carlosnazario.carteira_de_acoes.exceptions.carteiras.SaldoInsuficienteException;
import com.carlosnazario.carteira_de_acoes.repositories.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Principal classe da aplicacao que realiza todas as regras para criacao e manutencao de carteiras de um determinado cliente
 */

@Service
public class CarteiraService {
    @Autowired
    private CarteiraRepository carteiraRepository;
    @Autowired
    private AtivoService ativoService;
    @Autowired
    private AtivoCompradoService ativoCompradoService;
    @Autowired
    private AtivoVendidoService ativoVendidoService;
    @Autowired
    private BrapiClient brapiClient;

    public void criarCarteira(Carteira carteira) {
        carteiraRepository.save(carteira);
    }

    /**
     * Metodo que busca uma carteira ja criada pelo seu "id"
     * Ja adiciona alguns atributos em tempo real fornecidos pela api
     *
     * @param id Identificador unico da carteira que se deseja buscar
     * @return Uma carteira consolidada do cliente, ou uma lista vazia, caso nao haja ativos na carteira
     * @throws JsonProcessingException Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    public Carteira buscarCarteiraPorId(Long id) throws JsonProcessingException {

        // Pega a carteira e os ativos da carteira
        Carteira carteira = carteiraRepository.findById(id).orElseThrow();
        List<Ativo> ativos = carteira.getAtivos();

        // A lista de ativos da carteira ja possui todos os dados que nao precisam ser atualizados em tempo real
        // O laco pega os atributos pegos pela api em tempo real e os setta nos ativos da carteira
        for (Ativo ativo : ativos) {
            Cotacao cotacao = brapiClient.getCotacao(ativo.getTicker());

            BigDecimal precoAtual = cotacao.getPrecoAtual();
            BigDecimal variacaoDiariaPreco = cotacao.getVariacaoDiariaPreco();
            BigDecimal variacaoDiariaPercentual = cotacao.getVariacaoDiariaPercentual().setScale(2, RoundingMode.HALF_UP);

            ativo.setPrecoAtual(precoAtual);
            ativo.setVariacaoDiariaPreco(variacaoDiariaPreco);
            ativo.setVariacaoDiariaPercentual(variacaoDiariaPercentual);
        }

        return carteira;
    }

    /**
     * Metodo que adiciona saldo da carteira
     *
     * @param id        Identificador unico da carteira onde se deseja adicionar algum valor de saldo
     * @param novoSaldo Valor que sera adicionado do saldo total da carteira
     * @return Uma carteira ja com o saldo adicionado
     */
    public Carteira adicionarSaldo(Long id, BigDecimal novoSaldo) {

        // Pega a carteira
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new CarteiraNaoEncontradaException("Carteira não encontrada"));

        // Adiciona um novo saldo na carteira
        BigDecimal total = carteira.getSaldo().add(novoSaldo);
        carteira.setSaldo(total);
        return carteiraRepository.save(carteira);
    }

    /**
     * Metodo que baixa saldo da carteira
     *
     * @param id            Identificador unico da carteira onde se deseja remover algum valor de saldo
     * @param saldoAremover Valor que sera removido do saldo total da carteira
     * @return Uma carteira ja com o saldo removido
     * @throws SaldoInsuficienteException Caso o volar de saldo a remover seja maior que o saldo disponivel
     */
    public Carteira removerSaldo(Long id, BigDecimal saldoAremover) throws SaldoInsuficienteException {

        // Pega a carteira
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new CarteiraNaoEncontradaException("Carteira não encontrada"));

        // Validar saldo
        if (carteira.getSaldo().compareTo(saldoAremover) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        // Setta o saldo atualizado na carteira
        BigDecimal total = carteira.getSaldo().subtract(saldoAremover);
        carteira.setSaldo(total);
        return carteiraRepository.save(carteira);
    }

    /**
     * Principal metodo da aplicacao ao lado do metodo de vender ativos. Realiza um verificacao a cada compra.
     * Se a algum ativo igual na carteira, atualiza este e consolida o ativo na carteira.
     * Caso esse nao exista, cria um novo e o adiciona na carteira
     *
     * @param idCarteira       Identificador unico da carteira onde se deseja realizar a compra de algum ativo
     * @param ativoParaComprar Ativo o qual sera adicionado na carteira
     * @return Uma carteira consolidada ja com os ativos comprados, ou uma lista vazia, caso nao haja ativos na carteira
     * @throws SaldoInsuficienteException Caso nao haja saldo suficiente para realizar a compra
     * @throws JsonProcessingException    Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    public Carteira comprarAtivo(Long idCarteira, Ativo ativoParaComprar) throws SaldoInsuficienteException, JsonProcessingException {

        // Pega a carteira
        Carteira carteira = carteiraRepository.findById(idCarteira)
                .orElseThrow(() -> new CarteiraNaoEncontradaException("Carteira não encontrada"));

        // Converte em ativo comprado e cria um ativo comprado
        AtivoComprado ativoTransformado = ativoCompradoService.transformaEmAtivoComprado(ativoParaComprar);
        ativoCompradoService.criarAtivoComprado(ativoTransformado);

        // Adiciona o ativo comprado a lista de ativos comprados
        carteira.getAtivosComprados().add(ativoTransformado);

        // Pega o valor total da compra
        BigDecimal valorTotalCompra = ativoParaComprar.getPrecoMedio().multiply(BigDecimal.valueOf(ativoParaComprar.getQuantidade()));

        // Valida o saldo
        if (carteira.getSaldo().compareTo(valorTotalCompra) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        // Verifica se o ativo ja existe na lista de ativos da carteira
        Ativo ativoExistente = this.verificaSeAtivoExisteNaCarteira(idCarteira, ativoParaComprar);

        // Se o ativo ja existe atualiza os atributos, senao cria um ativo
        if (ativoExistente != null) {
            atualizaAtivoExistenteCompra(ativoExistente, ativoParaComprar);
        } else {
            ativoParaComprar = ativoService.criarAtivo(ativoParaComprar);
        }

        // Atualiza saldo
        carteira.setSaldo(carteira.getSaldo().subtract(valorTotalCompra));

        // Atualiza o valor investido
        carteira.setValorInvestido(carteira.getValorInvestido().add(valorTotalCompra));

        // Adiciona ou atualiza o ativo na carteira
        List<Ativo> ativos = carteira.adicionarAtivo(ativoParaComprar);

        // Atualiza o total da carteira
        carteira.setTotalDaCarteira(carteira.getSaldo().add(carteira.getValorInvestido()));

        // Caso haja um ativo nulo, remove o da lista
        ativos.removeIf(ativo -> ativo.getId() == null ||
                ativo.getTicker() == null ||
                ativo.getQuantidade() == null ||
                ativo.getTotalinvestido() == null ||
                ativo.getPrecoMedio() == null ||
                ativo.getTipo() == null);

        return carteiraRepository.save(carteira);
    }

    /**
     * Principal metodo da aplicacao ao lado do metodo de comprar ativos.
     * Ao realizar a venda calcula os dados de lucro ou prejuizo e tambem retorna a carteira ja consolidada
     *
     * @param idCarteira     Identificador unico da carteira onde se deseja vender algum ativo
     * @param ativoParaVenda Ativo da carteira que sera vendido
     * @return Uma carteira consolidada ja sem os ativos vendidos, ou uma lista vazia, caso nao haja ativos na carteira
     * @throws QuantidadeInsuficienteException Caso nao haja ativos em quantidades suficientes para a venda
     * @throws JsonProcessingException         Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    public Carteira venderAtivo(Long idCarteira, Ativo ativoParaVenda) throws QuantidadeInsuficienteException, JsonProcessingException {

        // Pega a carteira e os ativos da carteira
        Carteira carteira = carteiraRepository.findById(idCarteira)
                .orElseThrow(() -> new CarteiraNaoEncontradaException("Carteira não encontrada"));
        List<Ativo> ativos = carteira.getAtivos();

        // verifica se o ativo existe na carteira
        Ativo ativoExistente = verificaSeAtivoExisteNaCarteira(idCarteira, ativoParaVenda);

        // Valida se a quantidade a ser vendida é menor ou igual à quantidade disponível
        assert ativoExistente != null;
        if (ativoExistente.getQuantidade().compareTo(ativoParaVenda.getQuantidade()) < 0) {
            throw new QuantidadeInsuficienteException("Quantidade insuficiente para venda");
        }
        atualizaAtivoExistenteVenda(ativoExistente, ativoParaVenda);

        // Pega a quantidade de ativos vendidos e converter para BigDecimal
        BigDecimal quantidadeVenda = BigDecimal.valueOf(ativoParaVenda.getQuantidade());

        // Atualiza o preco da venda
        BigDecimal precoVenda = ativoParaVenda.getPrecoVenda();
        ativoExistente.setPrecoVenda(precoVenda);

        // Calcula o valor total da venda
        BigDecimal valorTotalVenda = precoVenda.multiply(quantidadeVenda);

        // Pega o valor venda e o valor medio da compra
        BigDecimal valorTotalPrecoMedio = ativoExistente.getPrecoMedio().multiply(quantidadeVenda);
        BigDecimal lucroPrejuizoVenda = valorTotalVenda.subtract(valorTotalPrecoMedio);

        // Atualiza o valor investido
        BigDecimal valorInvestido = carteira.getValorInvestido().subtract(valorTotalPrecoMedio);
        carteira.setValorInvestido(valorInvestido);

        // Atualiza o saldo da carteira
        carteira.setSaldo(carteira.getSaldo().add(valorTotalVenda));

        // Atualiza o lucro ou prejuizo da carteira
        carteira.setLucroPrejuizo(carteira.getLucroPrejuizo().add(lucroPrejuizoVenda));

        // Caso haja um ativo nulo, remove o da lista
        ativos.removeIf(ativo -> ativo.getQuantidade() == 0);

        // Pega o preco medio do ativo a ser vendido
        BigDecimal precoMedio = ativoExistente.getPrecoMedio();
        ativoParaVenda.setPrecoMedio(precoMedio);

        // Atualiza o ganho ou perda diaria dos ativos na carteira
        BigDecimal ganhoPerdaDiaria = ativoExistente.getVariacaoDiariaPreco().multiply(BigDecimal.valueOf(ativoExistente.getQuantidade()));
        ativoExistente.setGanhoPerdaDiaria(ganhoPerdaDiaria);

        // Verifica so o valor total investido e diferente de zero antes de realizar a operacao
        if (ativoExistente.getTotalinvestido().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal ganhoPerdaPercentual = ativoExistente.getTotalAtualizado()
                    .divide(ativoExistente.getTotalinvestido(), 2, RoundingMode.HALF_UP)
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100));
            ativoExistente.setGanhoPerdaPercentual(ganhoPerdaPercentual);

        } else {
            // Lidar com a situação em que totalinvestido é zero
            ativoExistente.setGanhoPerdaPercentual(BigDecimal.ZERO);
        }

        // Atualiza o total da carteira
        carteira.setTotalDaCarteira(carteira.getSaldo().add(carteira.getValorInvestido()));

        // Converte em ativo vendido e cria um ativo vendido
        AtivoVendido ativoTransformado = ativoVendidoService.transformaEmAtivoVendido(ativoParaVenda);
        ativoVendidoService.criarAtivoVendido(ativoTransformado);

        // Adiciona o ativo comprado a lista de ativos comprados
        carteira.getAtivosVendidos().add(ativoTransformado);

        return carteiraRepository.save(carteira);
    }

    /**
     * Retorna a lista de todos os ativos comprados para uma carteira específica.
     *
     * @param id Identificador único da carteira para a qual se deseja listar os ativos comprados.
     * @return Uma lista de objetos "AtivoComprado" representando os ativos comprados para a carteira, ou uma lista vazia caso a carteira não possua ativos comprados.
     * @throws CarteiraNaoEncontradaException Caso a carteira com o ID informado não seja encontrada.
     */
    public List<AtivoComprado> listarAtivosComprados(Long id) {
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new CarteiraNaoEncontradaException("Carteira não encontrada"));
        return carteira.getAtivosComprados();
    }

    /**
     * Retorna a lista de todos os ativos vendidos para uma carteira específica.
     *
     * @param id Identificador único da carteira para a qual se deseja listar os ativos comprados.
     * @return Uma lista de objetos "AtivoComprado" representando os ativos vendidos para a carteira, ou uma lista vazia caso a carteira não possua ativos comprados.
     * @throws CarteiraNaoEncontradaException Caso a carteira com o ID informado não seja encontrada.
     */
    public List<AtivoVendido> listarAtivosVendidos(Long id) {
        Carteira carteira = carteiraRepository.findById(id)
                .orElseThrow(() -> new CarteiraNaoEncontradaException("Carteira não encontrada"));
        return carteira.getAtivosVendidos();
    }

    /**
     * Metodo auxiliar que verifica se existe um ativo na carteira antes de realizar a consolidacao,
     * caso este exista
     *
     * @param id    Identificador único da carteira para a qual se deseja listar os ativos comprados.
     * @param ativo Ativo que se deseja fazer a verificacao de existencia ou nao na carteira
     * @return Ativo filtrado pelo seu ticker, caso ele exista
     * @throws JsonProcessingException Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    private Ativo verificaSeAtivoExisteNaCarteira(Long id, Ativo ativo) throws JsonProcessingException {
        Carteira carteira = this.buscarCarteiraPorId(id);

        // Verificar se o ativo já existe na carteira
        return carteira.getAtivos().stream()
                .filter(a -> a.getTicker().equals(ativo.getTicker()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Metodo auxiliar que busca e setta a grande maioria dos atributos do ativo no momento da compra
     *
     * @param ativoExistente   Ativo que ja existe na carteira que sera atualizado
     * @param ativoParaComprar Ativo com os novos dados para atualizar o ativo ja existente na carteira
     * @throws JsonProcessingException Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    private void atualizaAtivoExistenteCompra(Ativo ativoExistente, Ativo ativoParaComprar) throws JsonProcessingException {

        BigDecimal valorTotalCompra = ativoParaComprar.getPrecoMedio().multiply(BigDecimal.valueOf(ativoParaComprar.getQuantidade()));

        // Atualizar o ativo existente
        int novaQuantidade = ativoExistente.getQuantidade() + ativoParaComprar.getQuantidade();
        ativoExistente.setQuantidade(novaQuantidade);
        ativoExistente.setTotalinvestido(ativoExistente.getTotalinvestido().add(valorTotalCompra));
        ativoExistente.setPrecoMedio(ativoExistente.getTotalinvestido()
                .divide(BigDecimal.valueOf(ativoExistente.getQuantidade()), 2, RoundingMode.HALF_UP));

        // Pegar a cotacao atualizada
        Cotacao cotacao = brapiClient.getCotacao(ativoParaComprar.getTicker());

        // Pegar os dados fornecidos pela api
        BigDecimal precoAtual = cotacao.getPrecoAtual();
        BigDecimal variacaoDiariaPreco = cotacao.getVariacaoDiariaPreco();
        BigDecimal variacaoDiariaPercentual = cotacao.getVariacaoDiariaPercentual().setScale(2, RoundingMode.HALF_UP);

        // Settar os dados pegos pela api
        ativoExistente.setPrecoAtual(precoAtual);
        ativoExistente.setVariacaoDiariaPreco(variacaoDiariaPreco);
        ativoExistente.setVariacaoDiariaPercentual(variacaoDiariaPercentual);

        // Atualiza o valor total dos ativos na carteira
        BigDecimal totalAtualizado = cotacao.getPrecoAtual().multiply(BigDecimal.valueOf(novaQuantidade));
        ativoExistente.setTotalAtualizado(totalAtualizado);

        // Atualiza o ganho ou perda total dos ativos na carteira
        BigDecimal ganhoPerdaTotal = ativoExistente.getTotalAtualizado().subtract(ativoExistente.getTotalinvestido());
        ativoExistente.setGanhoPerdaTotal(ganhoPerdaTotal);

        // Atualiza o ganho ou perda diaria dos ativos na carteira
        BigDecimal ganhoPerdaDiaria = ativoExistente.getVariacaoDiariaPreco().multiply(BigDecimal.valueOf(ativoExistente.getQuantidade()));
        ativoExistente.setGanhoPerdaDiaria(ganhoPerdaDiaria);

        // Verifica se totalinvestido eh diferente de zero antes da divisão e setta o ganho ou perda percentual total do ativo
        if (ativoExistente.getTotalinvestido().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal ganhoPerdaPercentual = ativoExistente.getTotalAtualizado()
                    .divide(ativoExistente.getTotalinvestido(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .subtract(BigDecimal.valueOf(100));
            ativoExistente.setGanhoPerdaPercentual(ganhoPerdaPercentual);
        } else {
            // Lidar com a situação em que totalinvestido é zero
            ativoExistente.setGanhoPerdaPercentual(BigDecimal.ZERO);
        }
    }

    /**
     * Metodo auxiliar que busca e setta a grande maioria dos atributos do ativo no momento da venda
     *
     * @param ativoExistente ativo que ja existe na carteira que sera atualizado
     * @param ativoParaVenda ativo com os novos dados para atualizar o ativo ja existente na carteira
     * @throws JsonProcessingException Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    private void atualizaAtivoExistenteVenda(Ativo ativoExistente, Ativo ativoParaVenda) throws JsonProcessingException {

        // Atualiza a quantidade na carteira
        int novaQuantidade = ativoExistente.getQuantidade() - (ativoParaVenda.getQuantidade());
        ativoExistente.setQuantidade(novaQuantidade);

        // Atualiza o valor investido total do ativo na carteira
        BigDecimal novoTotal = ativoExistente.getPrecoMedio().multiply(BigDecimal.valueOf(novaQuantidade));
        ativoExistente.setTotalinvestido(novoTotal);

        // Pega a cotacao atraves da api
        Cotacao cotacao = brapiClient.getCotacao(ativoParaVenda.getTicker());

        // Pega os dados fornecidos pela api
        BigDecimal precoAtual = cotacao.getPrecoAtual();
        BigDecimal variacaoDiariaPreco = cotacao.getVariacaoDiariaPreco();
        BigDecimal variacaoDiariaPercentual = cotacao.getVariacaoDiariaPercentual().setScale(2, RoundingMode.HALF_UP);

        // Setta os dados pegos pela api
        ativoExistente.setPrecoAtual(precoAtual);
        ativoExistente.setVariacaoDiariaPreco(variacaoDiariaPreco);
        ativoExistente.setVariacaoDiariaPercentual(variacaoDiariaPercentual);

        // Setta o valor atualizado dos ativos na carteira
        BigDecimal totalAtualizado = cotacao.getPrecoAtual().multiply(BigDecimal.valueOf(novaQuantidade));
        ativoExistente.setTotalAtualizado(totalAtualizado);

        // Atualiza o ganho ou perda diaria dos ativos na carteira
        BigDecimal ganhoPerdaTotal = ativoExistente.getTotalAtualizado().subtract(ativoExistente.getTotalinvestido());
        ativoExistente.setGanhoPerdaTotal(ganhoPerdaTotal);
    }
}

