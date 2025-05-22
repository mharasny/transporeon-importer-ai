package ai.transporeonimporter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAiApiService {

    public static String sendPrompt(String apiKey, String inputText) throws Exception {
        String endpoint = "https://api.openai.com/v1/chat/completions";
        String jsonPayload = """
        {
          "model": "gpt-4o",
          "messages": [
            {"role": "system", "content": "Zamień tekst zlecenia transportowego na poprawny, czytelny JSON z danymi do importu."},
            {"role": "user", "content": "Oto treść zlecenia:\\n%s\\nZamień na JSON."}
          ],
          "max_tokens": 1024,
          "temperature": 0.0
        }
        """.formatted(inputText.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parsowanie odpowiedzi JSON – bardzo prosto: wyciągnij "content"
        String responseBody = response.body();
        // Najprostszy parser – wyciągnięcie zawartości pierwszego "content"
        String content = responseBody.split("\"content\":\"")[1].split("\"")[0];
        content = content.replace("\\n", "\n");
        return content;
    }
}