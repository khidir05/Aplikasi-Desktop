package com.rewritetext.controller;

import com.rewritetext.service.DocumentProcessor;
import com.rewritetext.model.ProcessingResult;
import java.io.File;

public class DocumentController {
    private DocumentProcessor processor;

    public DocumentController() {
        this.processor = new DocumentProcessor();
    }

    public ProcessingResult processFile(File file) {
        try {
            String content = processor.processDocument(file);
            return new ProcessingResult(true, content, null);
        } catch (Exception e) {
            return new ProcessingResult(false, null, e.getMessage());
        }
    }
}