package ai.transporeonimporter;

import java.io.*;

public class ApiKeyManager {
    private static final String KEY_FILE = "openai.key";

    public static String readApiKey() {
        File f = new File(KEY_FILE);
        if (!f.exists()) return "";
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            return r.readLine().trim();
        } catch (IOException e) {
            return "";
        }
    }

    public static void writeApiKey(String key) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(KEY_FILE))) {
            w.write(key.trim());
        } catch (IOException e) {
            // można dodać obsługę błędów jeśli trzeba
        }
    }
}