module io.momonet.portcheckerv2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;

    opens io.momonet.portcheckerv2 to javafx.fxml;
    exports io.momonet.portcheckerv2;
}