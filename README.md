# 📚 Escola Vértice --- Sistema Acadêmico

Sistema acadêmico desenvolvido para gerenciamento de alunos,
professores, matérias e notas.

O sistema permite que professores registrem notas e observações sobre os
alunos, enquanto os alunos podem acompanhar seu desempenho acadêmico
através do boletim escolar.

------------------------------------------------------------------------

# 🎯 Objetivo do Projeto

O objetivo deste projeto é desenvolver um sistema acadêmico que facilite
o gerenciamento de informações escolares, permitindo que professores
registrem notas e observações e que alunos acompanhem seu desempenho de
forma simples, organizada e acessível.


------------------------------------------------------------------------

# 📦 Requisitos

Antes de rodar o projeto, instale:

-   Docker
-   Docker Compose
-   IntelliJ IDEA
-   Java JDK

------------------------------------------------------------------------
# ⚙️ Como Rodar o Projeto Localmente

## 1. Clonar o repositório

```bash
git clone https://github.com/TrabalhoDEV/Web-Site.git
```
---
## 2. Entrar na pasta do projeto

```bash
cd Web-Site
```

---

## 3. Criar o arquivo `.env`

O projeto utiliza variáveis de ambiente para configuração.

Crie um arquivo chamado `.env` na raiz do projeto baseado no arquivo `.env.example`.

```bash
cp .env.example .env
```

Depois abra o arquivo `.env` e configure as variáveis necessárias.

### Exemplo de configuração

```
# Environment
ENVIRONMENT="development"

# PostgreSQL
DB_URL="jdbc:postgresql://localhost:5432/devsecretaria"
DB_NAME="devsecretaria"
DB_USER="sa"
DB_PASSWORD="DefaultPassword0"

# Brevo
BREVO_API_URL="https://api.brevo.com/v3/smtp/email"
BREVO_API_KEY=
BREVO_EMAIL=

# Cryptography
SECRET=
```

---

## 4. Iniciar o banco de dados com Docker

O projeto utiliza Docker para executar o banco de dados PostgreSQL.

```bash
docker compose up -d
```

Isso irá subir automaticamente o container do **PostgreSQL**.

---

## 5. Abrir o projeto no IntelliJ

Abra a pasta do projeto no **IntelliJ IDEA**.

---

## 6. Rodar o servidor

Execute o servidor Servlet (Tomcat configurado no projeto).

---

## 7. Acessar o sistema

Após iniciar o servidor, abra no navegador:

```
http://localhost:8080
```

------------------------------------------------------------------------

# 👩‍💻 Autores

Projeto desenvolvido por Caio Marcos Ambrósio Maciel,Caio Mezini De Paula Machado, Eduardo Costa Amex Macal,Erick Santos Silva,Isabelly Vila Silva Da Hora e Mariana Marrão Ferreira Felis
