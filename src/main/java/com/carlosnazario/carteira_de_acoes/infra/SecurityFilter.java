package com.carlosnazario.carteira_de_acoes.infra;

import com.carlosnazario.carteira_de_acoes.repositories.ClienteRepository;
import com.carlosnazario.carteira_de_acoes.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    TokenService tokenService;

    /**
     * Este método é o ponto de entrada do filtro de segurança. Ele é chamado para cada requisição HTTP e é responsável por:
     * Extrair o token JWT do header Authorization.
     * Validar o token e obter o CPF do usuário.
     * Verificar se o usuário existe no banco de dados.
     * Configurar o contexto de segurança com o usuário autenticado.
     * Chamar o próximo filtro na cadeia de filtros.
     *
     * @param request     Objeto HttpServletRequest representando a requisição HTTP.
     * @param response    Objeto HttpServletResponse representando a resposta HTTP.
     * @param filterChain Objeto FilterChain representando a cadeia de filtros de segurança.
     * @throws ServletException Erro de Servlet
     * @throws IOException      Erro de IO
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token = this.recuperaToken(request);

        if (token != null) {
            var login = tokenService.validadorDeToken(token);
            UserDetails userDetails = clienteRepository.findByCpf(login);

            // Verifica se o usuário foi encontrado
            if (userDetails == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                try (OutputStream os = response.getOutputStream()) {
                    byte[] output = "{\"message\":\"token invalido\"}".getBytes(StandardCharsets.UTF_8);
                    os.write(output);
                }
                return;
            }

            var autenticacao = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(autenticacao);

        }

        filterChain.doFilter(request, response);
    }

    /**
     * Recupera o token JWT do header Authorization da requisição.
     *
     * @param request Objeto HttpServletRequest representando a requisição.
     * @return String contendo o token JWT, ou null se o token não estiver presente no header.
     */
    private String recuperaToken(HttpServletRequest request) {
        var header = request.getHeader("Authorization");
        if (header == null) return null;
        return header.replace("Bearer ", "");
    }
}
