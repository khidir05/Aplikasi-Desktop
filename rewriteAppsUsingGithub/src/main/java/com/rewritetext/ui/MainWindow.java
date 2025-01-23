package com.rewritetext.ui;

import javafx.scene.layout.VBox;
import com.rewritetext.ui.component.FileSelectionPane;
import com.rewritetext.ui.component.ProcessingStatusBar;
import com.rewritetext.controller.DocumentController;
import com.rewritetext.controller.FileController;
import javafx.stage.Stage;
import java.io.File;

public class MainWindow {
    private VBox mainLayout;
    private FileSelectionPane fileSelectionPane;
    private ProcessingStatusBar statusBar;
    private DocumentController documentController;
    private FileController fileController;

    public MainWindow() {
        mainLayout = new VBox(10);

        fileSelectionPane = new FileSelectionPane();
        statusBar = new ProcessingStatusBar();
        documentController = new DocumentController();
        fileController = new FileController(new Stage());

        mainLayout.getChildren().addAll(fileSelectionPane, statusBar);

        initializeActions();
    }

    private void initializeActions() {
        fileSelectionPane.getSelectFileButton().setOnAction(event -> {
            File selectedFile = fileController.selectFile();
            if (selectedFile != null) {
                processFile(selectedFile);
            }
        });
    }

    private void processFile(File file) {
        statusBar.setStatus("Processing...");
        ProcessingResult result = documentController.processFile(file);
        if (result.isSuccess()) {
            // Handle successful processing (e.g., save to DOCX)
            statusBar.setStatus("Processing completed successfully.");
        } else {
            // Show error dialog
            ErrorDialog.showError("Error", "Failed to process file", result.getErrorMessage());
            statusBar.setStatus("Ready");
        }
    }

    public VBox getMainLayout() {
        return mainLayout;
    }
}