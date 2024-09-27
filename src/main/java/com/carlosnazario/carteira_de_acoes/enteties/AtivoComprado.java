package com.carlosnazario.carteira_de_acoes.enteties;

import com.carlosnazario.carteira_de_acoes.enums.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "ativo_comprado")
@Table(name = "ativo_comprado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtivoComprado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;

    @NotNull
    @NotBlank
    private String ticker;

    @NotNull
    @NotBlank
    private Integer quantidade;

    @NotNull
    @NotBlank
    private BigDecimal totalCompra;

    @NotNull
    @NotBlank
    private BigDecimal precoMedio;

    @NotNull
    @NotBlank
    LocalDateTime data = LocalDateTime.now();

}
