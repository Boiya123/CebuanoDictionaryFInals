package org.example.dictionaryfinals;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/dictionaryfinals/DictionaryUI.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cebuano-Tagalog-English Dictionary");
        primaryStage.show();

        // Load dictionary data (add your file path)
        DictionaryController controller = loader.getController();
        controller.loadDictionary("/Users/dionvargas/Documents/JAVA/DSA_Cebuano-Tagalog-English/DictionaryFinals/src/main/java/org/example/dictionaryfinals/filtered_combined_translation_filter.txt");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
