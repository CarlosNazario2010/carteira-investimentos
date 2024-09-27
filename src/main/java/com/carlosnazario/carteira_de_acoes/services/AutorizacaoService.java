package com.carlosnazario.carteira_de_acoes.services;

import com.carlosnazario.carteira_de_acoes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service responsável por fornecer os detalhes do usuário (UserDetails) para o Spring Security, com base no CPF do usuário.
 */
@Service
public class AutorizacaoService implements UserDetailsService {

    @Autowired
    ClienteRepository clienteRepository;

    /**
     * Metodo que busca o usuario para realizar o processo de autorizacao de acesso
     *
     * @param cpf Cpf do cliente utilizado para realizar o login ou o registro do usuario
     * @return Retornar um objeto UserDetails contendo as informações do usuário, incluindo a senha.
     * @throws UsernameNotFoundException Caso nao seja encontrado um cliente pelo seu cpf
     */
    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        UserDetails cliente = clienteRepository.findByCpf(cpf);
        if (cliente == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        // Crie um UserDetails com as informações do Cliente
        return new org.springframework.security.core.userdetails.User(
                cliente.getUsername(),
                cliente.getPassword(),
                Collections.emptyList() // Adicione as autoridades do usuário se necessário
        );
    }
}
