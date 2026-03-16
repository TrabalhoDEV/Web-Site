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

Crie um arquivo chamado `.env` (para o docker compose) na raiz do projeto baseado no arquivo `.env.example` e outra `.env` (para execução) dentro da pasta `resources` dentro de `src` .

```bash
cp .env.example .env
cp .env.example /src/resource/.env
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
SECRET=SuperSecret001

# Gemini:
AI_MODEL_API_KEY=

# Aplication:
BASE_URL="http://localhost:8080/SchoolServlet_war_exploded/"
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

## 6. Servidor

Configure o Apache Tomcat, se necessário baixe o em https://tomcat.apache.org/download-11.cgi.

Configure dentro da pasta do projeto um war_exploded para o projeto.

Baixe as dependências pelo Maven.

Execute o servidor Servlet (Tomcat configurado no projeto).

---

## 7. Acessar o sistema

Após iniciar o servidor, abra no navegador:

```
http://localhost:8080
```

------------------------------------------------------------------------

# 👩‍💻 Autores

Projeto desenvolvido pelos seguintes alunos da 2ºSérie da Escola de Tecnologia do Instituto J&F:
- Caio Marcos Ambrósio Maciel;
- Caio Mezini De Paula Machado;
- Eduardo Costa Amex Macal;
- Erick Santos Silva;
- Isabelly Vila Silva Da Hora;
- Mariana Marrão Ferreira Felis.

# Agradecimento:

Gostaríamos de dar os créditos da ideação do projeto ao nosso docente de Desenvolvimento de Sistemas, chamado Diogo Martins Nascimento,
que propôs esse desafio em sala de aula como forma de medir o nosso grau de conhecimento no desenvolvimento de aplicações Web com HTML5, CSS e Java Servlets.
Esse projeto não foi apenas um teste, foi uma forma de refletir acerca do aprendizado e aumento da nossa capacidade intelectual 
dentro da programação desde o ano passado (2025), pois ao contruí-lo percebemos erros que cometíamos no passado, e formas melhores de resolver determinado problema. 

Muito obrigado, professor!
