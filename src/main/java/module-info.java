module chounion.quizmaven {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop; 
     
  

    opens chounion.quizmaven to javafx.fxml;
    exports chounion.quizmaven;
}
