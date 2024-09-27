package com.carlosnazario.carteira_de_acoes.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Interceptor que adiciona um cabeçalho de autorização (token de acesso) às requisições HTTP.
 */
public class AuthInterceptor implements ClientHttpRequestInterceptor {

    @Value("${brapi.api-key}")
    private final String apiKey;

    /**
     * Construtor do interceptor.
     *
     * @param apiKey A chave de API a ser usada para a autenticação.
     */
    public AuthInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Intercepta uma requisição HTTP e adiciona o cabeçalho de autorização.
     *
     * @param request   A requisição HTTP.
     * @param body      O corpo da requisição.
     * @param execution Execução da requisição.
     * @return A resposta da requisição.
     * @throws IOException Se ocorrer um erro durante a execução da requisição.
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("Authorization", "Bearer " + apiKey);
        return execution.execute(request, body);
    }
}
