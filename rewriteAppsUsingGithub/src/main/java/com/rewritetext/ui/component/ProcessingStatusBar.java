package com.rewritetext.ui.component;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ProcessingStatusBar extends HBox {
    private Label statusLabel;

    public ProcessingStatusBar() {
        statusLabel = new Label("Ready");
        
        getChildren().add(statusLabel);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}