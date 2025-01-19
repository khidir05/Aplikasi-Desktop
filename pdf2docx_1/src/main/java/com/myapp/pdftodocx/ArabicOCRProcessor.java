package com.myapp.pdftodocx;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TessAPI;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ArabicOCRProcessor {
    private final Tesseract tesseract;
    
    public ArabicOCRProcessor() {
        tesseract = new Tesseract();
        setupTesseract();
    }
    
    private void setupTesseract() {
        tesseract.setDatapath("src/main/resources/tessdata");
        // Menggunakan kedua bahasa: Arab dan Indonesia
        tesseract.setLanguage("ara+ind");
        // Mengatur mode segmentasi untuk text arab
        tesseract.setPageSegMode(6); // Assume uniform text block
        // Mengatur parameter khusus untuk text arab
        tesseract.setTessVariable("preserve_interword_spaces", "1");
        tesseract.setTessVariable("textord_force_make_prop_words", "F");
        tesseract.setTessVariable("segment_nonalphabetic_script", "1");
    }
    
    public String performOCR(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}