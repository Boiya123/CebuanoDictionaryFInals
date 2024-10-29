package org.example.dictionaryfinals;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DictionaryController {

    @FXML
    private TextField cebuanoText;
    @FXML
    private TextField tagalogTranslation;
    @FXML
    private TextField englishTranslation;
    @FXML
    private TextField searchTextField;
    @FXML
    private ListView<HBox> wordsListView;
    @FXML
    private Button translateButton;
    @FXML
    private VBox historyContainer;
    @FXML
    private VBox vboxHistory;

    private HashMap<String, DictionaryEntry> dictionary = new HashMap<>();

    private boolean isUpdating = false; // Prevent circular updates

    // Class to hold dictionary entries
    static class DictionaryEntry {
        String tagalog;
        String english;
        String cebuano;

        DictionaryEntry(String cebuano, String tagalog, String english) {
            this.cebuano = cebuano;
            this.tagalog = tagalog;
            this.english = english;
        }
    }

    // Function to load the dictionary from the file
    public void loadDictionary(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String tagalog = "", cebuano = "", english = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Tagalog: ")) {
                    tagalog = line.replace("Tagalog: ", "").trim();
                } else if (line.startsWith("Cebuano: ")) {
                    cebuano = line.replace("Cebuano: ", "").trim();
                } else if (line.startsWith("English: ")) {
                    english = line.replace("English: ", "").trim();
                    dictionary.put(cebuano.toLowerCase(), new DictionaryEntry(cebuano, tagalog, english));
                    dictionary.put(tagalog.toLowerCase(), new DictionaryEntry(cebuano, tagalog, english));
                    dictionary.put(english.toLowerCase(), new DictionaryEntry(cebuano, tagalog, english));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        loadDictionary("/Users/dionvargas/Documents/JAVA/DSA_Cebuano-Tagalog-English/DictionaryFinals/src/main/java/org/example/dictionaryfinals/filtered_combined_translation_filter.txt");
        displayMatchingWords(getWordsStartingWith("A"));

        searchTextField.setOnAction(event -> onSearchEntered());
    }

    @FXML
    private void onTranslateClicked() {
        String cebuanoInput = cebuanoText.getText().trim();
        String tagalogInput = tagalogTranslation.getText().trim();
        String englishInput = englishTranslation.getText().trim();

        if (!cebuanoInput.isEmpty()) {
            translate(cebuanoInput, "cebuano");
        } else if (!tagalogInput.isEmpty()) {
            translate(tagalogInput, "tagalog");
        } else if (!englishInput.isEmpty()) {
            translate(englishInput, "english");
        } else {
            showAlert("Please enter a word to translate.");
        }
    }

    private void onSearchEntered() {
        String searchText = searchTextField.getText().trim();
        if (!searchText.isEmpty()) {
            DictionaryEntry entry = dictionary.get(searchText.toLowerCase());
            if (entry != null) {
                cebuanoText.setText(entry.cebuano);
                tagalogTranslation.setText(entry.tagalog);
                englishTranslation.setText(entry.english);
                // Add the Cebuano word to the history
                addTranslationToHistory(entry.cebuano, entry.tagalog, entry.english);
            } else {
                showAlert("Sorry, no word \"" + searchText + "\" was found.");
            }
        } else {
            showAlert("Please enter a word to search.");
        }
    }

    // Function to translate based on user input from any text field
    private void translate(String inputWord, String language) {
        isUpdating = true;
        DictionaryEntry entry = dictionary.get(inputWord.toLowerCase());

        if (entry != null) {
            switch (language) {
                case "cebuano":
                    tagalogTranslation.setText(entry.tagalog);
                    englishTranslation.setText(entry.english);
                    // Add to history
                    addTranslationToHistory(entry.cebuano, entry.tagalog, entry.english);
                    break;
                case "tagalog":
                    cebuanoText.setText(entry.cebuano);
                    englishTranslation.setText(entry.english);

                    addTranslationToHistory(entry.cebuano, entry.tagalog, entry.english);
                    break;
                case "english":
                    cebuanoText.setText(entry.cebuano);
                    tagalogTranslation.setText(entry.tagalog);

                    addTranslationToHistory(entry.cebuano, entry.tagalog, entry.english);
                    break;
            }
        } else {
            showAlert("Sorry, no word \"" + inputWord + "\" was found.");
            clearFields(language);
        }

        isUpdating = false;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Function to clear the other two text fields based on which one is currently being typed into
    private void clearFields(String activeLanguage) {
        switch (activeLanguage) {
            case "cebuano":
                tagalogTranslation.setText("");
                englishTranslation.setText("");
                break;
            case "tagalog":
                cebuanoText.setText("");
                englishTranslation.setText("");
                break;
            case "english":
                cebuanoText.setText("");
                tagalogTranslation.setText("");
                break;
        }
    }

    // Function to handle letter button clicks
    @FXML
    private void onLetterClicked(javafx.event.ActionEvent event) {
        String selectedLetter = ((Button) event.getSource()).getText().toUpperCase();
        List<String> matchingWords = getWordsStartingWith(selectedLetter);
        displayMatchingWords(matchingWords);
    }

    // Function to get words starting with the selected letter
    private List<String> getWordsStartingWith(String letter) {
        return dictionary.entrySet().stream()
                .filter(entry -> entry.getValue().cebuano.toLowerCase().startsWith(letter.toLowerCase()))
                .map(entry -> entry.getValue().cebuano)
                .distinct()
                .collect(Collectors.toList());
    }

    // Function to display words as buttons in the ListView
    private void displayMatchingWords(List<String> words) {
        wordsListView.getItems().clear();
        for (String word : words) {
            Button wordButton = new Button(word);
            wordButton.setOnAction(e -> onWordButtonClicked(word));

            HBox buttonContainer = new HBox(wordButton);
            buttonContainer.setStyle("");

            wordsListView.getItems().add(buttonContainer);
        }
    }

    // Function to handle word button clicks and populate text fields
    private void onWordButtonClicked(String word) {
        DictionaryEntry entry = dictionary.get(word.toLowerCase());
        if (entry != null) {
            isUpdating = true;
            cebuanoText.setText(entry.cebuano);
            tagalogTranslation.setText(entry.tagalog);
            englishTranslation.setText(entry.english);
            // Add the Cebuano word to the history
            addTranslationToHistory(entry.cebuano, entry.tagalog, entry.english);
            isUpdating = false;
        }
    }

    private void addTranslationToHistory(String cebuano, String tagalog, String english) {
        Button historyButton = new Button(cebuano);
        historyButton.setStyle("-fx-font-size: 26px; -fx-font-family: 'Helvetica';");

        historyButton.setOnAction(event -> {
            cebuanoText.setText(cebuano);
            tagalogTranslation.setText(tagalog);
            englishTranslation.setText(english);
        });


        vboxHistory.getChildren().add(0, historyButton);

        // Limit the history to the last 5 entries
        if (vboxHistory.getChildren().size() > 5) {
            vboxHistory.getChildren().remove(vboxHistory.getChildren().size() - 1); // Remove the oldest entry
        }
    }
}
