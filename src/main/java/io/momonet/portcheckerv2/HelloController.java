package io.momonet.portcheckerv2;

import io.momonet.portcheckerv2.models.Destination;
import io.momonet.portcheckerv2.models.Status;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class HelloController {

    private ObservableList<Destination> destinations = FXCollections.observableArrayList();

    @FXML private TableColumn portColumn;
    @FXML private TableColumn actionColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Destination> tableView;

    @FXML private TextField hostTextField;
    @FXML private TextField portTextField;
    @FXML private TextField descriptionTextField;


    public HelloController() {
    }

    @FXML
    public void initialize() {
        System.out.println("initializing");
        statusLabel.setText("initialized");

        destinations.addAll(
                new Destination("8.8.8.8", 80, "This is a nice description"),
                new Destination("google.com", 443, "This is a what description"),
                new Destination("finanteq.omg", 123, "This is a another description"),
                new Destination("ns1.telstra.net", 80, "This is a nice description")
        );

        tableView.setItems(destinations);
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Destination destination, boolean empty) {
                super.updateItem(destination, empty);
                if (!empty) {
                    switch (destination.getStatus()) {
                        case REACHABLE -> setStyle("-fx-background-color: #ceffce;");
                        case UNREACHABLE -> setStyle("-fx-background-color: #ffc9c9;");
                        default -> setStyle("-fx-background-color: none;");
                    }
                }
            }
        });

        portColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerConverter()));
        actionColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Check", this::onCheckRow));
    }

    @FXML
    protected void checkAllButtonHandler() {
    }

    @FXML
    protected void addRowHandler() {
        var destination = new Destination();
        destination.setHost(hostTextField.getText());
        destination.setPort(Integer.parseInt(portTextField.getText()));
        destination.setDescription(descriptionTextField.getText());
        tableView.getItems().add(destination);
        hostTextField.clear();
        portTextField.clear();
        descriptionTextField.clear();
    }

    public void onEditHost(TableColumn.CellEditEvent<Destination, String> cellEditEvent) {
        cellEditEvent.getTableView()
                .getItems()
                .get(cellEditEvent.getTablePosition().getRow())
                .setHost(cellEditEvent.getNewValue());
    }

    public void onEditPort(TableColumn.CellEditEvent<Destination, Integer> cellEditEvent) {
        cellEditEvent.getTableView()
                .getItems()
                .get(cellEditEvent.getTablePosition().getRow())
                .setPort(cellEditEvent.getNewValue());
    }

    public void onEditDescription(TableColumn.CellEditEvent<Destination, String> cellEditEvent) {
        cellEditEvent.getTableView()
                .getItems()
                .get(cellEditEvent.getTablePosition().getRow())
                .setDescription(cellEditEvent.getNewValue());
    }

    public void onDelete(ActionEvent actionEvent) {
        var allProducts = tableView.getItems();
        var productsSelected = tableView.getSelectionModel().getSelectedItems();
        productsSelected.forEach(allProducts::remove);
    }

    public Destination onCheckRow(Destination d, Button b) {
        var task = new Task<Destination>() {
            @Override
            protected Destination call() throws Exception {
                b.setDisable(true);
                Platform.runLater(() -> statusLabel.setText("Checking " + d.getHost() + ":" + d.getPort() + "..."));
                try {
                    tableView.setEditable(false);
                    try (Socket soc = new Socket()) {
                        soc.connect(new InetSocketAddress(d.getHost(), d.getPort()), 3000);
                    }
                    Platform.runLater(() -> statusLabel.setText(d.getHost() + ":" + d.getPort() + " is reachable"));
                    d.setStatus(Status.REACHABLE);
                } catch (IOException ex) {
                    Platform.runLater(() -> statusLabel.setText(d.getHost() + ":" + d.getPort() + " is not reachable"));
                    d.setStatus(Status.UNREACHABLE);
                } finally {
                    tableView.refresh();
                    b.setDisable(false);
                    tableView.setEditable(true);
                    return d;
                }
            }
        };

        var thread = new Thread(task);
        thread.start();
        return d;
    }

    class IntegerConverter extends StringConverter<Integer> {

        @Override
        public String toString(Integer integer) {
            return integer.toString();
        }

        @Override
        public Integer fromString(String s) {
            return Integer.parseInt(s);
        }
    }
}