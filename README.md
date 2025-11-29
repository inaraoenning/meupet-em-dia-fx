# Estrutura do Projeto

``````text
ClinicaVeterinaria/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── br/
│       │       └── com/
│       │           └── clinica/
│       │               ├── Main.java
│       │               │
│       │               ├── model/
│       │               │   ├── Dono.java
│       │               │   ├── Pet.java
│       │               │   ├── Vacina.java
│       │               │   ├── Consulta.java
│       │               │   └── RegistroVacina.java
│       │               │
│       │               ├── controller/
│       │               │   ├── LoginController.java
│       │               │   ├── MenuPrincipalController.java
│       │               │   ├── CadastroDonosController.java
│       │               │   ├── CadastroPetsController.java
│       │               │   ├── CadastroVacinasConsultasController.java
│       │               │   ├── ProximasVacinasController.java
│       │               │   └── RelatoriosController.java
│       │               │
│       │               ├── dao/
│       │               │   ├── DonoDAO.java
│       │               │   ├── PetDAO.java
│       │               │   ├── VacinaDAO.java
│       │               │   └── ConsultaDAO.java
│       │               │
│       │               ├── service/
│       │               │   ├── FirebaseAuthService.java
│       │               │   ├── VacinaService.java
│       │               │   └── RelatorioService.java
│       │               │
│       │               └── util/
│       │                   ├── NavigationUtil.java
│       │                   ├── ValidationUtil.java
│       │                   └── AlertUtil.java
│       │
│       └── resources/
│           ├── view/
│           │   ├── login.fxml
│           │   ├── menuPrincipal.fxml
│           │   ├── cadastroDonos.fxml
│           │   ├── cadastroPets.fxml
│           │   ├── cadastroVacinasConsultas.fxml
│           │   ├── proximasVacinas.fxml
│           │   └── relatorios.fxml
│           │
│           ├── css/
│           │   └── style.css
│           │
│           ├── images/
│           │   ├── logo.png
│           │   ├── icon-dono.png
│           │   ├── icon-pet.png
│           │   ├── icon-vacina.png
│           │   └── icon-relatorio.png
│           │
│           └── firebase/
│               └── serviceAccountKey.json
│
├── test/
│   └── java/
│       └── br/
│           └── com/
│               └── clinica/
│                   ├── model/
│                   ├── service/
│                   └── util/
│
└── pom.xml
``````