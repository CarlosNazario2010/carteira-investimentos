package com.carlosnazario.carteira_de_acoes.services;

import com.carlosnazario.carteira_de_acoes.enteties.Ativo;
import com.carlosnazario.carteira_de_acoes.enteties.AtivoVendido;
import com.carlosnazario.carteira_de_acoes.repositories.AtivoVendidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AtivoVendidoService {
    @Autowired
    private AtivoVendidoRepository ativoVendidoRepository;

    /**
     * Cria um novo ativo vendido, calculando métricas relevantes.
     * Este método calcula o valor total da venda, o valor total do preço médio e o lucro/prejuízo
     * com base nos dados fornecidos e salva o ativo vendido no banco de dados.
     *
     * @param ativoVendido O objeto AtivoVendido a ser criado.
     */
    public void criarAtivoVendido(AtivoVendido ativoVendido) {

        BigDecimal valorTotalVenda = ativoVendido.getPrecoVenda().multiply(BigDecimal.valueOf(ativoVendido.getQuantidade()));
        ativoVendido.setTotalVenda(valorTotalVenda);

        BigDecimal valotTotalPrecoMedio = ativoVendido.getPrecoMedio().multiply(BigDecimal.valueOf(ativoVendido.getQuantidade()));
        ativoVendido.setTotalPrecoMedio(valotTotalPrecoMedio);

        BigDecimal lucroPrejuizo = valorTotalVenda.subtract(valotTotalPrecoMedio);
        ativoVendido.setLucroPrejuizo(lucroPrejuizo);

        ativoVendidoRepository.save(ativoVendido);
    }

    /**
     * Transforma um objeto Ativo num objeto AtivoVendido.
     * Este método copia as propriedades relevantes do objeto Ativo para um novo objeto AtivoVendido.
     *
     * @param ativo O objeto Ativo a ser transformado.
     * @return O objeto AtivoVendido criado a partir do objeto Ativo.
     */
    public AtivoVendido transformaEmAtivoVendido(Ativo ativo) {
        AtivoVendido ativoVendido = new AtivoVendido();

        ativoVendido.setId(ativo.getId());
        ativoVendido.setTicker(ativo.getTicker());
        ativoVendido.setQuantidade(ativo.getQuantidade());
        ativoVendido.setPrecoVenda(ativo.getPrecoVenda());
        ativoVendido.setTipo(ativo.getTipo());
        ativoVendido.setData(ativo.getData());
        ativoVendido.setPrecoMedio(ativo.getPrecoMedio());

        return ativoVendido;
    }
}
