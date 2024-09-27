package com.carlosnazario.carteira_de_acoes.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cotacao {
    private String ticker;
    private BigDecimal precoAtual;
    private BigDecimal variacaoDiariaPreco;
    private BigDecimal variacaoDiariaPercentual;
}