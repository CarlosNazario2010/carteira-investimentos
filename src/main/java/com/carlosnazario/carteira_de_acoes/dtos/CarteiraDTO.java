package com.carlosnazario.carteira_de_acoes.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CarteiraDTO {
    private Long id;
    private ClienteDTO cliente;
    private List<AtivoDTO> ativo;
    private BigDecimal saldo;
    private BigDecimal valorInvestido;
    private BigDecimal lucroPrejuizo;
    private BigDecimal totalDaCarteira;
}