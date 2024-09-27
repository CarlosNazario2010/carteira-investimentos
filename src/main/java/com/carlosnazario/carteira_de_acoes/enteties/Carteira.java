package com.carlosnazario.carteira_de_acoes.enteties;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "carteira")
@Table(name = "carteira")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany
    @JoinColumn(name = "carteira_id")
    private List<Ativo> ativos = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "carteira_id")
    private List<AtivoComprado> ativosComprados = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "carteira_id")
    private List<AtivoVendido> ativosVendidos = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorInvestido = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal lucroPrejuizo = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalDaCarteira = BigDecimal.ZERO;

    public List<Ativo> adicionarAtivo(Ativo ativo) {
        ativos.add(ativo);
        return ativos;
    }
}