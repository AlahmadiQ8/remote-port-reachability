package io.momonet.portcheckerv2;

import io.momonet.portcheckerv2.models.Destination;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HelloController {

    private ObservableList<Destination> destinations = FXCollections.observableArrayList();

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Destination> tableView;

    public HelloController() {
    }

    @FXML
    public void initialize() {
        System.out.println("initializing");
        statusLabel.setText("initialized");


        destinations.addAll(
                new Destination("123.23.4,2", 123, "This is a nice description"),
                new Destination("google.com", 443, "This is a what description"),
                new Destination("finanteq.omg", 123, "This is a another description"),
                new Destination("localhost", 123, "This is a nice description")
        );

        tableView.setItems(destinations);
    }

    @FXML
    protected void checkAllButtonHandler() {
        System.out.println("ooh");
        statusLabel.setText("ooh");
    }
}