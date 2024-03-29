package io.momonet.portcheckerv2;

import io.momonet.portcheckerv2.models.Destination;
import io.momonet.portcheckerv2.models.Status;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;

public class HelloController {

    private ObservableList<Destination> destinations = FXCollections.observableArrayList();

    @FXML
    private TableColumn portColumn;
    @FXML
    private TableColumn actionColumn;

    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableView<Destination> tableView;

    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField path;

    @FXML private Button loadFromFile;

    final FileChooser fileChooser = new FileChooser();


    public HelloController() {
    }

    @FXML
    public void initialize() throws IOException, URISyntaxException {
        progressBar.setVisible(false);

//        destinations.addAll(
//                new Destination("google.com", 443, "This is an example"),
//                new Destination("checkip.amazonaws.com", 80, "A site to find your public IP Address")
//        );

        tableView.setItems(destinations);
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Destination destination, boolean empty) {
                super.updateItem(destination, empty);
                if (!empty) {
                    if (!isSelected()) {
                        switch (destination.getStatus()) {
                            case REACHABLE -> setStyle("-fx-background-color: #ceffce;");
                            case UNREACHABLE -> setStyle("-fx-background-color: #ffc9c9;");
                        }
                    } else {
                        setStyle("-fx-background-color: -fx-background, -fx-cell-focus-inner-border, -fx-background;");
                    }

                }
            }
        });

        portColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerConverter()));
        actionColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Check", this::onCheckRow));

        Platform.runLater(() -> {
            tableView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
                Node source = evt.getPickResult().getIntersectedNode();

                // move up through the node hierarchy until a TableRow or scene root is found
                while (source != null && !(source instanceof TableRow)) {
                    source = source.getParent();
                }


                // clear selection on click anywhere but on a filled row
                if (source == null || (source instanceof TableRow && ((TableRow) source).isEmpty())) {
                    tableView.getSelectionModel().clearSelection();
                }
            });
        });
        loadFromFile.setOnAction(actionEvent -> {
            try {
                File file = new File(path.getText());
                if (file != null && file.exists()) {
                    FileReader reader = new FileReader(file);
                    var br = new BufferedReader(reader);
                    readDestinations(br);
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("File not found");
                    alert.setContentText("File not found");
                    alert.show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @FXML
    protected void checkAllButtonHandler() {
        var task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                var length = destinations.size();
                tableView.setEditable(false);
                Platform.runLater(() -> {
                    statusLabel.setText("");
                    progressBar.setVisible(true);
                });
                for (int i = 0; i < length; i++) {
                    var d = destinations.get(i);
                    try {
                        try (Socket soc = new Socket()) {
                            soc.connect(new InetSocketAddress(d.getHost(), d.getPort()), 3000);
                        }
                        d.setStatus(Status.REACHABLE);
                    } catch (IOException ex) {
                        d.setStatus(Status.UNREACHABLE);
                    } finally {
                        updateProgress(i + 1, length);
                    }
                }
                tableView.setEditable(true);
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Checked reachability for " + length + " destinations");
                    tableView.refresh();
                });
                return null;
            }
        };
        progressBar.progressProperty().bind(task.progressProperty());
        var thread = new Thread(task);
        thread.start();
    }

    @FXML
    protected void addRowHandler() {
        var destination = new Destination();
        destination.setHost(hostTextField.getText());
        destination.setPort(Integer.parseInt(portTextField.getText()));
        destination.setDescription(descriptionTextField.getText());
        destination.setStatus(Status.UNKNOWN);
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

    public void chooseFile(ActionEvent actionEvent) throws IOException {
        var extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            var br = new BufferedReader(new FileReader(file));
            readDestinations(br);
        }
    }

    private void readDestinations(BufferedReader br) throws IOException {
        br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            var host = values[0];
            var port = values[1];
            destinations.add(new Destination(host, port, values.length == 3 ? values[2] : ""));
        }
    }

    public void exportToFile(ActionEvent actionEvent) throws IOException {
        var extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        var file = fileChooser.showOpenDialog(null);
        if (file != null) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            var str = new StringBuilder();
            str.append("host,port,description\n");
            for (Destination d : destinations) {
                str.append(d.getHost() + "," + d.getPort().toString() + "," + d.getDescription() + "\n");
            }
            writer.write(str.toString());
            writer.close();
            statusLabel.setText("Successfully exported to " + file.getPath());
        }
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