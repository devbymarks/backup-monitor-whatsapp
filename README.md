# 📦 Monitor Backup WhatsApp

> Sistema de monitoramento automático de backups em servidores Linux, desenvolvido em **Java 8 + Spring Boot 2.7.18**, empacotado como **WAR** para execução no **Apache Tomcat 9** e integrado à **WhatsApp Cloud API da Meta**.

O sistema verifica os backups configurados, identifica arquivos ausentes ou atrasados e envia um alerta consolidado pelo WhatsApp, reduzindo a necessidade de conferências manuais.

---

## 🎯 Objetivo

Transformar uma rotina operacional de conferência de backups em um processo automático.

O sistema:

1. verifica os diretórios configurados;
2. identifica o backup válido mais recente;
3. compara a idade do backup com o limite definido;
4. classifica o cliente como `OK`, `ATRASADO` ou `SEM_BACKUP`;
5. consolida os problemas em uma única mensagem;
6. envia o alerta pelo WhatsApp;
7. registra o estado do alerta diário para evitar duplicidade.

---

## ⭐ Principais funcionalidades

- 🔎 Monitoramento automático de múltiplos clientes.
- 📁 Leitura dos diretórios de backup do Linux.
- 🎯 Filtro por padrão de arquivo (`glob`).
- ⏱️ Limite de backup configurável.
- 🟢 Status `OK`.
- 🟠 Status `ATRASADO`.
- 🔴 Status `SEM_BACKUP`.
- 📱 Integração com WhatsApp Cloud API.
- 📨 Alerta consolidado.
- 🛑 Limite de uma tentativa de alerta por dia.
- 💾 Persistência do estado do alerta.
- 🔄 Continuidade após reinicialização.
- 🌎 Fuso horário configurável.
- 🌐 API REST para consulta e execução manual.
- 🧪 Testes automatizados com JUnit e Mockito.
- 🔐 Credenciais fora do código-fonte.
- ⚙️ Clientes configuráveis por ambiente.
- 📦 Empacotamento WAR para Tomcat 9.

---

## 🧱 Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 8 | Linguagem/runtime |
| Spring Boot 2.7.18 | Framework |
| Spring Web | API REST |
| Spring Scheduler | Execução automática |
| Maven | Build e dependências |
| Apache Tomcat 9 | Servidor de aplicação |
| WhatsApp Cloud API | Alertas |
| JUnit 5 | Testes |
| Mockito | Mocks |
| Linux | Ambiente operacional |

### Compatibilidade

O projeto permanece em Java 8 por compatibilidade com ambientes legados.

Por utilizar Spring Boot 2.7.x e `javax.*`, o WAR deve ser executado em Tomcat 9.x.

---

# 📂 Estrutura do projeto

```text
monitor-backup-whatsapp/
├── .gitignore
├── README.md
├── monitor-backup.env.example
├── pom.xml
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── br/com/monitorbackup/
    │   │       ├── MonitorBackupApplication.java
    │   │       ├── config/
    │   │       ├── controller/
    │   │       ├── model/
    │   │       └── service/
    │   │
    │   └── resources/
    │       └── application.properties
    │
    └── test/
        └── java/
            └── br/com/monitorbackup/
                └── service/
                    ├── AlertStateServiceTest.java
                    ├── BackupScannerServiceTest.java
                    └── MonitorServiceTest.java
```

A organização segue o padrão Maven/Spring Boot e separa configuração, API, modelos, regras de negócio, integração externa e testes.

---

# 🔐 Segurança

## A chave da API não fica no projeto

O token do WhatsApp deve ser fornecido por variável de ambiente:

```bash
WHATSAPP_TOKEN='SEU_ACCESS_TOKEN'
```

Também são configurados por ambiente:

```text
WHATSAPP_PHONE_NUMBER_ID
WHATSAPP_TO
WHATSAPP_GRAPH_VERSION
WHATSAPP_MESSAGE_MODE
WHATSAPP_TEMPLATE_NAME
WHATSAPP_TEMPLATE_LANGUAGE
```

Nunca coloque credenciais reais no código, no `application.properties` versionado ou no GitHub.

O arquivo `monitor-backup.env.example` contém somente placeholders.

---

# ⚙️ Configuração dos clientes

Os clientes não ficam definidos no código Java.

Use:

```bash
MONITOR_CLIENTS='nome-do-cliente:padrao-do-backup'
```

Formato:

```text
nome-do-cliente:padrao-do-backup
```

Exemplo genérico:

```text
cliente-a:*_0_L0
```

O projeto não contém nomes de clientes, servidores ou dados operacionais reais.

---

# ⏱️ Regra de monitoramento

Por padrão:

```text
menos de 24 horas → OK
24 horas ou mais  → ATRASADO
nenhum arquivo    → SEM_BACKUP
```

O limite pode ser alterado por:

```bash
MONITOR_LIMIT_HOURS=24
```

---

# 📱 Alertas pelo WhatsApp

Quando existem problemas, o sistema consolida os clientes em uma única mensagem.

