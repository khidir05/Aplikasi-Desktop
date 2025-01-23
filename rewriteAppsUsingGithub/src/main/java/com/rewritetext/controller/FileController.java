package com.rewritetext.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class FileController {
    private Stage stage;

    public FileController(Stage stage) {
        this.stage = stage;
    }

    public File selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported Files", "*.pdf", "*.docx", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.docx"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(stage);
    }
}