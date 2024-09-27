package com.carlosnazario.carteira_de_acoes.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.carlosnazario.carteira_de_acoes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Service responsável por gerar e validar tokens JWT.
 */
@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    private final ClienteRepository clienteRepository;

    public TokenService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Gera um token JWT para o usuário fornecido.
     *
     * @param cliente Objeto UserDetails representando o usuário.
     * @return String contendo o token JWT gerado.
     * @throws RuntimeException Lançada caso haja erro ao gerar o token.
     */
    public String geradorDeToken(UserDetails cliente) {
        try {
            String cpf = cliente.getUsername();
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("autenticaca-api")
                    .withSubject(cpf)
                    .withExpiresAt(geradorDataDeExpiracao())
                    .sign(algoritmo);
            return token;
        }catch(JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar o token", exception);
        }
    }

    /**
     * Valida um token JWT e retorna o CPF do usuário contido no payload.
     *
     * @param token String contendo o token JWT a ser validado.
     * @return String contendo o CPF do usuário extraído do token, ou null caso o token seja inválido.
     */
    public String validadorDeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); // Substitua 'secret' pela sua chave secreta
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("autenticaca-api")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("sub").asString();
        } catch (JWTVerificationException exception) {
            // Lidar com exceções, como token inválido
            return null;
        }
    }

    /**
     * Gera a data de expiração do token, com duas horas de validade a partir do momento atual.
     * Considera o timezone UTC-3 (Horário de Brasília).
     *
     * @return Instant representando a data de expiração do token.
     */
    private Instant geradorDataDeExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
