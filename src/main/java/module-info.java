module clinicaveterinaria.meupetemdia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens clinicaveterinaria.meupetemdia.controller to javafx.fxml;
    opens clinicaveterinaria.meupetemdia.model to javafx.base;

    exports clinicaveterinaria.meupetemdia;

    // ADICIONE ISTO:
    opens clinicaveterinaria.meupetemdia.config;
    exports clinicaveterinaria.meupetemdia.config;
}
