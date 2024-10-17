package com.carlosnazario.carteira_de_acoes.controllers;

import com.carlosnazario.carteira_de_acoes.dtos.ClienteDTO;
import com.carlosnazario.carteira_de_acoes.dtos.requests.AutenticacaoRequest;
import com.carlosnazario.carteira_de_acoes.dtos.responses.LoginResponseDTO;
import com.carlosnazario.carteira_de_acoes.enteties.Cliente;
import com.carlosnazario.carteira_de_acoes.repositories.ClienteRepository;
import com.carlosnazario.carteira_de_acoes.services.AutorizacaoService;
import com.carlosnazario.carteira_de_acoes.services.ClienteService;
import com.carlosnazario.carteira_de_acoes.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("autenticacao")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ClienteRepository repository;
    @Autowired
    ClienteService clienteService;
    @Autowired
    AutorizacaoService autorizacaoService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TokenService tokenService;

    /**
     * Endpoint para cadastrar um novo usuário.
     * <p>
     * Este método recebe um objeto "Cliente" no corpo da requisição e realiza as seguintes operações:
     * <p>
     * 1. Verifica se o CPF informado já existe no banco de dados.
     * 2. Se o CPF não existir, codifica a senha utilizando o algoritmo BCrypt.
     * 3. Cria um novo objeto "Cliente" com os dados informados e a senha codificada.
     * 4. Salva o novo cliente no banco de dados.
     * 5. Retorna um código de status 200 (OK) indicando sucesso.
     *
     * @param cliente O objeto "Cliente" contendo os dados do novo usuário.
     * @return ResponseEntity indicando o status da operação.
     */
    @PostMapping("/registrar")
    public ResponseEntity registrar(@RequestBody @Valid Cliente cliente) {
        if (this.repository.findByCpf(cliente.getCpf()) != null) return ResponseEntity.badRequest().build();

        String senhaEncriptada = passwordEncoder.encode(cliente.getSenha());

        Cliente clienteCriado = clienteService.criarCliente(new Cliente(
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail(),
                senhaEncriptada
        ));

        ClienteDTO clienteDTO = criaClienteDTO(clienteCriado);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteDTO);
    }

    /**
     * Endpoint para realizar o login de um usuário.
     * Este método recebe um objeto "AutenticacaoRequest" no corpo da requisição e realiza as seguintes operações:
     * 1. Cria um objeto "UsernamePasswordAuthenticationToken" com o CPF e senha fornecidos.
     * 2. Utiliza o "AuthenticationManager" para tentar autenticar o usuário.
     * 3. Se a autenticação for bem-sucedida, retorna um código de status 200 (OK) e o token gerado para realizar
     * novas chamadas para api.
     * 4. Se a autenticação falhar, retorna um status de nao autorizado com a mensagem de credenciais invalidas.
     *
     * @param cliente O objeto "AutenticacaoRequest" contendo CPF e senha do usuário.
     * @return ResponseEntity indicando o status da operação.
     */
    @PostMapping("/logar")
    public ResponseEntity logar(@RequestBody @Valid AutenticacaoRequest cliente) {
        try {
            UsernamePasswordAuthenticationToken usuarioSenha = new UsernamePasswordAuthenticationToken(cliente.getCpf(), cliente.getSenha());
            var autenticacao = this.authenticationManager.authenticate(usuarioSenha);
            UserDetails userDetails = autorizacaoService.loadUserByUsername(cliente.getCpf());
            var token = tokenService.geradorDeToken(userDetails);

            Cliente clienteRetornado = clienteService.buscarClientePorCpf(cliente.getCpf());
            Long id = clienteRetornado.getId();
            String nome = clienteRetornado.getNome();

            return ResponseEntity.ok(new LoginResponseDTO(token, id, nome));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }

    /**
     * Metodo auxiliar que retorna o dto de um cliente que sera repassado ao cliente da requisicao
     *
     * @param cliente Cliente que sera transformado em dto para ser passado na requisicao
     * @return Retorna um clienteDto que sera devolvido no response da requisicao
     */
    private static ClienteDTO criaClienteDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getCpf()
        );
    }
}
