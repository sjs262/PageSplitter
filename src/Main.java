import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {
    public static final String DIR = "C:/Users/sjsch/IdeaProjects/PageSplitter/myfiles/";

    public static void main(String[] args) throws IOException {
        String[] pngNames = getPNGs();
        for (String pngName : pngNames) {
            File invertedImage = invertImage(pngName);
    
            PdfDocument pdf = new PdfDocument(new PdfWriter(DIR + pngName.replace(".png", ".pdf")));
            Document document = new Document(pdf);
            ImageData data = ImageDataFactory.create(DIR + "inverted-" + pngName);
    
            float x = data.getWidth();
            float croppedHeight = 11F / 8.5F * x;
            float incrementHeight = 9F / 8.5F * x;
    
            float y = data.getHeight() - croppedHeight;
            while (true) {
                Image croppedImage = crop(pdf, data, 0, (int) y, (int) x, (int) croppedHeight);
                document.add(croppedImage);
                if (y < 0.0) break;
                y -= incrementHeight;
            }
            document.close();
            if (!invertedImage.delete()) throw new IOException();
        }
    }
    
    public static Image crop(PdfDocument pdf, ImageData data, int left, int bottom, int width, int height) {
        Image img = new Image(data).setFixedPosition(-left, -bottom);
        PdfFormXObject template = new PdfFormXObject(new Rectangle(width, height));
        Canvas canvas = new Canvas(template, pdf);
        canvas.add(img);
        return new Image(template);
    }

    public static File invertImage(String imageName) throws IOException {
        
        BufferedImage img = ImageIO.read(new File(DIR + imageName));
        File newFile = new File(DIR + "inverted-" + imageName);

        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgba = img.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
                img.setRGB(x, y, col.getRGB());
            }
        }
        
        ImageIO.write(img, "png", newFile);
        return newFile;
    }
    
    public static String[] getPNGs() {
        return new File(DIR).list((dir, name) -> name.endsWith(".png"));
    }
}