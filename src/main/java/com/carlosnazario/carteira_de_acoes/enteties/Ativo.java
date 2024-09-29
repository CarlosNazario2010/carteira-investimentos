package com.carlosnazario.carteira_de_acoes.enteties;

import com.carlosnazario.carteira_de_acoes.enums.Ticker;
import com.carlosnazario.carteira_de_acoes.enums.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "ativo")
@Table(name = "ativo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Ticker ticker;

    @NotNull
    @NotBlank
    private Integer quantidade;

    @NotNull
    @NotBlank
    private BigDecimal totalinvestido;

    @NotNull
    @NotBlank
    private BigDecimal precoMedio;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;

    @NotNull
    @NotBlank
    private BigDecimal precoVenda;

    @NotNull
    @NotBlank
    LocalDateTime data = LocalDateTime.now();

    private BigDecimal precoAtual;
    private BigDecimal variacaoDiariaPreco;
    private BigDecimal variacaoDiariaPercentual;
    private BigDecimal totalAtualizado;
    private BigDecimal ganhoPerdaTotal;
    private BigDecimal ganhoPerdaPercentual;
    private BigDecimal ganhoPerdaDiaria;
}
