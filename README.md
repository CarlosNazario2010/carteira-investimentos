## Carteira de Investimentos

**Primeira Versao de um projeto totalmente autoral usando Spring Boot, com a ajuda do LLM Google Gemini**

**Uma aplicação web para gerenciar seus investimentos.**

**Tecnologias:** Spring Boot, H2, JWT, Maven

**Funcionalidades:**

* Cadastro de usuários com validação de dados.
* Criação e gerenciamento de carteiras de investimento.
* Integração com API de dados financeiros em tempo real.
* Autenticação segura com JWT.
* Documentação Javadoc detalhada.

**Como executar:**

1. **Pré-requisitos:** Java 11+, Maven, H2DataBase
2. **Clonar o repositório:** `git clone https://github.com/carlosNazario2010/carteira-investimentos.git`
3. **Configurar o banco de dados:** ...
4. **Executar a aplicação:** `mvn spring-boot:run`

**Contribuições:**

Se você quiser contribuir, por favor, abra um pull request.

**Licença:** MIT

**Arquitetura:**

**Padrões de projeto:** API Rest

**Observações:**

* A aplicação utiliza o padrão MVC para separar as responsabilidades.
* A camada de serviço contém a lógica de negócio da aplicação.
* O repositório interage com o banco de dados.
* A segurança é garantida pela geração de tokens JWT e pela validação de usuários.
* A documentação Javadoc está disponível nos métodos principais.
