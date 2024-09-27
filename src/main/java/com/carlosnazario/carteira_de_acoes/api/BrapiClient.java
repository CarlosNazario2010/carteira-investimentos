package com.carlosnazario.carteira_de_acoes.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Cliente para interagir com a API Brapi.
 * Esta classe utiliza o RestTemplate para realizar chamadas HTTP à API Brapi e mapeia as respostas JSON para objetos Java.
 */
@Component
public class BrapiClient {

    @Autowired
    private ObjectMapper objectMapper;
    @Value("${brapi.base-url}")
    private String baseUrl;
    @Value("${brapi.api-key}")   // apiKey vence a cada 30 dias, acessar https://brapi.dev/dashboard
    private String apiKey;
    @Autowired
    private final RestTemplate restTemplate;

    public BrapiClient(RestTemplate restTemplate, AuthInterceptor authInterceptor) {
        this.restTemplate = restTemplate;
        this.restTemplate.getInterceptors().add(authInterceptor);
    }

    /**
     * Recupera a cotação de um ativo financeiro.
     * Este método realiza uma chamada GET à API Brapi para obter a cotação do ativo identificado pelo ticker informado.
     *
     * @param ticker Símbolo do ativo financeiro (exemplo: "PETR3").
     * @return Objeto "Cotacao" contendo os dados da cotação, ou null caso a cotação não seja encontrada.
     * @throws JsonProcessingException Se ocorrer um erro durante a conversão da resposta JSON para um objeto Java.
     */
    public Cotacao getCotacao(String ticker) throws JsonProcessingException {
        String url = baseUrl + "/quote/" + ticker;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Converte a resposta JSON para um mapa, permitindo acessar os dados por chave
        Map mapaResposta = objectMapper.readValue(response.getBody(), Map.class);

        // Extrai os atributos com tratamento de valores nulos
        String simbolo = null;
        BigDecimal precoAtual = null;
        BigDecimal variacaoDiariaPreco = null;
        BigDecimal variacaoDiariaPercentual = null;

        // Verifica se "results" é uma lista e trata o erro
        Object resultsObject = mapaResposta.get("results");
        if (resultsObject instanceof List && !((List) resultsObject).isEmpty()) {
            Object primeiroElemento = ((List) resultsObject).get(0);
            if (primeiroElemento instanceof Map) {
                Map<String, Object> primeiroResultado = (Map<String, Object>) primeiroElemento;
                simbolo = (String) primeiroResultado.get("symbol");
                precoAtual = BigDecimal.valueOf((Double) primeiroResultado.get("regularMarketPrice"));

                // Verifica o tipo de regularMarketChange e converte corretamente
                Object valorRegularMarketChange = primeiroResultado.get("regularMarketChange");
                if (valorRegularMarketChange instanceof Integer) {
                    variacaoDiariaPreco = BigDecimal.valueOf((Integer) valorRegularMarketChange);
                } else if (valorRegularMarketChange instanceof Double) {
                    variacaoDiariaPreco = BigDecimal.valueOf((Double) valorRegularMarketChange);
                } else {
                    System.err.println("Tipo de dado inesperado para regularMarketChange: " + valorRegularMarketChange.getClass());
                }

                // Verifica o tipo de regularMarketChangePercent e converte corretamente
                Object valorRegularMarketChangePercent = primeiroResultado.get("regularMarketChangePercent");
                if (valorRegularMarketChangePercent instanceof Integer) {
                    variacaoDiariaPercentual = BigDecimal.valueOf((Integer) valorRegularMarketChangePercent);
                } else if (valorRegularMarketChangePercent instanceof Double) {
                    variacaoDiariaPercentual = BigDecimal.valueOf((Double) valorRegularMarketChangePercent);
                } else {
                    System.err.println("Tipo de dado inesperado para regularMarketChangePercent: " + valorRegularMarketChangePercent.getClass());
                }
            } else {
                System.err.println("O primeiro elemento da lista 'results' não é um mapa");
            }
        } else {
            System.err.println("A lista 'results' está vazia ou não é uma lista");
        }

        // Cria um objeto Cotacao com os dados extraídos (opcional)
        return new Cotacao(simbolo, precoAtual, variacaoDiariaPreco, variacaoDiariaPercentual);
    }
}
