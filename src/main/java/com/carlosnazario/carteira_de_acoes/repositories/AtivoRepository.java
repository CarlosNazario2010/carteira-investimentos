package com.carlosnazario.carteira_de_acoes.repositories;

import com.carlosnazario.carteira_de_acoes.enteties.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoRepository extends JpaRepository<Ativo, Long> {
}
