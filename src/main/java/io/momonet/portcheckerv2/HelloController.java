package io.momonet.portcheckerv2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label statusLabel;

    @FXML
    protected void checkAllButtonHandler() {
        System.out.println("ooh");
        statusLabel.setText("ooh");
    }
}