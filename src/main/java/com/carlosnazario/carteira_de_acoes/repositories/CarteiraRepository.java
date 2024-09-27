package com.carlosnazario.carteira_de_acoes.repositories;

import com.carlosnazario.carteira_de_acoes.enteties.Carteira;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {
}