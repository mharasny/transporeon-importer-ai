package ai.transporeonimporter;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.io.File;

public class Main extends Application {

    private TextArea resultArea;
    private File selectedFile;
    private TextArea manualTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Transporeon Importer AI");

        Button chooseFileBtn = new Button("Wybierz plik (PDF, CSV, XLSX, JPG)");
        Button settingsBtn = new Button("Ustawienia");
        Label fileLabel = new Label("Nie wybrano pliku");

        chooseFileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Obsługiwane pliki", "*.pdf", "*.csv", "*.xlsx", "*.jpg", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile = file;
                fileLabel.setText("Wybrano: " + file.getName());
            }
        });

        settingsBtn.setOnAction(e -> SettingsWindow.showSettingsWindow());

        manualTextArea = new TextArea();
        manualTextArea.setPromptText("Możesz tu wkleić/edytować tekst, który zostanie wysłany do OpenAI (lub wybierz plik powyżej).");
        manualTextArea.setPrefRowCount(8);
        manualTextArea.setWrapText(true);

        Button sendBtn = new Button("Wyślij do OpenAI");
        sendBtn.setOnAction(e -> sendToOpenAI());

        resultArea = new TextArea();
        resultArea.setPromptText("Tutaj pojawi się wynikowy JSON...");
        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        HBox topRow = new HBox(10, chooseFileBtn, settingsBtn, fileLabel);
        VBox root = new VBox(12, topRow, manualTextArea, sendBtn, resultArea);
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root, 650, 560);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendToOpenAI() {
        resultArea.clear();

        // Najpierw sprawdź pole tekstowe (jeśli nie jest puste, korzystamy z niego)
        String userText = manualTextArea.getText().trim();
        String textToSend = null;

        if (!userText.isEmpty()) {
            textToSend = userText;
        } else if (selectedFile != null) {
            try {
                textToSend = extractFileText(selectedFile);
            } catch (Exception ex) {
                resultArea.setText("Błąd podczas czytania pliku: " + ex.getMessage());
                return;
            }
        } else {
            resultArea.setText("Wklej tekst lub wybierz plik do wysłania!");
            return;
        }

        String apiKey = ApiKeyManager.readApiKey();
        if (apiKey.isEmpty()) {
            resultArea.setText("Brak klucza OpenAI API. Ustaw klucz w Ustawieniach.");
            return;
        }

        String finalTextToSend = textToSend;
        new Thread(() -> {
            try {
                String jsonResult = OpenAiApiService.sendPrompt(apiKey, finalTextToSend);
                javafx.application.Platform.runLater(() -> resultArea.setText(jsonResult));
            } catch (Exception ex) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() ->
                        resultArea.setText("Błąd: " + ex.getMessage()));
            }
        }).start();
    }

    private String extractFileText(File file) throws Exception {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            return PdfToTextService.extractText(file);
        } else if (name.endsWith(".csv")) {
            return CsvToTextService.extractText(file);
        } else if (name.endsWith(".xlsx")) {
            return XlsxToTextService.extractText(file);
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return JpgOcrService.extractText(file);
        } else {
            throw new IllegalArgumentException("Nieobsługiwany format pliku!");
        }
    }
}