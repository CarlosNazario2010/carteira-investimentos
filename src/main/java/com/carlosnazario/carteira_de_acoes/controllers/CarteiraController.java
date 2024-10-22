package com.carlosnazario.carteira_de_acoes.controllers;

import com.carlosnazario.carteira_de_acoes.dtos.*;
import com.carlosnazario.carteira_de_acoes.dtos.requests.AtualizarSaldoRequest;
import com.carlosnazario.carteira_de_acoes.dtos.requests.BuscarCarteiraRequest;
import com.carlosnazario.carteira_de_acoes.dtos.requests.CriarCarteiraRequest;
import com.carlosnazario.carteira_de_acoes.enteties.*;
import com.carlosnazario.carteira_de_acoes.exceptions.carteiras.AtivoNaoEncontradoException;
import com.carlosnazario.carteira_de_acoes.exceptions.carteiras.QuantidadeInsuficienteException;
import com.carlosnazario.carteira_de_acoes.exceptions.carteiras.SaldoInsuficienteException;
import com.carlosnazario.carteira_de_acoes.exceptions.clientes.ClienteNaoEncontradoException;
import com.carlosnazario.carteira_de_acoes.services.CarteiraService;
import com.carlosnazario.carteira_de_acoes.services.ClienteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@EnableCaching
@RestController
@RequestMapping("/carteiras")
public class CarteiraController {
    @Autowired
    private CarteiraService carteiraService;
    @Autowired
    private ClienteService clienteService;

    /**
     * Controller que chama o metodo responsavel por criar uma carteira, recebendo um determinado cliente
     *
     * @param request Dto que contem o "id" do cliente que ira ser o detentor da carteira
     * @return Um dto de uma carteira recem criada para um determinado cliente
     */
    @PostMapping
    public ResponseEntity<CarteiraDTO> criarCarteiraComIdCliente(@RequestBody CriarCarteiraRequest request) {

        Cliente cliente = clienteService.buscarClientePorId(request.getClienteId())
                .orElseThrow(() -> new ClienteNaoEncontradoException("cliente nao encontrado"));

        Carteira carteira = new Carteira();
        carteira.setCliente(cliente);
        carteiraService.criarCarteira(carteira);

        ClienteDTO clienteDTO = criaClienteDTO(cliente);
        List<AtivoDTO> ativosDTO = new ArrayList<>();
        CarteiraDTO carteiraDTO = criaCarteiraDTO(carteira, clienteDTO, ativosDTO);

        return ResponseEntity.created(URI.create("/carteiras/" + carteira.getId()))
                .body(carteiraDTO);
    }


    @GetMapping
    public ResponseEntity<CarteiraDTO> buscarCarteiraPorId(
            @RequestParam Long clienteId,
            @RequestParam Long carteiraId
    ) throws JsonProcessingException {
        Cliente cliente = clienteService.buscarClientePorId(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("cliente nao encontrado"));
        Carteira carteira = carteiraService.buscarCarteiraPorId(carteiraId);

        List<Ativo> ativos = carteira.getAtivos();

        ClienteDTO clienteDTO = criaClienteDTO(cliente);
        List<AtivoDTO> ativosDTO = criaAtivosDTO(ativos);
        CarteiraDTO carteiraDTO = criaCarteiraDTO(carteira, clienteDTO, ativosDTO);

        return ResponseEntity.ok(carteiraDTO);
    }

    /**
     * Adiciona um determinado valor de saldo em um determinada carteira
     *
     * @param id      Identificados unico da carteira que tera o valor de saldo adicionado
     * @param request Dto com as informacoes recebidas do cliente da requisicao para realizar a adicao no saldo da carteira
     * @return Um dto de uma carteira ja com o valor de saldo atualizado
     */
    @PutMapping("/{id}/adicionar")
    @CacheEvict(value = "carteiras", key = "#id")
    public ResponseEntity<CarteiraDTO> adicionarSaldo(@PathVariable Long id, @RequestBody AtualizarSaldoRequest request) {

        Carteira carteiraAtualizada = carteiraService.adicionarSaldo(id, request.getNovoSaldo());
        Cliente cliente = carteiraAtualizada.getCliente();
        List<Ativo> ativos = carteiraAtualizada.getAtivos();

        ClienteDTO clienteDTO = criaClienteDTO(cliente);
        List<AtivoDTO> ativosDTO = criaAtivosDTO(ativos);
        CarteiraDTO carteiraDTO = criaCarteiraDTO(carteiraAtualizada, clienteDTO, ativosDTO);

        return ResponseEntity.ok(carteiraDTO);
    }

