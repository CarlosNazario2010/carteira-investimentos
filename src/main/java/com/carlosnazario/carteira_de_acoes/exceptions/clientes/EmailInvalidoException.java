package com.carlosnazario.carteira_de_acoes.exceptions.clientes;

public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}
