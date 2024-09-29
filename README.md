#### Carteira de Investimentos

**Uma aplicação web para gerenciar seus investimentos.**

**Tecnologias:** Spring Boot, H2, JWT, Maven

**Funcionalidades:**

* Cadastro de usuários com validação de dados.
* Criação e gerenciamento de carteiras de investimento.
* Integração com API de dados financeiros em tempo real.
* A segurança é garantida pela geração de tokens JWT e pela validação de usuários.
* A documentação Javadoc está disponível nos métodos principais.

**Como executar:**

1. **Pré-requisitos:** Java 11+, Maven, H2DataBase
2. **Clonar o repositório:** `git clone https://github.com/carlosNazario2010/carteira-investimentos.git`
3. **Configurar o banco de dados:** ...
4. **Executar a aplicação:** `mvn spring-boot:run`

### Exemplos de Requisições e Respostas


**Registrar um novo usuário:**

POST http://localhost:8080/autenticacao/registrar

Content-Type: application/json

Corpo da requisição: JSON contendo as informações do novo usuário (nome, CPF, email e senha).

```json

{
  "nome": "Carlos Nazario2",
  "cpf": "908.444.800-61",
  "email": "carlos@email.com",
  "senha": "minhaSenha123"
}
```

Resposta: JSON contendo o ID do usuário recém-criado e os dados cadastrados.

```json
{
  "id": 1,
  "nome": "Carlos Nazario2",
  "email": "carlos@email.com",
  "cpf": "908.444.800-61"
}
```


**Logar um usuario ja registrado:**

POST http://localhost:8080/autenticacao/logar

Content-Type: application/json

Corpo da requisição: JSON contendo o CPF e a senha do usuário.

```json
{
  "cpf": "908.444.800-61",
  "senha": "minhaSenha123"
}
```

