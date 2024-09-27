package com.carlosnazario.carteira_de_acoes.enteties;

import com.carlosnazario.carteira_de_acoes.enums.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "ativo_vendido")
@Table(name = "ativo_vendido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtivoVendido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    private String ticker;

    @NotNull
    @NotBlank
    private Integer quantidade;

    @NotNull
    @NotBlank
    private BigDecimal precoVenda;

    @NotNull
    @NotBlank
    private BigDecimal totalVenda;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;

    @NotNull
    @NotBlank
    LocalDateTime data = LocalDateTime.now();

    @NotNull
    @NotBlank
    private BigDecimal precoMedio;

    @NotNull
    @NotBlank
    private BigDecimal totalPrecoMedio;

    @NotNull
    @NotBlank
    private BigDecimal lucroPrejuizo;

}
