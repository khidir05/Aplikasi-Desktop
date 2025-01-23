package com.rewritetext.service;

import net.sourceforge.tess4j.Tesseract;
import java.io.File;

public class OCRService {
    private Tesseract tesseract;

    public OCRService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // Set path to Tesseract data
    }

    public String performOCR(File file) throws Exception {
        return tesseract.doOCR(file);
    }
}