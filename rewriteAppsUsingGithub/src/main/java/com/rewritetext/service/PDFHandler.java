package com.rewritetext.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.File;
import java.io.IOException;

public class PDFHandler {
    private File pdfFile;
    private PDDocument document;

    public PDFHandler(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    public boolean isPasswordProtected() {
        try {
            document = PDDocument.load(pdfFile);
            return document.isEncrypted();
        } catch (IOException e) {
            return true; // Assume encrypted if there's an error loading
        } finally {
            closeDocument();
        }
    }

    public boolean tryUnlock(String password) {
        try {
            document = PDDocument.load(pdfFile, password);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void closeDocument() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}