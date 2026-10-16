# 📦 Monitor Backup WhatsApp

> Sistema de monitoramento automático de backups em servidores Linux, desenvolvido em \*\*Java 8 + Spring Boot 2.7.18\*\*, empacotado como \*\*WAR\*\* para execução no \*\*Apache Tomcat 9\*\* e integrado à \*\*WhatsApp Cloud API da Meta\*\*.

O sistema verifica os backups configurados, identifica arquivos ausentes ou atrasados e envia um alerta consolidado pelo WhatsApp, reduzindo a necessidade de conferências manuais.

[![Java](https://img.shields.io/badge/Java-8-ED8B00?style=flat-square\&logo=openjdk\&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-6DB33F?style=flat-square\&logo=springboot\&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square\&logo=apachemaven\&logoColor=white)](https://maven.apache.org/)
[![Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-9-F8DC75?style=flat-square\&logo=apachetomcat\&logoColor=black)](https://tomcat.apache.org/)
[![Linux](https://img.shields.io/badge/Linux-Server-FCC624?style=flat-square\&logo=linux\&logoColor=black)](https://www.linux.org/)
[![WhatsApp Cloud API](https://img.shields.io/badge/WhatsApp-Cloud%20API-25D366?style=flat-square\&logo=whatsapp\&logoColor=white)](https://developers.facebook.com/docs/whatsapp/cloud-api/)
[![REST API](https://img.shields.io/badge/REST-API-005571?style=flat-square\&logo=fastapi\&logoColor=white)](https://en.wikipedia.org/wiki/REST)
[![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=flat-square\&logo=junit5\&logoColor=white)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-Tests-78A641?style=flat-square)](https://site.mockito.org/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

\---

## 📌 Sobre o projeto

O **Monitor Backup WhatsApp** automatiza a conferência de backups em diretórios configurados de servidores Linux. O sistema identifica arquivos ausentes ou atrasados, classifica a situação de cada cliente e consolida os problemas em um alerta enviado pelo WhatsApp.

A aplicação foi estruturada com Java e Spring Boot, com execução agendada, configuração por variáveis de ambiente, API REST e empacotamento WAR para implantação em Apache Tomcat 9.

\---

## 🎯 Objetivo

Transformar uma rotina operacional de conferência de backups em um processo automático.

O sistema:

1. Verifica os diretórios configurados;
2. Identifica o backup válido mais recente;
3. Compara a idade do backup com o limite definido;
4. Classifica o cliente como `OK`, `ATRASADO` ou `SEM\_BACKUP`;
5. Consolida os problemas em uma única mensagem;
6. Envia o alerta pelo WhatsApp;
7. Registra o estado do alerta diário para evitar duplicidade.

\---

## ⭐ Principais funcionalidades

* 🔎 Monitoramento automático de múltiplos clientes;
* 📁 Leitura dos diretórios de backup do Linux;
* 🎯 Filtro por padrão de arquivo (`glob`);
* ⏱️ Limite de backup configurável;
* 🟢 Status `OK`;
* 🟠 Status `ATRASADO`;
* 🔴 Status `SEM\_BACKUP`;
* 📱 Integração com WhatsApp Cloud API;
* 📨 Alerta consolidado;
* 🛑 Limite de uma tentativa de alerta por dia;
* 💾 Persistência do estado do alerta;
* 🔄 Continuidade após reinicialização;
* 🌎 Fuso horário configurável;
* 🌐 API REST para consulta e execução manual;
* 🧪 Testes automatizados com JUnit e Mockito;
* 🔐 Credenciais fora do código-fonte;
* ⚙️ Clientes configuráveis por ambiente;
* 📦 Empacotamento WAR para Tomcat 9.

\---

## 🧰 Tecnologias

[!\[Java](https://img.shields.io/badge/Java-8-ED8B00?style=flat-square\&logo=openjdk\&logoColor=white)](https://www.java.com/)
[!\[Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-6DB33F?style=flat-square\&logo=springboot\&logoColor=white)](https://spring.io/projects/spring-boot)
[!\[Spring Web](https://img.shields.io/badge/Spring%20Web-REST%20API-6DB33F?style=flat-square\&logo=spring\&logoColor=white)](https://spring.io/)
[!\[Maven](https://img.shields.io/badge/Maven-Dependencies-C71A36?style=flat-square\&logo=apachemaven\&logoColor=white)](https://maven.apache.org/)
[!\[Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-9-F8DC75?style=flat-square\&logo=apachetomcat\&logoColor=black)](https://tomcat.apache.org/)
[!\[WhatsApp Cloud API](https://img.shields.io/badge/WhatsApp-Cloud%20API-25D366?style=flat-square\&logo=whatsapp\&logoColor=white)](https://developers.facebook.com/docs/whatsapp/cloud-api/)
[!\[JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=flat-square\&logo=junit5\&logoColor=white)](https://junit.org/junit5/)
[!\[Mockito](https://img.shields.io/badge/Mockito-Testing-78A641?style=flat-square)](https://site.mockito.org/)
[!\[Linux](https://img.shields.io/badge/Linux-Server-FCC624?style=flat-square\&logo=linux\&logoColor=black)](https://www.linux.org/)

|Tecnologia|Utilização|
|-|-|
|Java 8|Linguagem e runtime|
|Spring Boot 2.7.18|Framework da aplicação|
|Spring Web|API REST|
|Spring Scheduler|Execução automática|
|Maven|Build e dependências|
|Apache Tomcat 9|Servidor de aplicação|
|WhatsApp Cloud API|Envio de alertas|
|JUnit 5|Testes automatizados|
|Mockito|Simulação de dependências nos testes|
|Linux|Ambiente operacional|

### Compatibilidade

O projeto permanece em Java 8 por compatibilidade com ambientes legados.

Por utilizar Spring Boot 2.7.x e APIs `javax.\*`, o WAR é destinado ao Apache Tomcat 9.x.

\---

## 🏗️ Arquitetura

```text
                 ┌──────────────────────┐
                 │      Scheduler       │
                 │     Spring Boot      │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │ BackupScannerService │
                 │ Diretórios de backup │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │    MonitorService    │
                 │ OK / ATRASADO /      │
                 │ SEM\_BACKUP           │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │  AlertStateService   │
                 │   1 alerta por dia   │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │   WhatsAppService    │
                 │   Cloud API da Meta │
                 └──────────────────────┘
```

\---

## 📂 Estrutura do projeto

```text
monitor-backup-whatsapp/
├── .gitignore
├── README.md
├── monitor-backup.env.example
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── br/com/monitorbackup/
    │   │       ├── MonitorBackupApplication.java
    │   │       ├── config/
    │   │       ├── controller/
    │   │       ├── model/
    │   │       └── service/
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/
            └── br/com/monitorbackup/
                └── service/
                    ├── AlertStateServiceTest.java
                    ├── BackupScannerServiceTest.java
                    └── MonitorServiceTest.java
```

A organização segue o padrão Maven/Spring Boot e separa configuração, API, modelos, regras de negócio, integração externa e testes.

\---

## 🔐 Segurança e configuração

### 🔑 Credenciais da API

O token do WhatsApp deve ser fornecido por variável de ambiente:

```bash
WHATSAPP\_TOKEN='SEU\_ACCESS\_TOKEN'
```

Também são configurados por ambiente:

```text
WHATSAPP\_PHONE\_NUMBER\_ID
WHATSAPP\_TO
WHATSAPP\_GRAPH\_VERSION
WHATSAPP\_MESSAGE\_MODE
WHATSAPP\_TEMPLATE\_NAME
WHATSAPP\_TEMPLATE\_LANGUAGE
```

Nunca coloque credenciais reais no código, no `application.properties` versionado ou no GitHub.

O arquivo `monitor-backup.env.example` contém somente placeholders.

### ⚙️ Configuração dos clientes

Os clientes não ficam definidos diretamente no código Java. A configuração utiliza:

```bash
MONITOR\_CLIENTS='nome-do-cliente:padrao-do-backup'
```

Formato:

```text
nome-do-cliente:padrao-do-backup
```

Exemplo genérico:

```text
cliente-a:\*\_0\_L0
```

O projeto não contém nomes de clientes, servidores ou dados operacionais reais.

\---

## ⏱️ Regra de monitoramento

Configuração padrão:

```text
Menos de 24 horas → OK
24 horas ou mais  → ATRASADO
Nenhum arquivo    → SEM\_BACKUP
```

O limite pode ser alterado por variável de ambiente:

```bash
MONITOR\_LIMIT\_HOURS=24
```

\---

## 📱 Alertas pelo WhatsApp

Quando existem problemas, o sistema consolida os clientes afetados em uma única mensagem.

O envio utiliza a **WhatsApp Cloud API da Meta** e pode trabalhar com texto ou template, conforme a configuração do ambiente e as regras aplicáveis à API.

\---

## 🛑 Controle de alerta diário

O Scheduler pode executar várias vezes ao longo do dia, mas o sistema mantém o estado do alerta em arquivo.

Fluxo simplificado:

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

O estado é registrado antes da chamada externa para ajudar a evitar duplicidade de tentativas no mesmo dia.

\---

## 🔄 Agendamento automático

Configuração padrão:

|Parâmetro|Valor|
|-|-|
|Intervalo|30 minutos|
|Primeira execução|1 minuto após a inicialização|

Variáveis de configuração:

```bash
MONITOR\_INTERVAL\_MS=1800000
MONITOR\_INITIAL\_DELAY\_MS=60000
```

\---

## 🌐 API REST

### ❤️ Health Check

```http
GET /api/health
```

### 📊 Consultar todos os clientes

```http
GET /api/status
```

### 🔎 Consultar um cliente

```http
GET /api/status/{cliente}
```

### ▶️ Executar monitoramento manual

```http
POST /api/monitorar
```

### 📱 Testar integração com WhatsApp

```http
POST /api/teste-whatsapp
```

> Em produção, recomenda-se proteger os endpoints com autenticação, firewall, proxy reverso ou outra camada de controle de acesso.

\---

## 🧪 Testes automatizados

O projeto possui testes para os seguintes serviços:

### `BackupScannerServiceTest`

* Identificação do backup;
* Filtro por padrão;
* Cálculo de idade;
* Status `OK`;
* Status `ATRASADO`;
* Status `SEM\_BACKUP`.

### `AlertStateServiceTest`

* Persistência do estado;
* Leitura após reinicialização;
* Controle diário.

### `MonitorServiceTest`

* Consolidação dos problemas;
* Envio de uma única mensagem;
* Bloqueio de uma segunda tentativa no mesmo dia.

Execute os testes com:

```bash
mvn clean test
```

\---

## 🏗️ Build

Para gerar o WAR:

```bash
mvn clean package
```

Artefato esperado:

```text
target/monitor-backup-whatsapp.war
```

\---

## 🚀 Deploy no Apache Tomcat 9

Copie o WAR para o diretório de aplicações:

```bash
cp target/monitor-backup-whatsapp.war /opt/tomcat/webapps/
```

Configure as variáveis de ambiente usando `monitor-backup.env.example` como referência.

Depois, reinicie o Tomcat e acompanhe os logs:

```bash
tail -f /opt/tomcat/logs/catalina.out
```

Confirme o caminho de instalação, as permissões e a configuração do serviço antes de realizar o deploy.

\---

## 🛡️ Boas práticas de produção

* 🔐 Não versionar tokens ou credenciais;
* 🚫 Não publicar arquivos `.env` reais no Git;
* 🧾 Não registrar cabeçalhos HTTP sensíveis nos logs;
* 🔒 Não expor a API diretamente à Internet sem proteção;
* 📁 Restringir as permissões do diretório de estado;
* 👤 Executar o Tomcat com usuário sem privilégios administrativos;
* 🌐 Utilizar HTTPS quando a API estiver exposta;
* 📊 Monitorar os logs do Tomcat;
* ⬆️ Planejar uma migração futura de Java 8 para uma versão mais recente.

\---

## 💡 O que este projeto demonstra

* ☕ Java;
* ⚙️ Spring Boot;
* 🌐 API REST;
* 🔗 Integração com API externa;
* 🤖 Automação e Scheduler;
* 🐧 Linux e sistema de arquivos;
* 🔎 Monitoramento de backups;
* 🧯 Tratamento de exceções;
* 💾 Persistência de estado;
* 🔐 Segurança de credenciais;
* 📦 Maven e empacotamento WAR;
* 🐱 Apache Tomcat;
* 🧪 Testes automatizados.

O projeto foi desenvolvido para apoiar uma rotina operacional de monitoramento de backups, automação e alertas em ambientes Linux.

\---

## 🚧 Melhorias futuras

* 🔐 Autenticação da API;
* 📊 Dashboard web;
* 📈 Histórico de disponibilidade;
* 🗄️ Banco PostgreSQL para histórico;
* 👥 Cadastro de clientes via banco;
* 🧾 Logs estruturados;
* 📡 Métricas e observabilidade;
* 📊 Integração com Prometheus/Grafana;
* 🐳 Docker;
* 🔄 CI/CD;
* 🌐 HTTPS com Nginx;
* 🔁 Retry controlado da integração externa;
* 📣 Suporte a múltiplos canais de alerta;
* ⬆️ Migração para Java 17+ e uma versão mais recente do Spring Boot;
* 🧪 Testes de integração;
* 👤 Controle de usuários e permissões.

\---

## \### 👨‍💻 Autor

## 

## \*\*Matheus Marks\*\*

## 

Projeto desenvolvido por iniciativa própria para auxiliar uma rotina operacional real, automatizando o monitoramento de backups em servidores Linux e o envio de alertas pelo WhatsApp. A solução foi criada para facilitar o acompanhamento dos backups, identificar arquivos ausentes ou atrasados e reduzir a necessidade de verificações manuais no dia a dia do serviço. O projeto está em constante evolução, com melhorias e ajustes baseados nas necessidades operacionais.

\---

## ⭐ Apoie o projeto

Se este projeto foi útil para você, considere deixar uma ⭐ no repositório.

\---

## 📄 Licença

Consulte o arquivo [`LICENSE`](LICENSE) para verificar os termos de uso e distribuição deste projeto.

