package com.carlosnazario.carteira_de_acoes.exceptions.carteiras;

public class SaldoInsuficienteException extends Throwable {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
