package com.carlosnazario.carteira_de_acoes.dtos;

import com.carlosnazario.carteira_de_acoes.enums.TipoAtivo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtivoVendidoDTO {
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;
    private String ticker;
    private BigDecimal quantidade;
    private BigDecimal precoVenda;
    private BigDecimal totalVenda;
    private LocalDate dataVenda;
    private BigDecimal precoMedio;
    private BigDecimal totalPrecoMedio;
    private BigDecimal lucroPrejuizo;
}
