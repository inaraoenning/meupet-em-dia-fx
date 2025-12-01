# 📁 Utilidade de cada pasta do projeto

## 1. config/

Contém classes de configuração geral do sistema, especialmente:

**DatabaseConfig.java:** responsável por criar a conexão com o banco SQLite, inicializar tabelas, e centralizar
parâmetros de acesso ao BD.

### 👉 Por que existe?

Separa a configuração da infraestrutura do restante da aplicação, mantendo o código organizado e facilitando manutenção.

## 2. controller/

Armazena todos os controllers JavaFX, um para cada tela FXML.

**Exemplos:**

- LoginController.java
- CadastroDonosController.java

### 👉 Função dos controllers:

- Controlam eventos das telas (cliques, validações, mudanças).
- Fazem a comunicação entre a interface gráfica (FXML) e a lógica interna (model e service).
- Alteram a tela, buscam dados e exibem respostas ao usuário.

## 3. model/

Aqui ficam as entidades do domínio — objetos que representam os dados reais do sistema.

**Exemplos:**

- Dono.java
- Pet.java
- Vacina.java

### 👉 Função dos models:

Espelham as tabelas do banco de dados.

Representam o que o sistema manipula (pets, donos, vacinas...).

Geralmente incluem atributos, getters/setters e algumas regras simples (como validação mínima).

## 4. dao/ (Data Access Object)

Classes responsáveis por manter e recuperar dados do banco.

**Exemplos:**

- DonoDAO.java
- VacinaDAO.java

### 👉 Função dos DAOs:

São a camada que conversa diretamente com o banco SQLite.

Fazem SELECT, INSERT, UPDATE, DELETE.

Permitem que controllers e serviços não fiquem sujos com SQL.

## 5. service/

Camada de regras de negócio que não pertence ao controller nem ao DAO.

Exemplo:

AuthService.java: valida login, autenticação, permissões.

### 👉 Para que serve?

- Centraliza lógica importante.
- Evita controllers "gordos".
- Pode chamar múltiplos DAOs em uma única operação.

## 6. util/

Funções utilitárias, helpers e classes de apoio.

Exemplo:

NavigationUtil.java: trocar telas, carregar FXML, abrir pop-ups etc.

### 👉 Utilidade:

Evita duplicação de código nos controllers, facilita manutenção e deixa o projeto mais limpo.

📁 Pasta resources/

## 7. view/

Onde ficam os arquivos FXML das telas do JavaFX.

**Exemplos:**

- login.fxml
- cadastroPets.fxml

### 👉 Por que fica em resources?

Porque são recursos externos (arquivos de layout), carregados pelo JavaFX via classpath.

## 8. css/

Armazena folhas de estilo, ex:

- style.css

### 👉 Função:

Padronização visual do app (cores, fontes, espaçamentos).
Mantém o código visual separado do FXML.

📄 Arquivo raiz

## 9. pom.xml

Arquivo de configuração do Maven.
Gerencia dependências, plugins, compilação e build.

🗄️ Arquivo do banco

## 10. meupetemdia.db

O banco SQLite gerado automaticamente.

### 👉 Por que fica na raiz?

Boa prática em aplicações pequenas, fácil acesso e migração.

### ==================================================

### ✔️ Resumo Geral
**Pasta:** Função

- **config:** Configurações (principalmente banco)
- **controller:** Lógica das telas (MVC Controller)
- **model:** Classes de entidade do domínio
- **dao:** Comunicação com o banco
- **service:** Lógica de negócio
- **util:** Funções auxiliares, helpers
- **view:** (FXML)  Layout das telas
- **css:** Estilos da UI
- **Pom.xml:** Configuração Maven
- **meupetemdia.db:** Banco SQLite