package io.momonet.portcheckerv2;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Source https://stackoverflow.com/a/49066796/5431968
 */
public class ActionButtonTableCell<S> extends TableCell<S, Button> {
    private final Button actionButton;

    public ActionButtonTableCell(String label, BiFunction<S, Button, S> function) {
        this.getStyleClass().add("action-button-table-cell");

        this.actionButton = new Button(label);
        this.actionButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem(), this.actionButton);
        });
        this.actionButton.setMaxWidth(Double.MAX_VALUE);
    }

    public S getCurrentItem() {
        return (S) getTableView().getItems().get(getIndex());
    }

    @Override
    protected void updateItem(Button button, boolean b) {
        super.updateItem(button, b);

        if (b) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> forTableColumn(String label, BiFunction< S, Button, S> function) {
        return param -> new ActionButtonTableCell<>(label, function);
    }
}
