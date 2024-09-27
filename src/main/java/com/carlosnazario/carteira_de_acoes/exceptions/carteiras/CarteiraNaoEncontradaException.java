package com.carlosnazario.carteira_de_acoes.exceptions.carteiras;

public class CarteiraNaoEncontradaException extends RuntimeException {

    public CarteiraNaoEncontradaException(String message) {
        super(message);
    }

}