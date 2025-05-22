package ai.transporeonimporter;

import net.sourceforge.tess4j.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class JpgOcrService {
    public static String extractText(File file) throws Exception {
        ITesseract instance = new Tesseract();
        // Jeżeli chcesz polski język, zainstaluj tesseract-ocr-pol i ustaw instance.setLanguage("pol");
        BufferedImage img = ImageIO.read(file);
        return instance.doOCR(img);
    }
}