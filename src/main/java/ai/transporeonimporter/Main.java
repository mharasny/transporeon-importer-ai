package ai.transporeonimporter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Main extends Application {
    private TextArea manualTextArea;
    private TextArea resultArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Transporeon Importer AI");

        // Przycisk ustawień po prawej
        Button settingsBtn = new Button("Ustawienia");
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10, 10, 0, 10));
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.getChildren().add(settingsBtn);
        settingsBtn.setOnAction(e -> SettingsWindow.showSettingsWindow());

        // Przycisk do ładowania pliku
        Button importBtn = new Button("Importuj plik (PDF, CSV, XLSX, JPG)");
        importBtn.setOnAction(e -> chooseFile(primaryStage));

        // Pole na JSON
        manualTextArea = new TextArea();
        manualTextArea.setPromptText("Wklej tutaj JSON frachtu lub skonwertowane dane z pliku...");

        // Przycisk wysyłania
        Button sendBtn = new Button("Wyślij do Transporeon");
        sendBtn.setOnAction(e -> sendToTransporeon());

        // Wynik działania
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPromptText("Wynik operacji...");

        VBox mainBox = new VBox(10, topBar, importBtn,
            new Label("Dane JSON frachtu:"), manualTextArea,
            sendBtn, new Label("Odpowiedź API:"), resultArea);
        mainBox.setPadding(new Insets(10));
        Scene scene = new Scene(mainBox, 650, 580);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik do zaimportowania");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Obsługiwane pliki", "*.pdf", "*.csv", "*.xlsx", "*.jpg", "*.jpeg", "*.png"),
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("CSV", "*.csv"),
            new FileChooser.ExtensionFilter("Excel", "*.xlsx"),
            new FileChooser.ExtensionFilter("Obrazy", "*.jpg", "*.jpeg", "*.png")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String json = "";
            String fileName = file.getName().toLowerCase();
            try {
                if (fileName.endsWith(".pdf")) {
                    json = PdfToTextService.extractText(file);
                } else if (fileName.endsWith(".csv")) {
                    json = CsvToTextService.extractText(file);
                } else if (fileName.endsWith(".xlsx")) {
                    json = XlsxToTextService.extractText(file);
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                    json = JpgOcrService.extractText(file);
                } else {
                    resultArea.setText("Nieobsługiwany format pliku!");
                    return;
                }
                manualTextArea.setText(json);
                resultArea.setText("Plik zaimportowany i przekonwertowany do JSON.");
            } catch (Exception e) {
                resultArea.setText("Błąd podczas przetwarzania pliku: " + e.getMessage());
            }
        }
    }

    private void sendToTransporeon() {
        resultArea.clear();
        String json = manualTextArea.getText().trim();
        if (json.isEmpty()) {
            resultArea.setText("Wklej JSON frachtu do pola tekstowego!");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(json);
        } catch (JsonProcessingException e) {
            resultArea.setText("Błąd: Nieprawidłowy JSON!\n" + e.getMessage());
            return;
        }

        String transporeonToken = TransporeonKeyManager.readApiKey();
        if (transporeonToken.isEmpty()) {
            resultArea.setText("Brak klucza Transporeon API. Ustaw klucz w pliku transporeon.key!");
            return;
        }

        new Thread(() -> {
            try {
                String response = TransporeonApiService.createShipment(transporeonToken, json);
                Platform.runLater(() -> resultArea.setText(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() ->
                        resultArea.setText("Błąd: " + ex.getMessage()));
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}