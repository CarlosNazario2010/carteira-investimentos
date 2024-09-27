package com.carlosnazario.carteira_de_acoes.dtos.requests;

import lombok.Getter;

@Getter
public class BuscarCarteiraRequest {
    private Long clienteId;
    private Long carteiraId;
}