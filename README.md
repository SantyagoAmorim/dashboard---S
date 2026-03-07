# 🚀 Creative Ops Dashboard

Sistema interno de **gestão de operações criativas da Start Digital Company**.

O **Creative Ops Dashboard** foi criado para centralizar toda a operação da agência em um único painel: pedidos criativos, gestão de clientes, acompanhamento de entregas e métricas de performance dos times.

Este projeto está sendo desenvolvido utilizando:

- **Spring Boot**
- **Vaadin**
- **PostgreSQL**
- **Docker**
- **Java 21**

---

# 🎯 Objetivo do Sistema

Agências normalmente organizam suas demandas em várias ferramentas diferentes como:

- WhatsApp
- Trello
- Notion
- Planilhas

Isso acaba gerando **desorganização, retrabalho e perda de informações**.

O **Creative Ops Dashboard** resolve esse problema centralizando toda a operação em um único sistema.

Com ele será possível:

- gerenciar pedidos criativos
- acompanhar entregas
- monitorar produtividade
- organizar clientes
- estruturar squads
- visualizar métricas da agência

---

# 🏢 Estrutura da Agência

O sistema foi pensado para a estrutura operacional da agência.

## 🎨 Design

Responsável pela produção criativa.

Inclui:

- Social media
- Vídeos
- Criativos de tráfego
- Peças institucionais
- Materiais publicitários

---

## 📈 Tráfego Pago

Responsável pela performance das campanhas.

Inclui:

- gestão de campanhas
- otimização de anúncios
- acompanhamento de métricas
- geração de leads

---

## 💼 Comercial

Responsável pela aquisição e relacionamento com clientes.

Inclui:

- prospecção
- fechamento
- relacionamento
- gestão da carteira de clientes

---

# 👥 Organização por Squads

A operação da agência pode ser organizada por **squads multidisciplinares**.

Um squad normalmente possui:

- 2 Designers
- 2 Gestores de Tráfego
- 2 Comerciais

Cada squad pode atender um conjunto de clientes.

---

# ⚙️ Tecnologias Utilizadas

## Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate

## Frontend

- Vaadin (UI construída totalmente em Java)

## Banco de Dados

- PostgreSQL
- Docker

## Build

- Maven

---

# 🏗 Arquitetura do Projeto

O projeto segue uma arquitetura organizada por camadas.

src/main/java/com/agency/dashboard

domain
└── entidades do sistema

repo
└── repositórios de acesso ao banco

service
└── regras de negócio

ui
└── telas do sistema (Vaadin)

seed
└── inicialização de dados



---

# ✅ Funcionalidades Já Implementadas

## 🔐 Autenticação básica

O sistema já possui um sistema inicial de login.

Funcionalidades atuais:

- login de usuário
- controle de sessão
- bloqueio de páginas sem login
- exibição do usuário logado
- botão de logout

---

## 📊 Dashboard Operacional

Painel principal com visão geral da operação criativa.

KPIs disponíveis:

- requisições no mês
- entregas no mês
- pendências
- lead time médio

Filtros disponíveis:

- cliente
- status
- mês

---

## 🏆 Ranking de Clientes

Visualização do volume de pedidos por cliente no mês.

Permite identificar rapidamente:

- clientes com maior demanda
- volume de produção da agência

---

## 📋 Pedidos Recentes

Tabela com os pedidos mais recentes contendo:

- cliente
- tipo de pedido
- título
- status
- data de criação

---

## 🎨 Gestão de Pedidos Criativos

Tela completa para gerenciamento de requisições.

Permite:

- criar pedido
- editar pedido
- alterar status
- excluir pedido

Campos principais:

- cliente
- tipo
- status
- título
- descrição
- data de criação
- data de entrega

---

## 🗄 Banco de Dados PostgreSQL

O sistema já está conectado a um banco real utilizando Docker.

Tabelas atuais:

- users
- clients
- requests

---

# 🚧 Funcionalidades Planejadas

O sistema continuará evoluindo com diversas funcionalidades.

---

## 👤 Gestão de Usuários

- cadastro de usuários
- edição de usuários
- controle de setor
- controle de squad

---

## 🔑 Sistema de Permissões

Controle de acesso baseado em função.

Perfis planejados:

- Admin
- Gestão
- Design
- Tráfego
- Comercial

---

## 📊 Dashboard por Setor

Cada equipe terá seu próprio painel.

### Design

- volume de criativos
- backlog de produção
- entregas por designer

### Tráfego

- campanhas ativas
- performance
- leads gerados

### Comercial

- pipeline de vendas
- novos clientes
- taxa de conversão

---

## 👥 Gestão de Clientes

Sistema completo para gestão de clientes.

Funcionalidades planejadas:

- cadastro de clientes
- histórico de pedidos
- volume de produção
- métricas por cliente

---

## 🗂 Sistema de Tarefas

Organização de produção interna.

Permitirá:

- criação de tarefas
- atribuição de responsáveis
- acompanhamento de status

---

## ⚡ Sistema de Squads

Visualização da estrutura da agência.

Permitirá visualizar:

- squads
- membros
- clientes atendidos
- performance do time

---

## 📈 Métricas Avançadas

Indicadores como:

- tempo médio de entrega
- produtividade por designer
- produtividade por squad
- volume de produção por cliente

---

# ▶️ Como Rodar o Projeto

## 1️⃣ Clonar o repositório

```bash
git clone https://github.com/seuusuario/creative-dashboard


docker run -d \
--name start-postgres \
-e POSTGRES_USER=start \
-e POSTGRES_PASSWORD=start123 \
-e POSTGRES_DB=start_dashboard \
-p 5433:5432 \
postgres:16



👨‍💻 Autor

Projeto desenvolvido por Yago Santos da Silva.

Sistema interno para gestão de operações criativas de agência.
