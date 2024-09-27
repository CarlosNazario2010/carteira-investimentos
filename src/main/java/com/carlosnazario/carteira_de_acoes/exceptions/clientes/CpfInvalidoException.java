package com.carlosnazario.carteira_de_acoes.exceptions.clientes;

public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException(String message) {
        super(message);
    }
}
