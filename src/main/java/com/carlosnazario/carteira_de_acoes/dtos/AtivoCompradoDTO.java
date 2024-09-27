package com.carlosnazario.carteira_de_acoes.dtos;

import com.carlosnazario.carteira_de_acoes.enums.TipoAtivo;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtivoCompradoDTO {
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;
    private String ticker;
    private BigDecimal quantidade;
    private BigDecimal precoCompra;
    private BigDecimal totalCompra;
    private LocalDate dataCompra;
}
