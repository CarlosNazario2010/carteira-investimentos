package com.carlosnazario.carteira_de_acoes.services;

import com.carlosnazario.carteira_de_acoes.enteties.Ativo;
import com.carlosnazario.carteira_de_acoes.enteties.AtivoComprado;
import com.carlosnazario.carteira_de_acoes.repositories.AtivoCompradoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AtivoCompradoService {
    @Autowired
    private AtivoCompradoRepository ativoCompradoRepository;

    /**
     * Cria um novo ativo comprado.
     * Este método calcula o valor total da compra com base no preço médio e na quantidade, e salva o ativo comprado no banco de dados.
     *
     * @param ativoComprado Objeto contendo os dados do ativo comprado a ser criado.
     */
    public void criarAtivoComprado(AtivoComprado ativoComprado) {
        BigDecimal valorTotalCompra = ativoComprado.getPrecoMedio().multiply(BigDecimal.valueOf(ativoComprado.getQuantidade()));
        ativoComprado.setTotalCompra(valorTotalCompra);
        ativoCompradoRepository.save(ativoComprado);
    }

    /**
     * Transforma um objeto Ativo num objeto AtivoComprado.
     * Este método copia as propriedades do objeto Ativo para um novo objeto AtivoComprado.
     *
     * @param ativo Objeto Ativo a ser transformado.
     * @return O objeto AtivoComprado criado a partir do objeto Ativo.
     */
    public AtivoComprado transformaEmAtivoComprado(Ativo ativo) {
        AtivoComprado ativoComprado = new AtivoComprado();

        ativoComprado.setId(ativo.getId());
        ativoComprado.setTicker(ativo.getTicker());
        ativoComprado.setQuantidade(ativo.getQuantidade());
        ativoComprado.setTotalCompra(ativo.getTotalinvestido());
        ativoComprado.setPrecoMedio(ativo.getPrecoMedio());
        ativoComprado.setTipo(ativo.getTipo());
        ativoComprado.setData(ativo.getData());

        return ativoComprado;
    }
}
