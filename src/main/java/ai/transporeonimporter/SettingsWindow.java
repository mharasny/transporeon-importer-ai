package ai.transporeonimporter;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow {
    public static void showSettingsWindow() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Ustawienia API");
        settingsStage.initModality(Modality.APPLICATION_MODAL);

        // OpenAI
        Label openAiLabel = new Label("Podaj swój klucz OpenAI API:");
        PasswordField openAiKeyField = new PasswordField();
        openAiKeyField.setPromptText("sk-...");
        openAiKeyField.setText(ApiKeyManager.readApiKey());

        // Transporeon
        Label transporeonLabel = new Label("Podaj swój token Transporeon API:");
        PasswordField transporeonKeyField = new PasswordField();
        transporeonKeyField.setPromptText("Token Transporeon...");
        transporeonKeyField.setText(TransporeonKeyManager.readApiKey());

        Button saveBtn = new Button("Zapisz");
        Label statusLabel = new Label();
        saveBtn.setOnAction(e -> {
            String openAiKey = openAiKeyField.getText().trim();
            String transporeonKey = transporeonKeyField.getText().trim();
            if (openAiKey.isEmpty() || transporeonKey.isEmpty()) {
                statusLabel.setText("Oba pola muszą być wypełnione.");
            } else {
                ApiKeyManager.writeApiKey(openAiKey);
                TransporeonKeyManager.writeApiKey(transporeonKey);
                statusLabel.setText("Zapisano klucze.");
            }
        });

        VBox vbox = new VBox(12, openAiLabel, openAiKeyField,
                transporeonLabel, transporeonKeyField,
                saveBtn, statusLabel);
        vbox.setPadding(new Insets(15));
        settingsStage.setScene(new Scene(vbox, 380, 200));
        settingsStage.showAndWait();
    }
}