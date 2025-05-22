package ai.transporeonimporter;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsWindow {
    public static void showSettingsWindow() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Ustawienia - Klucz OpenAI API");
        settingsStage.initModality(Modality.APPLICATION_MODAL);

        Label infoLabel = new Label("Podaj swój klucz OpenAI API:");
        PasswordField keyField = new PasswordField();
        keyField.setPromptText("sk-...");
        keyField.setText(ApiKeyManager.readApiKey());

        Button saveBtn = new Button("Zapisz");
        Label statusLabel = new Label();
        saveBtn.setOnAction(e -> {
            String key = keyField.getText().trim();
            if (!key.isEmpty()) {
                ApiKeyManager.writeApiKey(key);
                statusLabel.setText("Zapisano klucz.");
            } else {
                statusLabel.setText("Pole nie może być puste.");
            }
        });

        VBox vbox = new VBox(10, infoLabel, keyField, saveBtn, statusLabel);
        vbox.setPadding(new javafx.geometry.Insets(15));
        settingsStage.setScene(new Scene(vbox, 350, 150));
        settingsStage.showAndWait();
    }
}