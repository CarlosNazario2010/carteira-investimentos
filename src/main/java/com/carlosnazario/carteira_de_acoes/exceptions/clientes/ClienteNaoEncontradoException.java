package com.carlosnazario.carteira_de_acoes.exceptions.clientes;


public class ClienteNaoEncontradoException extends RuntimeException {
    public ClienteNaoEncontradoException(String message) {
        super(String.valueOf(message));
    }
}