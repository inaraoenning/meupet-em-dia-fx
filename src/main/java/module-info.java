module clinicaveterinaria.meupetemdia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens clinicaveterinaria.meupetemdia.controller to javafx.fxml;

    exports clinicaveterinaria.meupetemdia;

    // ADICIONE ISTO:
    opens clinicaveterinaria.meupetemdia.config;
    exports clinicaveterinaria.meupetemdia.config;
}
