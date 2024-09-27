package com.carlosnazario.carteira_de_acoes.exceptions.clientes;

public class CpfJaCadastradoException extends RuntimeException {
    public CpfJaCadastradoException(String message) {
        super(message);
    }
}