    /**
     * Baixa um determinado valor de saldo de uma determinada carteira
     *
     * @param id      Identificados unico da carteira que tera o valor de saldo baixado
     * @param request dto com as informacoes recebidas do cliente da requisicao para realizar o resgate de saldo na carteira
     * @return Um dto de uma carteira ja com o valor de saldo atualizado
     * @throws SaldoInsuficienteException Caso o valor de saldo a ser resgatado seja maior que o saldo disponivel
     */
    @PutMapping("/{id}/remover")
    @CacheEvict(value = "carteiras", key = "#id")
    public ResponseEntity<CarteiraDTO> removerSaldo(@PathVariable Long id, @RequestBody AtualizarSaldoRequest request) throws SaldoInsuficienteException {

        Carteira carteiraAtualizada = carteiraService.removerSaldo(id, request.getNovoSaldo());
        Cliente cliente = carteiraAtualizada.getCliente();
        List<Ativo> ativos = carteiraAtualizada.getAtivos();

        ClienteDTO clienteDTO = criaClienteDTO(cliente);
        List<AtivoDTO> ativosDTO = criaAtivosDTO(ativos);
        CarteiraDTO carteiraDTO = criaCarteiraDTO(carteiraAtualizada, clienteDTO, ativosDTO);

        return ResponseEntity.ok(carteiraDTO);
    }

    /**
     * Controller que chama o metodo responsavel por comprar ativos para uma determinada carteira
     *
     * @param id    Identificador unico da carteira que ira conter o ativo a ser comprado
     * @param ativo Ativo que sera adicionado na carteira
     * @return Um dto de uma carteira ja consolidada com os ativos comprados
     * @throws SaldoInsuficienteException Caso nao haja saldo suficiente na carteira para realizar a compra
     * @throws JsonProcessingException    Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    @PostMapping("/{id}/comprar")
    @CacheEvict(value = "carteiras", key = "#id")
    public ResponseEntity<CarteiraDTO> comprarAtivo(@PathVariable Long id, @RequestBody Ativo ativo)
            throws SaldoInsuficienteException, JsonProcessingException {

        Carteira carteiraAtualizada = carteiraService.comprarAtivo(id, ativo);

        Cliente cliente = carteiraAtualizada.getCliente();
        ClienteDTO clienteDTO = criaClienteDTO(cliente);

        List<Ativo> ativos = carteiraAtualizada.getAtivos();
        List<AtivoDTO> ativosDTO = criaAtivosDTO(ativos);
        CarteiraDTO carteiraDTO = criaCarteiraDTO(carteiraAtualizada, clienteDTO, ativosDTO);

        return ResponseEntity.ok(carteiraDTO);
    }

    /**
     * Controller que chama o metodo responsavel por vender ativos de uma determinada carteira
     *
     * @param id    Identificador unico da carteira que contem o ativo a ser vendido
     * @param ativo Ativo de uma determinada carteira a ser vendido
     * @return Um dto de uma carteira ja consolidada sem os ativos vendidos
     * @throws AtivoNaoEncontradoException     Caso o ativo a ser vendido nao seja encontrado
     * @throws QuantidadeInsuficienteException Caso a quantidade de ativos a vender seja maior que a quantidade de ativos na carteira
     * @throws JsonProcessingException         Caso ocorra algum erro na serializacao ou desserializacao do json
     */
    @PostMapping("/{id}/vender")
    @CacheEvict(value = "carteiras", key = "#id")
    public ResponseEntity<CarteiraDTO> venderAtivo(@PathVariable Long id, @RequestBody Ativo ativo)
            throws AtivoNaoEncontradoException, QuantidadeInsuficienteException, JsonProcessingException {

        Carteira carteiraAtualizada = carteiraService.venderAtivo(id, ativo);

        Cliente cliente = carteiraAtualizada.getCliente();
        ClienteDTO clienteDTO = criaClienteDTO(cliente);

        List<Ativo> ativos = carteiraAtualizada.getAtivos();
        List<AtivoDTO> ativosDTO = criaAtivosDTO(ativos);
        CarteiraDTO carteiraDTO = criaCarteiraDTO(carteiraAtualizada, clienteDTO, ativosDTO);

        return ResponseEntity.ok(carteiraDTO);
    }

    /**
     * Metodo que retorna a lista de ativos comprados de uma determidada carteira
     *
     * @param id Identificador unico da carteira onde se deseja consultar os ativos comprados
     * @return Uma lista de dtos de ativos comprados
     */
    @GetMapping("/{id}/ativos-comprados")
    public ResponseEntity<List<AtivoCompradoDTO>> listarAtivosComprados(@PathVariable Long id) {
        List<AtivoComprado> ativosComprados = carteiraService.listarAtivosComprados(id);
        List<AtivoCompradoDTO> ativosCompradosDTO = new ArrayList<>();

        for (AtivoComprado ativoComprado : ativosComprados) {
            AtivoCompradoDTO dto = new AtivoCompradoDTO();
            dto.setId(ativoComprado.getId());
            dto.setTipo(ativoComprado.getTipo());
            dto.setTicker(ativoComprado.getTicker());
            dto.setQuantidade(BigDecimal.valueOf(ativoComprado.getQuantidade()));
            dto.setPrecoCompra(ativoComprado.getPrecoMedio());
            dto.setTotalCompra(ativoComprado.getTotalCompra());
            dto.setDataCompra(ativoComprado.getData().toLocalDate());
            ativosCompradosDTO.add(dto);
        }

        return ResponseEntity.ok(ativosCompradosDTO);
    }

