package com.rewritetext.ui.component;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class FileSelectionPane extends HBox {
    private Button selectFileButton;

    public FileSelectionPane() {
        selectFileButton = new Button("Select File");
        
        getChildren().add(selectFileButton);
    }

    public Button getSelectFileButton() {
        return selectFileButton;
    }
}