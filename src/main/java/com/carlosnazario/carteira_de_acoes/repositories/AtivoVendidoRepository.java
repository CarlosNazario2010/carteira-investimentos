package com.carlosnazario.carteira_de_acoes.repositories;

import com.carlosnazario.carteira_de_acoes.enteties.AtivoVendido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoVendidoRepository extends JpaRepository<AtivoVendido, Long> {
}
