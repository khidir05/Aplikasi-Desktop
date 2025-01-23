package com.rewritetext.service;

import com.rewritetext.util.FileTypeDetector;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class DocumentProcessor {
    private Tesseract tesseract;
    private PDFHandler pdfHandler;

    public DocumentProcessor() {
        tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // Set path to Tesseract data
    }

    public String processDocument(File file) throws Exception {
        String extension = FileTypeDetector.getFileExtension(file);
        
        switch (extension.toLowerCase()) {
            case "pdf":
                return processPDF(file);
            case "docx":
                return processDocx(file);
            case "png":
            case "jpg":
            case "jpeg":
                return processImage(file);
            default:
                throw new UnsupportedOperationException("Unsupported file format");
        }
    }

    private String processPDF(File file) throws Exception {
        pdfHandler = new PDFHandler(file);
        
        if (pdfHandler.isPasswordProtected()) {
            // Handle password input and unlocking in UI
            throw new Exception("PDF is password protected");
        }
        
        // Process unlocked PDF
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(PDDocument.load(file));
    }

    private String processDocx(File file) {
        // Add DOCX processing logic
        return "";
    }

    private String processImage(File file) throws Exception {
        // Perform OCR on image
        return tesseract.doOCR(file);
    }
}