# Estrutura do Projeto

``````text
MeuPetEmDia/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── clinicaveterinaria/meupetemdia/
│       │       ├── Main.java
│       │       │
│       │       ├── config/
│       │       │   └── DatabaseConfig.java
│       │       │
│       │       ├── controller/
│       │       │   ├── LoginController.java
│       │       │   ├── MenuPrincipalController.java
│       │       │   ├── CadastroDonosController.java
│       │       │   ├── CadastroPetsController.java
│       │       │   ├── CadastroVacinasConsultasController.java
│       │       │   ├── ProximasVacinasController.java
│       │       │   └── RelatoriosController.java
│       │       │
│       │       ├── model/
│       │       │   ├── Dono.java
│       │       │   ├── Pet.java
│       │       │   ├── Vacina.java
│       │       │   ├── Consulta.java
│       │       │   └── RegistroVacina.java
│       │       │
│       │       ├── dao/
│       │       │   ├── DonoDAO.java
│       │       │   ├── PetDAO.java
│       │       │   ├── VacinaDAO.java
│       │       │   ├── ConsultaDAO.java
│       │       │   └── RegistroVacinaDAO.java
│       │       │
│       │       ├── service/
│       │       │   └── AuthService.java
│       │       │
│       │       └── util/
│       │           └── NavigationUtil.java
│       │
│       └── resources/
│           ├── clinicaveterinaria/meupetemdia/view/
│           │   ├── login.fxml
│           │   ├── menuPrincipal.fxml
│           │   ├── cadastroDonos.fxml
│           │   ├── cadastroPets.fxml
│           │   ├── cadastroVacinasConsultas.fxml
│           │   ├── proximasVacinas.fxml
│           │   └── relatorios.fxml
│           │
│           └── css/
│               └── style.css
│
└── meupetemdia.db (criado automaticamente)
``````