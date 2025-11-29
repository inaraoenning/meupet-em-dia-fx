module clinicaveterinaria.meupetemdia {
    requires javafx.controls;
    requires javafx.fxml;


    opens clinicaveterinaria.meupetemdia to javafx.fxml;
    exports clinicaveterinaria.meupetemdia;
}