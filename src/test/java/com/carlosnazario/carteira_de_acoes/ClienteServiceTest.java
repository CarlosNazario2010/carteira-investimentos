//package com.carlosnazario.carteira_de_acoes;
//
//import com.carlosnazario.carteira_de_acoes.enteties.Cliente;
//import com.carlosnazario.carteira_de_acoes.exceptions.clientes.EmailInvalidoException;
//import com.carlosnazario.carteira_de_acoes.repositories.ClienteRepository;
//import com.carlosnazario.carteira_de_acoes.services.ClienteService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//class ClienteServiceTest {
//
//    @Autowired
//    private ClienteService clienteService;
//
//    @MockBean
//    private ClienteRepository clienteRepository;
//
//    @Test
//    public void deveCriarClienteComSucesso() {
//        // Arrange
//        Cliente cliente = new Cliente();
//        cliente.setNome("João da Silva");
//        cliente.setEmail("joao@email.com");
//        cliente.setCpf("301.205.020-54");
//
//        when(clienteRepository.existsByEmail(cliente.getEmail())).thenReturn(false);
//        when(clienteRepository.existsByCpf(cliente.getCpf())).thenReturn(false);
//        when(clienteRepository.save(cliente)).thenReturn(cliente);
//
//        // Act
//        Cliente clienteCriado = clienteService.criarCliente(cliente);
//
//        // Assert
//        assertNotNull(clienteCriado);
//        assertEquals(cliente, clienteCriado);
//    }
//
//    @Test
//    public void deveLancarEmailInvalidoException() {
//        // Arrange
//        Cliente cliente = new Cliente();
//        cliente.setEmail("emailinvalido");
//
//        // Act & Assert
//        assertThrows(EmailInvalidoException.class, () -> clienteService.criarCliente(cliente));
//    }
//}
