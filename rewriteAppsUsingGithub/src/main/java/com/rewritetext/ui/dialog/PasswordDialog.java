package com.rewritetext.ui.dialog;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

public class PasswordDialog extends Dialog<String> {
    private PasswordField passwordField;
    private ButtonType submitButtonType;
    private ButtonType retryButtonType;

    public PasswordDialog() {
        setTitle("PDF Password Required");
        setHeaderText("This PDF file is password protected");

        // Create UI components
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);

        getDialogPane().setContent(grid);

        // Add buttons
        submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        retryButtonType = new ButtonType("Retry", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(submitButtonType, retryButtonType);

        // Set result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return passwordField.getText();
            }
            return null;
        });
    }
}