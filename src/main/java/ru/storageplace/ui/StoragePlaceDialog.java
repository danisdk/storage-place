package ru.storageplace.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import ru.storageplace.model.StoragePlace;
import ru.storageplace.model.StoragePlaceType;
import ru.storageplace.model.Warehouse;

import java.util.List;

public class StoragePlaceDialog extends Dialog<StoragePlace> {
    private final TextField idField = new TextField();
    private final ComboBox<Warehouse> warehouseComboBox = new ComboBox<>();
    private final ComboBox<StoragePlaceType> typeComboBox = new ComboBox<>();
    private final TextField numberField = new TextField();
    private final TextField lengthField = new TextField();
    private final TextField widthField = new TextField();
    private final TextField heightField = new TextField();
    private final TextField maxWeightField = new TextField();

    private final Long storagePlaceId;
    private final ButtonType actionButtonType;

    public StoragePlaceDialog(StoragePlace place,
                              List<Warehouse> warehouses,
                              List<StoragePlaceType> types) {
        boolean editMode = place != null;
        this.storagePlaceId = editMode ? place.getId() : null;

        setTitle(editMode ? "Карточка места хранения" : "Создание места хранения");
        setHeaderText(editMode ? "Изменение места хранения" : "Создание нового места хранения");

        warehouseComboBox.setItems(FXCollections.observableArrayList(warehouses));
        typeComboBox.setItems(FXCollections.observableArrayList(types));

        actionButtonType = new ButtonType(
                editMode ? "Сохранить" : "Создать",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(actionButtonType, cancelButtonType);
        getDialogPane().setContent(createForm());

        if (editMode) {
            fillForm(place);
        }

        Node actionButton = getDialogPane().lookupButton(actionButtonType);
        actionButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                buildStoragePlace();
            } catch (Exception e) {
                UiUtils.showError("Ошибка заполнения места хранения", e);
                event.consume();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == actionButtonType) {
                return buildStoragePlace();
            }

            return null;
        });
        UiUtils.applyDialogStyle(this);
    }

    private GridPane createForm() {
        idField.setEditable(false);
        idField.setPromptText("Автоматически");

        warehouseComboBox.setPrefWidth(220);
        typeComboBox.setPrefWidth(220);
        warehouseComboBox.setPromptText("Выберите склад");
        typeComboBox.setPromptText("Не указан");

        Button clearTypeButton = new Button("Без типа");
        clearTypeButton.setOnAction(event -> typeComboBox.getSelectionModel().clearSelection());

        HBox typeBox = new HBox(8, typeComboBox, clearTypeButton);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(16));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;

        grid.add(new Label("ID"), 0, row);
        grid.add(idField, 1, row++);

        grid.add(new Label("Склад"), 0, row);
        grid.add(warehouseComboBox, 1, row++);

        grid.add(new Label("Тип"), 0, row);
        grid.add(typeBox, 1, row++);

        grid.add(new Label("Номер"), 0, row);
        grid.add(numberField, 1, row++);

        grid.add(new Label("Длина, см"), 0, row);
        grid.add(lengthField, 1, row++);

        grid.add(new Label("Ширина, см"), 0, row);
        grid.add(widthField, 1, row++);

        grid.add(new Label("Высота, см"), 0, row);
        grid.add(heightField, 1, row++);

        grid.add(new Label("Макс. вес, кг"), 0, row);
        grid.add(maxWeightField, 1, row);

        return grid;
    }

    private void fillForm(StoragePlace place) {
        idField.setText(place.getId() == null ? "" : place.getId().toString());
        selectWarehouseById(place.getWarehouseId());
        selectTypeById(place.getTypeId());

        numberField.setText(place.getNumber());
        lengthField.setText(place.getLengthCm().toString());
        widthField.setText(place.getWidthCm().toString());
        heightField.setText(place.getHeightCm().toString());
        maxWeightField.setText(place.getMaxWeightKg().toString());
    }

    private StoragePlace buildStoragePlace() {
        Warehouse warehouse = warehouseComboBox.getSelectionModel().getSelectedItem();

        if (warehouse == null) {
            throw new IllegalArgumentException("Поле \"Склад\" обязательно для заполнения");
        }

        StoragePlaceType type = typeComboBox.getSelectionModel().getSelectedItem();

        StoragePlace place = new StoragePlace();

        place.setId(storagePlaceId);
        place.setWarehouseId(warehouse.getId());
        place.setTypeId(type == null ? null : type.getId());
        place.setNumber(UiUtils.requiredText(numberField, "Номер"));
        place.setLengthCm(UiUtils.requiredDecimal(lengthField, "Длина"));
        place.setWidthCm(UiUtils.requiredDecimal(widthField, "Ширина"));
        place.setHeightCm(UiUtils.requiredDecimal(heightField, "Высота"));
        place.setMaxWeightKg(UiUtils.requiredDecimal(maxWeightField, "Макс. вес"));

        return place;
    }

    private void selectWarehouseById(Long warehouseId) {
        if (warehouseId == null) {
            warehouseComboBox.getSelectionModel().clearSelection();
            return;
        }

        for (Warehouse warehouse : warehouseComboBox.getItems()) {
            if (warehouseId.equals(warehouse.getId())) {
                warehouseComboBox.getSelectionModel().select(warehouse);
                return;
            }
        }
    }

    private void selectTypeById(Long typeId) {
        if (typeId == null) {
            typeComboBox.getSelectionModel().clearSelection();
            return;
        }

        for (StoragePlaceType type : typeComboBox.getItems()) {
            if (typeId.equals(type.getId())) {
                typeComboBox.getSelectionModel().select(type);
                return;
            }
        }
    }
}