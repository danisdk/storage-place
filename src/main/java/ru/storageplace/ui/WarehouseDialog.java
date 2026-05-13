package ru.storageplace.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.storageplace.model.Warehouse;

public class WarehouseDialog extends Dialog<Warehouse> {
    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField addressField = new TextField();
    private final TextField descriptionField = new TextField();

    private final Long warehouseId;
    private final ButtonType actionButtonType;

    public WarehouseDialog(Warehouse warehouse) {
        boolean editMode = warehouse != null;
        this.warehouseId = editMode ? warehouse.getId() : null;

        setTitle(editMode ? "Карточка склада" : "Создание склада");
        setHeaderText(editMode ? "Изменение данных склада" : "Создание нового склада");

        actionButtonType = new ButtonType(
                editMode ? "Сохранить" : "Создать",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(actionButtonType, cancelButtonType);
        getDialogPane().setContent(createForm());

        if (editMode) {
            fillForm(warehouse);
        }

        Node actionButton = getDialogPane().lookupButton(actionButtonType);
        actionButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                buildWarehouse();
            } catch (Exception e) {
                UiUtils.showError("Ошибка заполнения склада", e);
                event.consume();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == actionButtonType) {
                return buildWarehouse();
            }

            return null;
        });
        UiUtils.applyDialogStyle(this);
    }

    private GridPane createForm() {
        idField.setEditable(false);
        idField.setPromptText("Автоматически");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(16));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;

        grid.add(new Label("ID"), 0, row);
        grid.add(idField, 1, row++);

        grid.add(new Label("Наименование"), 0, row);
        grid.add(nameField, 1, row++);

        grid.add(new Label("Адрес"), 0, row);
        grid.add(addressField, 1, row++);

        grid.add(new Label("Описание"), 0, row);
        grid.add(descriptionField, 1, row);

        return grid;
    }

    private void fillForm(Warehouse warehouse) {
        idField.setText(warehouse.getId() == null ? "" : warehouse.getId().toString());
        nameField.setText(warehouse.getName());
        addressField.setText(warehouse.getAddress() == null ? "" : warehouse.getAddress());
        descriptionField.setText(warehouse.getDescription() == null ? "" : warehouse.getDescription());
    }

    private Warehouse buildWarehouse() {
        Warehouse warehouse = new Warehouse();

        warehouse.setId(warehouseId);
        warehouse.setName(UiUtils.requiredText(nameField, "Наименование"));
        warehouse.setAddress(UiUtils.optionalText(addressField));
        warehouse.setDescription(UiUtils.optionalText(descriptionField));

        return warehouse;
    }
}