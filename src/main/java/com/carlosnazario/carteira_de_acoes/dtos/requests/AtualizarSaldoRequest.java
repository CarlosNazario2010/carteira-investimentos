package com.carlosnazario.carteira_de_acoes.dtos.requests;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AtualizarSaldoRequest {
    private BigDecimal novoSaldo;
}
