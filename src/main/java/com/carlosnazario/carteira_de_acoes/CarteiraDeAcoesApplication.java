package com.carlosnazario.carteira_de_acoes;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class CarteiraDeAcoesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarteiraDeAcoesApplication.class, args);
    }

}
