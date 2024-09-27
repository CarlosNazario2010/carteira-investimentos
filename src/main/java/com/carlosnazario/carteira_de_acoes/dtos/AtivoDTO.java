package com.carlosnazario.carteira_de_acoes.dtos;

import com.carlosnazario.carteira_de_acoes.enums.TipoAtivo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtivoDTO {
    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;
    private String ticker;
    private BigDecimal quantidade;
    private BigDecimal precoMedio;
    private BigDecimal totalInvestido;
    private BigDecimal precoAtual;
    private BigDecimal totalAtualizado;
    private BigDecimal ganhoPerdaTotal;
    private BigDecimal ganhoPerdaPercentual;
    private BigDecimal variacaoDiariaPreco;
    private BigDecimal ganhoPerdaDiaria;
    private BigDecimal variacaoDiariaPercentual;
}