Resposta JSON contendo o token JWT (JSON Web Token) de autenticação.

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRlbnRpY2FjYS1hcGkiLCJzdWIiOiI5MDguNDQ4LjgwMC02MSIsImV4cCI6MTcyNzU5NDA0OH0.hMe2WY1kYfv0ztbGpp0HDqkdAG1D9vB_ac2hJM3cy4s"
}
```

Este token deve ser incluído no header das próximas requisições da seguinte forma:

Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRlbnRpY2FjYS1hcGkiLCJzdWIiOiI5MDguNDQ4LjgwMC02MSIsImV4cCI6MTcyNzU5NDA0OH.hMe2WY1kYfv0ztbGpp0HDqkdAG1D9vB_ac2hJM3cy4s


**Criar uma carteira para um usuario ja registrado**

POST http://localhost:8080/carteiras

Content-Type: application/json

Corpo da requisição: JSON contendo o ID do cliente para o qual a carteira será criada.

```json
{
  "clienteId": 1
}
```

Resposta: JSON contendo os detalhes da carteira criada, incluindo o ID, o cliente associado
          e os valores iniciais (saldo, valor investido, lucro/prejuízo e total da carteira).

```json
{
  "id": 1,
  "cliente": {
    "id": 1,
    "nome": "Carlos Nazario2",
    "email": "carlos@email.com",
    "cpf": "908.444.800-61"
  },
  "ativo": [], // Array que será preenchido com os ativos da carteira
  "saldo": 0, // Saldo atual da carteira
  "valorInvestido": 0, // Valor total investido na carteira
  "lucroPrejuizo": 0, // Lucro ou prejuízo total da carteira
  "totalDaCarteira": 0 // Valor total da carteira (soma dos valores dos ativos)
}
```


**Adicionar um valor de saldo a carteira para realizar a compra de ativos**

POST http://localhost:8080/carteiras/1/adicionar

Content-Type: application/json

Parâmetro: {id} é o ID da carteira que será atualizada.
Corpo da requisição: JSON contendo o novo valor a ser adicionado ao saldo da carteira.

```json
{
  "novoSaldo": 20000.00
}
```

Resposta: JSON contendo os detalhes da carteira atualizada, incluindo o novo saldo.

```json
{
  "id": 1,
  "cliente": {
    "id": 1,
    "nome": "Carlos Nazario2",
    "email": "carlos@email.com",
    "cpf": "908.444.800-61"
  },
  "ativo": [],
  "saldo": 20000.00, // Novo saldo da carteira após a adição
  "valorInvestido": 0.00,
  "lucroPrejuizo": 0.00,
  "totalDaCarteira": 0.00
}
```

Observações:

O valor informado no campo novoSaldo é adicionado ao saldo existente da carteira.
Este endpoint pode ser utilizado para realizar depósitos na carteira.


**Comprar um ativo para a carteira**

POST http://localhost:8080/carteiras/1/comprar

Content-Type: application/json

Parâmetro: {id} é o ID da carteira onde o ativo será adicionado.
Corpo da requisição: JSON contendo os detalhes do ativo a ser comprado (ticker, quantidade, preço médio e tipo).

```json
{
  "ticker": "PETR3",
  "quantidade": 100,
  "precoMedio": 30.00,
  "tipo": "ACAO"
}
```

Resposta: JSON contendo os detalhes da carteira atualizada, incluindo o novo ativo adicionado e os cálculos de
saldo, valor investido, lucro/prejuízo e total da carteira.

```json
{
  "id": 1,
  "cliente": {
    "id": 1,
    "nome": "Carlos Nazario2",
    "email": "carlos@email.com",
    "cpf": "908.444.800-61"
  },
  "ativo": [
    {
      "tipo": "ACAO",
      "ticker": "PETR3",
      "quantidade": 100,
      "precoMedio": 30.00,
      "totalInvestido": 3000.00,
      "precoAtual": 39.52, // Obtido a partir de uma API externa de cotações.
      "totalAtualizado": 3952.00,
      "ganhoPerdaTotal": 952.00,
      "ganhoPerdaPercentual": 32.00,
      "variacaoDiariaPreco": -0.16, // Obtido a partir de uma API externa de cotações.
      "ganhoPerdaDiaria": -16.00,
      "variacaoDiariaPercentual": -0.40 // Obtido a partir de uma API externa de cotações.
    }
  ],
  "saldo": 17000.00,
  "valorInvestido": 3000.00,
  "lucroPrejuizo": 0.00,
  "totalDaCarteira": 20000.00
}
```

Observações:

Cálculos: A API calcula o valor total investido no ativo, o valor atual do ativo considerando o preço atual de mercado, o ganho ou perda total, o ganho ou perda percentual, a variação diária do preço e o ganho ou perda diária.

Array de ativos: O ativo comprado é adicionado ao array ativo da carteira.

Atualização de saldos: O saldo da carteira é atualizado com base no valor investido no ativo.


**Comprar um ativo que ja existe na carteira**

POST http://localhost:8080/carteiras/1/comprar

Content-Type: application/json

Parâmetro: {id} é o ID da carteira onde o ativo será adicionado.
Corpo da requisição: JSON contendo os detalhes do ativo a ser comprado (ticker, quantidade, preço médio e tipo).

```json
{
  "ticker": "PETR3",
  "quantidade": 100,
  "precoMedio": 5.00,
  "tipo": "ACAO"
}
```

Resposta: JSON contendo os detalhes da carteira atualizada, incluindo o novo ativo adicionado e os cálculos de saldo, valor investido, lucro/prejuízo e total da carteira.

```json
{
    "id": 1,
    "cliente": {
        "id": 1,
        "nome": "Carlos Nazario2",
        "email": "carlos@email.com",
        "cpf": "908.444.800-61"
    },
    "ativo": [
        {
            "tipo": "ACAO",
            "ticker": "PETR3",
            "quantidade": 200,
            "precoMedio": 17.50,
            "totalInvestido": 3500.00,
            "precoAtual": 39.52,
            "totalAtualizado": 7904.00,
            "ganhoPerdaTotal": 4404.00,
            "ganhoPerdaPercentual": 126.00,
            "variacaoDiariaPreco": -0.16,
            "ganhoPerdaDiaria": -32.00,
            "variacaoDiariaPercentual": -0.40
        }
    ],
    "saldo": 16500.00,
    "valorInvestido": 3500.00,
    "lucroPrejuizo": 0.00,
    "totalDaCarteira": 20000.00
}
```

Observações:

Calculo de valores consolidados: A API calcula os valores para consolidacao, caso o ativo ja exista na carteira,
como calculo de um novo preco medio e ajustes nos demais indicadores

Array de ativos: O ativo comprado é atualizado e consolidado na carteira


**Comprar um ativo que ainda nao existe na carteira**

POST http://localhost:8080/carteiras/1/comprar

Content-Type: application/json

Parâmetro: {id} é o ID da carteira onde o ativo será adicionado.
Corpo da requisição: JSON contendo os detalhes do ativo a ser comprado (ticker, quantidade, preço médio e tipo).

```json
{
  "ticker": "SAPR11",
  "quantidade": 100,
  "precoMedio": 10.00,
  "tipo": "ACAO"
}
```

Resposta: JSON contendo os detalhes da carteira atualizada, incluindo o novo ativo adicionado e os cálculos do saldo, valor investido, lucro/prejuízo e total da carteira.

```json
{
    "id": 1,
    "cliente": {
        "id": 1,
        "nome": "Carlos Nazario2",
        "email": "carlos@email.com",
        "cpf": "908.444.800-61"
    },
    "ativo": [
        {
            "tipo": "ACAO",
            "ticker": "PETR3",
            "quantidade": 200,
            "precoMedio": 17.50,
            "totalInvestido": 3500.00,
            "precoAtual": 39.52,
            "totalAtualizado": 7904.00,
            "ganhoPerdaTotal": 4404.00,
            "ganhoPerdaPercentual": 126.00,
            "variacaoDiariaPreco": -0.16,
            "ganhoPerdaDiaria": -32.00,
            "variacaoDiariaPercentual": -0.40
        },
        {
            "tipo": "ACAO",
            "ticker": "SAPR11",
            "quantidade": 100,
            "precoMedio": 10.00,
            "totalInvestido": 1000.00,
            "precoAtual": 29.4,
            "totalAtualizado": 2940.0,
            "ganhoPerdaTotal": 1940.00,
            "ganhoPerdaPercentual": 194.00,
            "variacaoDiariaPreco": -0.41,
            "ganhoPerdaDiaria": -41.00,
            "variacaoDiariaPercentual": -1.38
        }
    ],
    "saldo": 15500.00,
    "valorInvestido": 4500.00,
    "lucroPrejuizo": 0.00,
    "totalDaCarteira": 20000.00
}
```

Observações:

Múltiplos ativos: A API suporta múltiplos ativos por carteira. O preço médio de um ativo é calculado considerando todas as compras realizadas.

Atualização automática: Os valores da carteira são atualizados automaticamente após cada compra, incluindo saldo,valor investido, lucro/prejuízo e total da carteira.


**Venda de um ativo da carteira**

POST http://localhost:8080/carteiras/1/vender

Content-Type: application/json

Parâmetro: {id} é o ID da carteira onde o ativo será adicionado.
Corpo da requisição: JSON contendo os detalhes do ativo a ser comprado (ticker, quantidade, preço de venda e tipo).

```json
{
  "ticker": "PETR3",
  "quantidade": 100,
  "precoVenda": 30.00,
  "tipo": "ACAO"
}
```

Resposta: JSON contendo os detalhes da carteira atualizada, excluindo os ativos vendidos, juntamente com os cálculos de
          saldo, valor investido, lucro/prejuízo e total da carteira.

```json
{
    "id": 1,
    "cliente": {
        "id": 1,
        "nome": "Carlos Nazario2",
        "email": "carlos@email.com",
        "cpf": "908.444.800-61"
    },
    "ativo": [
        {
            "tipo": "ACAO",
            "ticker": "PETR3",
            "quantidade": 100,
            "precoMedio": 17.50,
            "totalInvestido": 1750.00,
            "precoAtual": 39.52,
            "totalAtualizado": 3952.00,
            "ganhoPerdaTotal": 2202.00,
            "ganhoPerdaPercentual": 126.00,
            "variacaoDiariaPreco": -0.16,
            "ganhoPerdaDiaria": -16.00,
            "variacaoDiariaPercentual": -0.40
        },
        {
            "tipo": "ACAO",
            "ticker": "SAPR11",
            "quantidade": 100,
            "precoMedio": 10.00,
            "totalInvestido": 1000.00,
            "precoAtual": 29.4,
            "totalAtualizado": 2940.00,
            "ganhoPerdaTotal": 1940.00,
            "ganhoPerdaPercentual": 194.00,
            "variacaoDiariaPreco": -0.41,
            "ganhoPerdaDiaria": -41.00,
            "variacaoDiariaPercentual": -1.38
        }
    ],
    "saldo": 18500.00,
    "valorInvestido": 2750.00,
    "lucroPrejuizo": 1250.00,
    "totalDaCarteira": 21250.00
}
```

Observações:

Remoção de ativos: A quantidade vendida é removida da quantidade total do ativo na carteira. Caso a quantidade vendida seja maior que a disponível, a operação será rejeitada.

Cálculo do lucro/prejuízo: O lucro ou prejuízo da operação é calculado pela diferença entre o valor total da venda (quantidade * preço de venda) e o valor total investido no ativo vendido.

Atualização de valores: Os valores da carteira são atualizados após cada venda, incluindo saldo, valor investido,lucro/prejuízo e total da carteira.


**Busca de uma carteira de um usuario**

GET http://localhost:8080/carteiras/1/1

Content-Type: application/json

Parâmetro: Primeiro {id} é o ID do cliente que possui a carteira e o segundo é o ID da carteira.

Resposta: JSON contendo os detalhes da carteira atualizada

```json
{
    "id": 1,
    "cliente": {
        "id": 1,
        "nome": "Carlos Nazario2",
        "email": "carlos@email.com",
        "cpf": "908.444.800-61"
    },
    "ativo": [
        {
            "tipo": "ACAO",
            "ticker": "PETR3",
            "quantidade": 100,
            "precoMedio": 17.50,
            "totalInvestido": 1750.00,
            "precoAtual": 39.52,
            "totalAtualizado": 3952.00,
            "ganhoPerdaTotal": 2202.00,
            "ganhoPerdaPercentual": 126.00,
            "variacaoDiariaPreco": -0.16,
            "ganhoPerdaDiaria": -16.00,
            "variacaoDiariaPercentual": -0.40
        },
        {
            "tipo": "ACAO",
            "ticker": "SAPR11",
            "quantidade": 100,
            "precoMedio": 10.00,
            "totalInvestido": 1000.00,
            "precoAtual": 29.4,
            "totalAtualizado": 2940.00,
            "ganhoPerdaTotal": 1940.00,
            "ganhoPerdaPercentual": 194.00,
            "variacaoDiariaPreco": -0.41,
            "ganhoPerdaDiaria": -41.00,
            "variacaoDiariaPercentual": -1.38
        }
    ],
    "saldo": 18500.00,
    "valorInvestido": 2750.00,
    "lucroPrejuizo": 1250.00,
    "totalDaCarteira": 21250.00
}
```

Observações:

Cache: Este endpoint é cacheado por 20 minutos ou até que uma compra ou venda seja realizada na carteira.


**Busca de todas as compras feitas em uma carteira**

GET http://localhost:8080/carteiras/1/ativos-comprados

Content-Type: application/json

Parâmetro: Primeiro {id} é o ID da carteira do cliente.

Resposta: JSON contendo a lista dos ativos comprados na carteira de um cliente

```json
[
    {
        "id": 1,
        "tipo": "ACAO",
        "ticker": "PETR3",
        "quantidade": 100,
        "precoCompra": 30.00,
        "totalCompra": 3000.00,
        "dataCompra": "2024-09-29"
    },
    {
        "id": 2,
        "tipo": "ACAO",
        "ticker": "PETR3",
        "quantidade": 100,
        "precoCompra": 5.00,
        "totalCompra": 500.00,
        "dataCompra": "2024-09-29"
    },
    {
        "id": 3,
        "tipo": "ACAO",
        "ticker": "SAPR11",
        "quantidade": 100,
        "precoCompra": 10.00,
        "totalCompra": 1000.00,
        "dataCompra": "2024-09-29"
    }
]
```


**Busca de todas as vendas feitas em uma carteira**

GET http://localhost:8080/carteiras/1/ativos-vendidos

Content-Type: application/json

Parâmetro: Primeiro {id} é o ID da carteira do cliente.

Resposta: JSON contendo a lista dos ativos vendidos na carteira de um cliente

```json
[
    {
        "id": 1,
        "tipo": "ACAO",
        "ticker": "PETR3",
        "quantidade": 100,
        "precoVenda": 30.00,
        "totalVenda": 3000.00,
        "dataVenda": "2024-09-29",
        "precoMedio": 17.50,
        "totalPrecoMedio": 1750.00,
        "lucroPrejuizo": 1250.00
    }
]
```

Observações:

Lucro ou Prejuizo: A API informa o lucro ou prejuizo da operacao realizada




**Contribuições:**

Se você quiser contribuir, por favor, abra um pull request.

**Licença:** MIT

**Arquitetura:**

**Padrões de projeto:** API Rest

