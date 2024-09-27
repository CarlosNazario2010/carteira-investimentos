package com.carlosnazario.carteira_de_acoes.repositories;

import com.carlosnazario.carteira_de_acoes.enteties.AtivoComprado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoCompradoRepository extends JpaRepository<AtivoComprado, Long> {
}
