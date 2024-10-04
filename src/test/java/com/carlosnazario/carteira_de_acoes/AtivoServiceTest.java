//package com.carlosnazario.carteira_de_acoes;
//
//import com.carlosnazario.carteira_de_acoes.api.BrapiClient;
//import com.carlosnazario.carteira_de_acoes.api.Cotacao;
//import com.carlosnazario.carteira_de_acoes.enteties.Ativo;
//import com.carlosnazario.carteira_de_acoes.repositories.AtivoRepository;
//import com.carlosnazario.carteira_de_acoes.services.AtivoService;
//import com.carlosnazario.carteira_de_acoes.enums.Ticker;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class AtivoServiceTest {
//
//    @Autowired
//    private AtivoService ativoService;
//
//    @MockBean
//    private BrapiClient brapiClient;
//
//    @MockBean
//    private AtivoRepository ativoRepository;
//
//    @Test
//    public void deveCriarAtivoComSucesso() throws JsonProcessingException {
//        // Arrange
//        Ativo ativo = new Ativo();
//        ativo.setTicker(Ticker.PETR4);
//        ativo.setQuantidade(10);
//        ativo.setPrecoMedio(BigDecimal.valueOf(150));
//
//        Cotacao cotacao = new Cotacao();
//        cotacao.setPrecoAtual(BigDecimal.valueOf(160));
//        cotacao.setVariacaoDiariaPreco(BigDecimal.valueOf(10));
//        cotacao.setVariacaoDiariaPercentual(BigDecimal.valueOf(6.67));
//
//        when(brapiClient.getCotacao("PETR4")).thenReturn(cotacao);
//        when(ativoRepository.save(ativo)).thenReturn(ativo);
//
//        // Act
//        Ativo ativoCriado = ativoService.criarAtivo(ativo);
//
//        // Assert
//        assertEquals(BigDecimal.valueOf(160), ativoCriado.getPrecoAtual());
//        assertEquals(BigDecimal.valueOf(10), ativoCriado.getVariacaoDiariaPreco());
//        assertEquals(BigDecimal.valueOf(1500), ativoCriado.getTotalinvestido()); // 150 * 10
//        assertEquals(BigDecimal.valueOf(1600), ativoCriado.getTotalAtualizado()); // 160 * 10
//        assertEquals(BigDecimal.valueOf(100), ativoCriado.getGanhoPerdaTotal()); // 1600 - 1500
//        assertEquals(BigDecimal.valueOf(100), ativoCriado.getGanhoPerdaDiaria()); // 10 * 10
//    }
//}
