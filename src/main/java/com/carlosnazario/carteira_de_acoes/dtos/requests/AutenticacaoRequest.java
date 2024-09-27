package com.carlosnazario.carteira_de_acoes.dtos.requests;

import lombok.Data;

@Data
public class AutenticacaoRequest {
    private String cpf;
    private String senha;
}
