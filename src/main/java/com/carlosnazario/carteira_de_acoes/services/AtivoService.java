package com.carlosnazario.carteira_de_acoes.services;

import com.carlosnazario.carteira_de_acoes.api.BrapiClient;
import com.carlosnazario.carteira_de_acoes.api.Cotacao;
import com.carlosnazario.carteira_de_acoes.enteties.Ativo;
import com.carlosnazario.carteira_de_acoes.repositories.AtivoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class AtivoService {
    @Autowired
    private AtivoRepository ativoRepository;
    @Autowired
    private BrapiClient brapiClient;

    /**
     * Cria um novo ativo, buscando informações em tempo real na API Brapi e realizando cálculos.
     * Este método busca as informações de cotação do ativo na API Brapi, atualiza os dados do ativo com os valores obtidos,
     * calcula diversas métricas financeiras (valor total, variação, ganho/perda) e salva o ativo no banco de dados.
     * **Importante:** Verifica se o valor total investido é diferente de zero antes de calcular a porcentagem de ganho ou perda para evitar divisões por zero.
     *
     * @param ativo O objeto Ativo a ser criado e atualizado.
     * @return O objeto Ativo salvo no banco de dados, com todas as informações atualizadas.
     * @throws JsonProcessingException Se ocorrer um erro ao processar a resposta da API Brapi.
     */
    public Ativo criarAtivo(Ativo ativo) throws JsonProcessingException {

        // Pega a cotacao com os dados em tempo real da api
        Cotacao cotacao = brapiClient.getCotacao(String.valueOf(ativo.getTicker()));

        // Pega os dados da api e setta os mesmos no ativo
        BigDecimal precoAtual = cotacao.getPrecoAtual();
        BigDecimal variacaoDiariaPreco = cotacao.getVariacaoDiariaPreco().round(MathContext.DECIMAL32);
        BigDecimal variacaoDiariaPercentual = cotacao.getVariacaoDiariaPercentual().setScale(2, RoundingMode.HALF_UP);

        ativo.setPrecoAtual(precoAtual);
        ativo.setVariacaoDiariaPreco(variacaoDiariaPreco);
        ativo.setVariacaoDiariaPercentual(variacaoDiariaPercentual);

        // Calcula o valor total da compra e setta no ativo
        BigDecimal valorTotalCompra = ativo.getPrecoMedio().multiply(BigDecimal.valueOf(ativo.getQuantidade()));
        ativo.setTotalinvestido(valorTotalCompra);

        // Calcula o valor total atualizado em tempo real e setta no ativo
        BigDecimal totalAtualizado = ativo.getPrecoAtual().multiply(BigDecimal.valueOf(ativo.getQuantidade()));
        ativo.setTotalAtualizado(totalAtualizado);

        // Calcula o ganho ou perda total e setta no ativo
        BigDecimal ganhoPerdaTotal = ativo.getTotalAtualizado().subtract(ativo.getTotalinvestido());
        ativo.setGanhoPerdaTotal(ganhoPerdaTotal);

        // Calcula o ganho ou perda diaria em tempo real e setta no ativo
        BigDecimal ganhoPerdaDiaria = ativo.getVariacaoDiariaPreco().multiply(BigDecimal.valueOf(ativo.getQuantidade()));
        ativo.setGanhoPerdaDiaria(ganhoPerdaDiaria);

        // Verifica se totalinvestido e diferente de zero antes da divisão. Ha um exception lancado caso essa
        //     verificacao nao seja realizada. Apos a verificacao setta o ganho ou perda percentual total no ativo
        if (ativo.getTotalinvestido().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal ganhoPerdaPercentual = ativo.getTotalAtualizado()
                    .divide(ativo.getTotalinvestido(), 2, RoundingMode.HALF_UP)
                    .subtract(BigDecimal.ONE)
                    .multiply(BigDecimal.valueOf(100));
            ativo.setGanhoPerdaPercentual(ganhoPerdaPercentual);

        } else {
            // Lidar com a situação em que totalinvestido é zero
            ativo.setGanhoPerdaPercentual(BigDecimal.ZERO);
        }

        return ativoRepository.save(ativo);
    }
}
