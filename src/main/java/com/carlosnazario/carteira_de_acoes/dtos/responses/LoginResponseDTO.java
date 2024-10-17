package com.carlosnazario.carteira_de_acoes.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long id;
    private String nome;
}