    /**
     * Metodo que retorna a lista de ativos vendidos de uma determidada carteira
     *
     * @param id Identificador unico da carteira onde se deseja consultar os ativos vendidos
     * @return Uma lista de dtos de ativos vendidos
     */
    @GetMapping("/{id}/ativos-vendidos")
    public ResponseEntity<List<AtivoVendidoDTO>> listarAtivosVendidos(@PathVariable Long id) {
        List<AtivoVendido> ativosVendidos = carteiraService.listarAtivosVendidos(id);
        List<AtivoVendidoDTO> ativosVendidosDTO = new ArrayList<>();

        for (AtivoVendido ativoVendido : ativosVendidos) {
            AtivoVendidoDTO dto = new AtivoVendidoDTO();
            dto.setId(ativoVendido.getId());
            dto.setTipo(ativoVendido.getTipo());
            dto.setTicker(ativoVendido.getTicker());
            dto.setQuantidade(BigDecimal.valueOf(ativoVendido.getQuantidade()));
            dto.setPrecoVenda(ativoVendido.getPrecoVenda());
            dto.setTotalVenda(ativoVendido.getTotalVenda());
            dto.setDataVenda(ativoVendido.getData().toLocalDate());
            dto.setPrecoMedio(ativoVendido.getPrecoMedio());
            dto.setTotalPrecoMedio(ativoVendido.getTotalPrecoMedio());
            dto.setLucroPrejuizo(ativoVendido.getLucroPrejuizo());
            ativosVendidosDTO.add(dto);
        }

        return ResponseEntity.ok(ativosVendidosDTO);
    }

    /**
     * Metodo auxiliar que cria um dto de um objeto cliente que sera devovido no response dos requisicoes
     *
     * @param cliente Recebe um objeto cliente que sera convertido num dto de cliente
     * @return Um cliente dto que sera devolvido ao cliente da requisicao
     */
    private static ClienteDTO criaClienteDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf()
        );
    }

    /**
     * Metodo auxiliar que cria um dto de um objeto carteira que sera devovido no response dos requisicoes
     *
     * @param carteira   Carteira que sera devolvida ao cliente da requisicao
     * @param clienteDTO Dto do cliente que possui a carteira de ativos
     * @param ativosDTO  Dto dos ativos que compoe a carteira
     * @return Uma carteira dto somente com os dados que serao retornados ao cliente da requisicao
     */
    private static CarteiraDTO criaCarteiraDTO(Carteira carteira, ClienteDTO clienteDTO, List<AtivoDTO> ativosDTO) {
        return new CarteiraDTO(
                carteira.getId(),
                clienteDTO,
                ativosDTO,
                carteira.getSaldo(),
                carteira.getValorInvestido(),
                carteira.getLucroPrejuizo(),
                carteira.getTotalDaCarteira()
        );
    }

    /**
     * Metodo auxiliar que cria uma lista de dto dos ativos da carteira que serao retornados na requisicao do cliente
     *
     * @param ativos Lista de ativos que serao convertidos em ativos dtos
     * @return Uma lista de ativos dtos que serao retornados ao cliente da requisicao
     */
    private static List<AtivoDTO> criaAtivosDTO(List<Ativo> ativos) {
        List<AtivoDTO> ativosDTO = new ArrayList<>();

        for (Ativo ativoDaCompra : ativos) {
            AtivoDTO dto = new AtivoDTO();
            dto.setTipo(ativoDaCompra.getTipo());
            dto.setTicker(String.valueOf(ativoDaCompra.getTicker()));
            dto.setQuantidade(BigDecimal.valueOf(ativoDaCompra.getQuantidade()));
            dto.setPrecoMedio(ativoDaCompra.getPrecoMedio());
            dto.setTotalInvestido(ativoDaCompra.getTotalinvestido());
            dto.setPrecoAtual(ativoDaCompra.getPrecoAtual());
            dto.setVariacaoDiariaPreco(ativoDaCompra.getVariacaoDiariaPreco());
            dto.setVariacaoDiariaPercentual(ativoDaCompra.getVariacaoDiariaPercentual());
            dto.setTotalAtualizado(ativoDaCompra.getTotalAtualizado());
            dto.setGanhoPerdaTotal(ativoDaCompra.getGanhoPerdaTotal());
            dto.setGanhoPerdaPercentual(ativoDaCompra.getGanhoPerdaPercentual());
            dto.setGanhoPerdaDiaria(ativoDaCompra.getGanhoPerdaDiaria());
            ativosDTO.add(dto);
        }
        return ativosDTO;
    }
}