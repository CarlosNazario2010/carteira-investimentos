package com.carlosnazario.carteira_de_acoes.exceptions.carteiras;

public class AtivoNaoEncontradoException extends RuntimeException {

    public AtivoNaoEncontradoException(String message) {
        super(message);
    }

}