package ai.transporeonimporter;

import java.io.*;
import org.apache.commons.csv.*;

public class CsvToTextService {
    public static String extractText(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Reader in = new FileReader(file)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            for (CSVRecord record : records) {
                sb.append(String.join(", ", record)).append("\n");
            }
        }
        return sb.toString();
    }
}