O envio utiliza a WhatsApp Cloud API da Meta e pode trabalhar com texto ou template, conforme a configuração do ambiente.

---

# 🛑 Controle de uma mensagem por dia

O Scheduler pode executar várias vezes durante o dia, mas o sistema mantém o estado do alerta em arquivo.

Fluxo:

```text
Monitoramento
     │
     ▼
Existem problemas?
     │
   ┌─┴─┐
   NÃO SIM
    │   │
    │   ▼
    │ Já alertou hoje?
    │   │
    │ ┌─┴─┐
    │ SIM NÃO
    │  │   │
    │  │   ▼
    │  │ Reserva estado
    │  │   │
    │  │   ▼
    │  │ WhatsApp
    ▼  ▼
   Fim
```

O estado é registrado antes da chamada externa para evitar duplicidade.

---

# 🔄 Agendamento automático

Por padrão:

```text
intervalo: 30 minutos
primeira execução: 1 minuto após a inicialização
```

Configuração:

```bash
MONITOR_INTERVAL_MS=1800000
MONITOR_INITIAL_DELAY_MS=60000
```

---

# 🌐 API REST

### Health Check

```http
GET /api/health
```

### Consultar todos os clientes

```http
GET /api/status
```

### Consultar um cliente

```http
GET /api/status/{cliente}
```

### Executar monitoramento manual

```http
POST /api/monitorar
```

### Testar WhatsApp

```http
POST /api/teste-whatsapp
```

> Em produção, recomenda-se proteger os endpoints com autenticação, firewall, proxy reverso ou outra camada de controle de acesso.

---

# 🧪 Testes

O projeto possui testes para:

### `BackupScannerServiceTest`

- identificação do backup;
- filtro por padrão;
- cálculo de idade;
- status `OK`;
- status `ATRASADO`;
- status `SEM_BACKUP`.

### `AlertStateServiceTest`

- persistência do estado;
- leitura após restart;
- controle diário.

### `MonitorServiceTest`

- consolidação dos problemas;
- envio de uma única mensagem;
- bloqueio de segunda mensagem no mesmo dia.

Executar:

```bash
mvn clean test
```

---

# 🏗️ Build

Gerar o WAR:

```bash
mvn clean package
```

Resultado:

```text
target/monitor-backup-whatsapp.war
```

---

# 🚀 Deploy no Tomcat 9

Copie o WAR para:

```text
$CATALINA_BASE/webapps/
```

Exemplo:

```bash
cp target/monitor-backup-whatsapp.war /opt/tomcat/webapps/
```

Configure as variáveis de ambiente usando `monitor-backup.env.example` como referência.

Depois reinicie o Tomcat e acompanhe os logs:

```bash
tail -f /opt/tomcat/logs/catalina.out
```

---

# 🔒 Boas práticas de produção

- Não versionar tokens.
- Não colocar `.env` real no Git.
- Não registrar headers HTTP nos logs.
- Não expor a API diretamente à Internet.
- Restringir permissões do diretório de estado.
- Executar o Tomcat com usuário sem privilégios administrativos.
- Utilizar HTTPS quando a API estiver exposta.
- Monitorar os logs do Tomcat.
- Planejar migração futura de Java 8 para Java 17+.

---

# 🧩 Arquitetura

```text
                 ┌──────────────────────┐
                 │      Scheduler       │
                 │    Spring Boot       │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │ BackupScannerService │
                 │                      │
                 │ Diretórios de backup │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │    MonitorService    │
                 │                      │
                 │ OK / ATRASADO /      │
                 │ SEM_BACKUP           │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │ AlertStateService    │
                 │                      │
                 │ 1 alerta por dia     │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │  WhatsAppService     │
                 │                      │
                 │ Cloud API Meta       │
                 └──────────────────────┘
```

---

# 💡 O que este projeto demonstra

- Java;
- Spring Boot;
- API REST;
- integração com API externa;
- automação;
- Scheduler;
- Linux;
- sistema de arquivos;
- monitoramento;
- tratamento de exceções;
- persistência de estado;
- configuração por ambiente;
- segurança de credenciais;
- Maven;
- Tomcat;
- testes automatizados.

O projeto foi desenvolvido para apoiar uma rotina operacional de monitoramento de backups, automação e alertas em ambientes Linux.

---

# 🚧 Melhorias futuras

- Autenticação da API.
- Dashboard web.
- Histórico de disponibilidade.
- Banco PostgreSQL para histórico.
- Cadastro de clientes via banco.
- Logs estruturados.
- Métricas e observabilidade.
- Prometheus/Grafana.
- Docker.
- CI/CD.
- HTTPS com Nginx.
- Retry controlado da integração externa.
- Suporte a múltiplos canais de alerta.
- Migração para Java 17+.
- Spring Boot atual.
- Testes de integração.
- Controle de usuários e permissões.

---

# ⭐ Apoie o projeto

Se este projeto foi útil para você, considere deixar uma ⭐ no repositório.

---

## 📄 Licença

Projeto desenvolvido para apoiar uma rotina operacional de monitoramento de backups, automação e alertas em ambientes Linux.
