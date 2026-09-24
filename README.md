<div align="center">

# 🔐 Identity Microservice (`identity-ms`)

<p align="center">
  <b>Serviço central de autenticação, autorização e gerenciamento de identidades do ecossistema Barbearia.</b>
</p>

<!-- Badges de Tecnologias -->
<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot 4" />
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/JWT-Auth0_4.5.0-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/Flyway_Migration-CC0202?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway" />
  <img src="https://img.shields.io/badge/Netflix_Eureka_Client-Spring_Cloud-brightgreen?style=for-the-badge&logo=spring&logoColor=white" alt="Eureka" />
  <img src="https://img.shields.io/badge/OpenAPI_/_Swagger-v2.8.5-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger" />
</p>

</div>

---

## 📌 Visão Geral

O **`identity-ms`** é o microsserviço responsável pela segurança perimetral e gerenciamento de contas do ecossistema. Atuando como o provedor de identidade oficial da arquitetura, o serviço provê login seguro, registro de novos usuários, hashing de credenciais com BCrypt e assinatura/validação de tokens JWT.

O microsserviço é desenhado para operar de forma totalmente stateless e desacoplada, registrando-se dinamicamente no **Spring Cloud Netflix Eureka** para ser consumido e roteado através do **API Gateway**.

---

## 🛠️ Stack & Tecnologias

* **Linguagem & Framework:** Java 17 + Spring Boot 4.0.5 (MVC)
* **Segurança:** Spring Security + Auth0 Java JWT
* **Persistência de Dados:** Spring Data JPA + Hibernate + MySQL Connector/J
* **Migração e Versionamento:** Flyway Migration
* **Service Discovery:** Spring Cloud Netflix Eureka Client
* **Validação:** Bean Validation
* **Documentação de API:** SpringDoc OpenAPI 3 / Swagger UI
* **Testes:** jUnit e Mockito

---

## ✨ Funcionalidades Principais

- 🔑 **Autenticação Stateless (JWT):** Geração de tokens assinados contendo claims de identificação e perfis de acesso do usuário.
- 👥 **Gestão de Perfis (RBAC):** Controle de acesso baseado em roles.
- 🔒 **Criptografia de Senhas:** Hashing seguro utilizando `BCryptPasswordEncoder`.
- 🗄️ **Schema Versioning com Flyway:** Evolução rastreável do banco de dados relacional através de scripts versionados.
- 🚦 **Registro no Eureka:** Anúncio automático da instância e status de saúde para o cluster do Spring Cloud.
- 📖 **Contrato de API Interativo:** Swagger UI integrado para experimentação e documentação viva das rotas.
- 🧪 **Isolamento de Testes:** Suíte de testes automatizados.

---

## 🏗️ Estrutura Arquitetural

O serviço adota organização em camadas coesas e com separação clara de responsabilidades:

```text
identity-ms/
├── src/
│   ├── main/
│   │   ├── java/br/com/identity_ms/
│   │   │   ├── config/            # SecurityFilterChain
│   │   │   ├── controller/        # Endpoints 
│   │   │   ├── dto/               # Records e DTOs de entrada e saída
│   │   │   ├── model/             # Entidades JPA 
│   │   │   ├── exception/         # ExceptionHandler
│   │   │   ├── repository/        # Repositórios Spring Data JPA
│   │   │   └── service/           # Lógica de negócio 
│   │   └── resources/
│   │       ├── db/migration/      # Scripts SQL versionados 
│   │       └── application.properties    # Propriedades de runtime e conexões
│   └── test/
│       └── java/br/com/identity_ms/ # Testes unitários
│     
└── pom.xml
```

---

## 🔌 Endpoints da API

> 📚 Quando a aplicação e a infraestrutura estiverem ativas, a interface do Swagger pode ser acessada em:  
> `http://localhost:8080/webjars/swagger-ui/index.html` (pelo API Gateway).

---

## ⚙️ Variáveis de Ambiente Esperadas

As configurações gerenciadas no `application.properties` aceitam as seguintes variáveis de ambiente:

| Variável | Descrição | Valor Padrão / Exemplo |
| :--- | :--- | :--- |
| `SERVER_PORT` | Porta de execução do microsserviço | `8082` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL do servidor de Service Discovery (Eureka) | `http://localhost:8081/eureka` |
| `SPRING_DATASOURCE_URL` | URL de conexão JDBC com o MySQL | `jdbc:mysql://localhost:3306/identity_ms?createDatabaseIfNotExist=true` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco de dados | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados | `root` |
| `JWT_SECRET` | Chave secreta de assinatura HMAC-SHA256 (mín. 256 bits / 32 chars) | `SUA_CHAVE_JWT_SECRETA_LOCAL_COM_NO_MINIMO_32_CARACTERES` |
| `GOOGLE_CLIENT_ID` | Client ID registrado no Google Cloud Console | `seu_google_client_id` |
| `GOOGLE_CLIENT_SECRET` | Client Secret do aplicativo Google Cloud | `seu_google_client_secret` |
| `GITHUB_CLIENT_ID` | Client ID do GitHub OAuth App | `seu_github_client_id` |
| `GITHUB_CLIENT_SECRET` | Client Secret do GitHub OAuth App | `seu_github_client_secret` |
| `FRONTEND_URL` | URL base do cliente web para redirecionamento após autenticação | `http://localhost:3000/` |

---

## 🧪 Testes Automatizados

A suíte de testes de unidade e integração pode ser executada isoladamente sem a necessidade de dependências externas ativas:

```bash
./mvnw test
```

---
## ⚠️ Observações de Execução no Ecossistema

> **Atenção:** Este microsserviço integra uma arquitetura distribuída. Para o seu funcionamento pleno (comunicação entre serviços, descoberta de rotas e persistência de dados), é indispensável que o **`eureka-server`**, uma instância ativa do **MySQL** e os demais microsserviços dependentes estejam em execução.
>
> Para executar ou clonar o ecossistema completo com todas as dependências e submódulos configurados, acesse o repositório central:  
> 🔗 [**barbearia-network-main**](https://github.com/yamamotolucas/barbearia-network-main)
