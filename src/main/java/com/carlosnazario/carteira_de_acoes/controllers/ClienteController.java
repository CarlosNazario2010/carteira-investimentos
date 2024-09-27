package com.carlosnazario.carteira_de_acoes.controllers;

import com.carlosnazario.carteira_de_acoes.dtos.ClienteDTO;
import com.carlosnazario.carteira_de_acoes.enteties.Cliente;
import com.carlosnazario.carteira_de_acoes.exceptions.clientes.ClienteNaoEncontradoException;
import com.carlosnazario.carteira_de_acoes.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Cria um novo cliente.
     *
     * @param cliente Objeto contendo os dados do cliente a ser criado. O corpo da requisição deve conter um JSON válido representando o objeto Cliente.
     * @return ResponseEntity contendo o cliente recém-criado (com o ID gerado) no formato DTO e status CREATED (201).
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> criarCliente(@Valid @RequestBody Cliente cliente) {
        Cliente clienteSalvo = clienteService.criarCliente(cliente);
        ClienteDTO clienteDTO = criaClienteDTO(clienteSalvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteDTO);
    }

    /**
     * Recupera uma lista de todos os clientes cadastrados no sistema.
     *
     * @return ResponseEntity contendo uma lista de objetos ClienteDTO representando os clientes cadastrados e o status OK (200).
     */
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> buscarTodosClientes() {
        List<Cliente> clientes = clienteService.buscarTodosClientes();

        List<ClienteDTO> clientesDTO = clientes.stream()
                .map(cliente -> new ClienteDTO(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getEmail(),
                        cliente.getNome()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientesDTO);
    }

    /**
     * Recupera um cliente específico pelo seu identificador.
     *
     * @param id Identificador do cliente a ser recuperado.
     * @return ResponseEntity contendo o cliente encontrado no formato DTO e o status OK (200), ou um status NOT_FOUND (404) caso o cliente não seja encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarClientePorId(@PathVariable Long id) {
        Optional<Cliente> clienteOptional = clienteService.buscarClientePorId(id);
        return clienteOptional
                .map(cliente -> ResponseEntity.ok(new ClienteDTO(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getEmail(),
                        cliente.getCpf())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza os dados de um cliente existente.
     *
     * @param id      Identificador do cliente a ser atualizado.
     * @param cliente Objeto contendo os novos dados do cliente. O corpo da requisição deve conter um JSON válido representando o objeto Cliente.
     * @return ResponseEntity contendo o cliente atualizado no formato DTO e o status OK (200), ou um status NOT_FOUND (404) caso o cliente não seja encontrado.
     * @throws ClienteNaoEncontradoException Se o cliente a ser atualizado não for encontrado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        try {
            Cliente clienteAtualizado = clienteService.atualizarCliente(id, cliente);
            ClienteDTO clienteDTO = criaClienteDTO(clienteAtualizado);
            return ResponseEntity.ok(clienteDTO);

        } catch (ClienteNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Exclui um cliente do sistema.
     *
     * @param id Identificador do cliente a ser excluído.
     * @return ResponseEntity com status NO_CONTENT (204) caso o cliente seja excluído com sucesso, ou um status NOT_FOUND (404) caso o cliente não seja encontrado.
     * @throws ClienteNaoEncontradoException Se o cliente a ser excluído não for encontrado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (ClienteNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private static ClienteDTO criaClienteDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf()
        );
    }
}