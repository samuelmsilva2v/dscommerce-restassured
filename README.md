# DScommerce REST Assured
[🇺🇸 Read in English](#dscommerce-rest-assured-1)

Projeto de estudo para testes de API com **REST Assured**, escritos sobre a API REST do [DScommerce](https://github.com/devsuperior/dscommerce) (curso DevSuperior). Não implementa regras de negócio — o objetivo é testar, na prática, uma API já existente: montar requisições, autenticar via OAuth2, validar status code e verificar o corpo da resposta com Hamcrest.

## Tecnologias
Java 21, Spring Boot 4.1.0, REST Assured 5.0.0, JUnit 5, Hamcrest, JSON Simple

## Pré-requisitos
Este projeto **não** contém a API testada. É preciso ter o [dscommerce](https://github.com/devsuperior/dscommerce) rodando em `http://localhost:8080` com o banco populado pelos dados de seed padrão.

| Perfil | Email | Senha |
|---|---|---|
| Cliente | maria@gmail.com | 123456 |
| Admin | alex@gmail.com | 123456 |

## Como executar
```bash
./mvnw test
```

## O que é testado
`ProductControllerRA` cobre o recurso `/products`: busca por ID, listagem paginada (com filtro por nome e por preço via Groovy), inserção (sucesso, validações de campo, acesso negado pra cliente, token inválido) e remoção (sucesso, não encontrado, conflito de FK, acesso negado, token inválido). Autenticação via OAuth2 (`/oauth2/token`, grant `password`), token reutilizado no header `Authorization: Bearer`.

---

# DScommerce REST Assured
[🇧🇷 Leia em Português](#dscommerce-rest-assured)

Study project for API testing with **REST Assured**, written on top of the [DScommerce](https://github.com/devsuperior/dscommerce) REST API (DevSuperior course). It doesn't implement business rules — the goal is to test, in practice, an already existing API: building requests, authenticating via OAuth2, validating status codes and checking the response body with Hamcrest.

## Technologies
Java 21, Spring Boot 4.1.0, REST Assured 5.0.0, JUnit 5, Hamcrest, JSON Simple

## Prerequisites
This project does **not** contain the API under test. You need [dscommerce](https://github.com/devsuperior/dscommerce) running at `http://localhost:8080` with the database populated with the default seed data.

| Profile | Email | Password |
|---|---|---|
| Client | maria@gmail.com | 123456 |
| Admin | alex@gmail.com | 123456 |

## How to run
```bash
./mvnw test
```

## What's tested
`ProductControllerRA` covers the `/products` resource: search by ID, paginated listing (with name filter and price filtering via Groovy), insertion (success, field validations, forbidden for client, invalid token) and deletion (success, not found, FK conflict, forbidden, invalid token). Authentication via OAuth2 (`/oauth2/token`, `password` grant), token reused in the `Authorization: Bearer` header.
