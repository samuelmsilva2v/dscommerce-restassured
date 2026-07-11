# DScommerce REST Assured

Projeto de estudo para prática de testes de API com **REST Assured**, escritos em Java sobre a API REST do [DScommerce](https://github.com/devsuperior/dscommerce) (projeto da formação DevSuperior).

O objetivo aqui não é implementar regras de negócio, e sim testar, na prática, os endpoints de uma API já existente: montagem de requisições, autenticação via OAuth2, validação de status code e verificação do corpo da resposta em JSON usando os matchers do Hamcrest.

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- REST Assured 5.0.0
- JUnit 5
- Hamcrest (matchers)
- JSON Simple (montagem de corpo de requisição em JSON)
- Maven

## Estrutura do projeto

```
src/test/java/com/devsuperior/dscommerce
├── controllers/
│   └── ProductControllerRA.java   # Testes do recurso /products
└── tests/
    └── TokenUtil.java             # Utilitário para obtenção de token OAuth2
```

O módulo `main` contém apenas a aplicação Spring Boot mínima necessária para o projeto compilar e rodar os testes — toda a lógica de negócio é fornecida pela API alvo, que precisa estar em execução separadamente.

## Pré-requisitos

Este projeto **não** contém a API testada. Para rodar os testes é necessário ter a aplicação [dscommerce](https://github.com/devsuperior/dscommerce) rodando localmente em `http://localhost:8080`, com o banco de dados populado com os dados de seed padrão do projeto (usuários, produtos, categorias etc.).

Usuários usados nos testes (`TokenUtil` / `ProductControllerRA`):

| Perfil  | Email             | Senha  |
|---------|-------------------|--------|
| Cliente | maria@gmail.com   | 123456 |
| Admin   | alex@gmail.com    | 123456 |

## Como executar os testes

Com a API `dscommerce` rodando em `localhost:8080`, execute na raiz do projeto:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

## O que é testado

Os testes estão em `ProductControllerRA` e cobrem o recurso `/products`:

- **Busca por id**: retorno de produto existente com status 200 e validação dos campos (nome, preço, categorias etc.).
- **Listagem paginada**: busca sem filtro, com filtro por nome e com filtragem/verificação de preço via expressão Groovy sobre a resposta JSON.
- **Inserção de produto** (`POST /products`):
  - Sucesso com usuário admin autenticado (201).
  - Validações de campo: nome, descrição e preço inválidos (422).
  - Produto sem categoria (422).
  - Acesso negado para usuário cliente (403).
  - Token inválido (401).
- **Remoção de produto** (`DELETE /products/{id}`):
  - Sucesso com admin (204).
  - Id inexistente (404).
  - Id com dependência de integridade (400).
  - Acesso negado para cliente (403) e token inválido (401).

A autenticação é feita via OAuth2 (grant type `password`), obtendo o `access_token` através do endpoint `/oauth2/token` e reutilizando-o nos headers `Authorization: Bearer <token>` das requisições.

## Referência

- [REST Assured](https://rest-assured.io/)
- [DScommerce (API testada)](https://github.com/devsuperior/dscommerce)
- [DevSuperior](https://devsuperior.com.br/)
