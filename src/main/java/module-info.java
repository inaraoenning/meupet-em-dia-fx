module clinicaveterinaria.meupetemdia {
    requires javafx.controls;
    requires javafx.fxml;

    // Abre pacotes para reflexão (FXML loader)
    opens clinicaveterinaria.meupetemdia.controller to javafx.fxml;

    // Exporta o pacote principal
    exports clinicaveterinaria.meupetemdia;
}
