package com.carlosnazario.carteira_de_acoes.controllers;

import com.carlosnazario.carteira_de_acoes.api.BrapiClient;
import com.carlosnazario.carteira_de_acoes.api.Cotacao;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/cotacoes")
public class CotacaoController {

    @Autowired
    private BrapiClient brapiClient;

    /**
     * Obtém a cotação de um ativo financeiro.
     *
     * @param ticker Símbolo do ativo financeiro (exemplo: "AAPL").
     * @return ResponseEntity contendo a cotação do ativo, ou um status de erro em caso de falha.
     */
    @GetMapping("/{ticker}")
    public ResponseEntity<Cotacao> getCotacao(@PathVariable String ticker) {
        try {
            Cotacao cotacao = brapiClient.getCotacao(ticker);
            return ResponseEntity.ok(cotacao);
        } catch (JsonProcessingException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}