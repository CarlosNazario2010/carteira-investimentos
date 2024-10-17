package com.carlosnazario.carteira_de_acoes.services;

import com.carlosnazario.carteira_de_acoes.enteties.Cliente;
import com.carlosnazario.carteira_de_acoes.exceptions.clientes.*;
import com.carlosnazario.carteira_de_acoes.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.carlosnazario.carteira_de_acoes.utils.CPFValidator.isValidCPF;
import static com.carlosnazario.carteira_de_acoes.utils.EmailValidator.isValidEmail;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Cria um novo cliente, realizando validações de email e CPF.
     * Este método valida se o email e o CPF informados são válidos e únicos, e caso estejam, salva o novo cliente no banco de dados.
     *
     * @param cliente O objeto Cliente a ser criado.
     * @return O objeto Cliente salvo no banco de dados.
     * @throws EmailInvalidoException     Se o email informado for inválido.
     * @throws EmailJaCadastradoException Se o email informado já estiver cadastrado no sistema.
     * @throws CpfInvalidoException       Se o CPF informado for inválido.
     * @throws CpfJaCadastradoException   Se o CPF informado já estiver cadastrado no sistema.
     */
    public Cliente criarCliente(Cliente cliente) {

        // Validação do email
        if (!isValidEmail(cliente.getEmail())) {
            throw new EmailInvalidoException("Email inválido.");
        }

        // Validação de email duplicado
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new EmailJaCadastradoException("Email já cadastrado.");
        }

        // Validação de CPF
        if (!isValidCPF(cliente.getCpf())) {
            throw new CpfInvalidoException("CPF inválido.");
        }

        // Validação de CPF duplicado
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new CpfJaCadastradoException("CPF já cadastrado.");
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Recupera uma lista de todos os clientes cadastrados no sistema.
     *
     * @return Uma lista contendo todos os objetos Cliente cadastrados.
     */
    public List<Cliente> buscarTodosClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Recupera um cliente específico pelo seu identificador.
     *
     * @param id O identificador do cliente a ser recuperado.
     * @return Um Optional contendo o objeto Cliente encontrado, ou Optional.empty() caso o cliente não seja encontrado.
     * @throws ClienteNaoEncontradoException Se o cliente não for encontrado
     */
    public Optional<Cliente> buscarClientePorId(Long id) {
        return Optional.ofNullable(clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("cliente nao encontrado")));
    }

    /**
     * Atualiza os dados de um cliente existente.
     *
     * @param id                O identificador do cliente a ser atualizado.
     * @param clienteAtualizado Objeto Cliente contendo os novos dados.
     * @return O objeto Cliente atualizado e salvo no banco de dados.
     * @throws ClienteNaoEncontradoException Se o cliente a ser atualizado não for encontrado.
     */
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);

        if (clienteOptional.isPresent()) {
            Cliente clienteExistente = clienteOptional.get();
            clienteExistente.setNome(clienteAtualizado.getNome());
            clienteExistente.setEmail(clienteAtualizado.getEmail());
            clienteExistente.setSenha(clienteAtualizado.getSenha());
            return clienteRepository.save(clienteExistente);
        } else {
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        }
    }

    /**
     * Exclui um cliente do sistema.
     *
     * @param id O identificador do cliente a ser excluído.
     * @throws ClienteNaoEncontradoException Se o cliente a ser excluído não for encontrado.
     */
    public void deletarCliente(Long id) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);

        if (clienteOptional.isPresent()) {
            clienteRepository.deleteById(id);
        } else {
            throw new ClienteNaoEncontradoException("Cliente não encontrado");
        }
    }

    public Cliente buscarClientePorCpf(String cpf) {
        return clienteRepository.getClienteByCpf(cpf);
    }
}
