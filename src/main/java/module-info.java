module mycompany.mavenproject1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens mycompany.mavenproject1 to javafx.fxml;
    exports mycompany.mavenproject1;
}
