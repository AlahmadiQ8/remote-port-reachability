package io.momonet.portcheckerv2;

import io.momonet.portcheckerv2.models.Destination;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class HelloController {

    private ObservableList<Destination> destinations = FXCollections.observableArrayList();

    @FXML private TableColumn portColumn;

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
                new Destination("123.23.4,2", 123, "This is a nice description"),
                new Destination("google.com", 443, "This is a what description"),
                new Destination("finanteq.omg", 123, "This is a another description"),
                new Destination("localhost", 123, "This is a nice description")
        );

        tableView.setItems(destinations);

        portColumn.setCellFactory(TextFieldTableCell.forTableColumn(new PortConverter()));
    }

    @FXML
    protected void checkAllButtonHandler() {
        System.out.println("ooh");
        statusLabel.setText("ooh");
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

    class PortConverter extends StringConverter<Integer> {

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