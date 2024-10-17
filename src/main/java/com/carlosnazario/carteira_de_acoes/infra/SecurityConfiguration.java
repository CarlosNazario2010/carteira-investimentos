package com.carlosnazario.carteira_de_acoes.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    SecurityFilter securityFilter;

    /**
     * Configura a cadeia de filtros de segurança da aplicação.
     *
     * Obs - metodos .cors() e .and() estao marcados como deprecated
     *
     * @param httpSecurity Objeto HttpSecurity utilizado para definir as configurações de segurança.
     * @return SecurityFilterChain contendo a cadeia de filtros de segurança.
     * @throws Exception Lançada caso haja erro ao configurar a segurança.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors()
                .and()
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST, "/autenticacao/logar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/autenticacao/registrar").permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Obtém o AuthenticationManager da configuração de autenticação do Spring Security.
     *
     * @param authenticationConfiguration Objeto AuthenticationConfiguration.
     * @return AuthenticationManager responsável por validar as credenciais do usuário.
     * @throws Exception Lançada caso haja erro ao obter o AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define o codificador de senha utilizado para armazenar as senhas dos usuários de forma segura.
     *
     * @return PasswordEncoder utilizado para codificar e decodificar as senhas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
