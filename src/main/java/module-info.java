module org.example.dictionaryfinals {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.dictionaryfinals to javafx.fxml;
    exports org.example.dictionaryfinals;
}