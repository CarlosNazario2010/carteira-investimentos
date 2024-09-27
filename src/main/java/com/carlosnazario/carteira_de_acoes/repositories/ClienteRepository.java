package com.carlosnazario.carteira_de_acoes.repositories;

import com.carlosnazario.carteira_de_acoes.enteties.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    UserDetails findByCpf(String cpf);
}
