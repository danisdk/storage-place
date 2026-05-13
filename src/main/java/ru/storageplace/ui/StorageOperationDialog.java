package ru.storageplace.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.storageplace.model.OperationType;
import ru.storageplace.model.Product;
import ru.storageplace.model.StorageOperation;
import ru.storageplace.model.StoragePlace;

import java.util.List;

public class StorageOperationDialog extends Dialog<StorageOperation> {
    private final TextField idField = new TextField();
    private final ComboBox<OperationType> operationTypeComboBox = new ComboBox<>();
    private final ComboBox<Product> productComboBox = new ComboBox<>();
    private final ComboBox<StoragePlace> sourcePlaceComboBox = new ComboBox<>();
    private final ComboBox<StoragePlace> targetPlaceComboBox = new ComboBox<>();
    private final TextField quantityField = new TextField();

    private final Long operationId;
    private final ButtonType actionButtonType;

    public StorageOperationDialog(StorageOperation operation,
                                  List<Product> products,
                                  List<StoragePlace> places) {
        boolean editMode = operation != null;
        this.operationId = editMode ? operation.getId() : null;

        setTitle(editMode ? "Карточка операции хранения" : "Создание операции хранения");
        setHeaderText(editMode ? "Изменение операции хранения" : "Создание новой операции хранения");

        operationTypeComboBox.setItems(FXCollections.observableArrayList(OperationType.values()));
        productComboBox.setItems(FXCollections.observableArrayList(products));
        sourcePlaceComboBox.setItems(FXCollections.observableArrayList(places));
        targetPlaceComboBox.setItems(FXCollections.observableArrayList(places));

        actionButtonType = new ButtonType(
                editMode ? "Пересчитать" : "Рассчитать",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(actionButtonType, cancelButtonType);
        getDialogPane().setContent(createForm());

        configureBehavior();

        if (editMode) {
            fillForm(operation);
        }

        Node actionButton = getDialogPane().lookupButton(actionButtonType);
        actionButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                buildOperation();
            } catch (Exception e) {
                UiUtils.showError("Ошибка заполнения операции", e);
                event.consume();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == actionButtonType) {
                return buildOperation();
            }

            return null;
        });
        UiUtils.applyDialogStyle(this);
    }

    private GridPane createForm() {
        idField.setEditable(false);
        idField.setPromptText("Автоматически");

        operationTypeComboBox.setPrefWidth(260);
        productComboBox.setPrefWidth(260);
        sourcePlaceComboBox.setPrefWidth(260);
        targetPlaceComboBox.setPrefWidth(260);
        quantityField.setPrefWidth(260);

        operationTypeComboBox.setPromptText("Выберите тип операции");
        productComboBox.setPromptText("Выберите товар");
        sourcePlaceComboBox.setPromptText("Выберите исходное место");
        targetPlaceComboBox.setPromptText("Выберите целевое место");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(16));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;

        grid.add(new Label("ID"), 0, row);
        grid.add(idField, 1, row++);

        grid.add(new Label("Тип операции"), 0, row);
        grid.add(operationTypeComboBox, 1, row++);

        grid.add(new Label("Товар"), 0, row);
        grid.add(productComboBox, 1, row++);

        grid.add(new Label("Количество"), 0, row);
        grid.add(quantityField, 1, row++);

        grid.add(new Label("Исходное место"), 0, row);
        grid.add(sourcePlaceComboBox, 1, row++);

        grid.add(new Label("Целевое место"), 0, row);
        grid.add(targetPlaceComboBox, 1, row);

        return grid;
    }

    private void configureBehavior() {
        operationTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
                updatePlaceFields(newValue)
        );
    }

    private void updatePlaceFields(OperationType operationType) {
        if (operationType == null) {
            sourcePlaceComboBox.setDisable(false);
            targetPlaceComboBox.setDisable(false);
            return;
        }

        switch (operationType) {
            case INCOME -> {
                sourcePlaceComboBox.getSelectionModel().clearSelection();
                sourcePlaceComboBox.setDisable(true);
                targetPlaceComboBox.setDisable(false);
            }
            case OUTCOME -> {
                sourcePlaceComboBox.setDisable(false);
                targetPlaceComboBox.getSelectionModel().clearSelection();
                targetPlaceComboBox.setDisable(true);
            }
            case TRANSFER -> {
                sourcePlaceComboBox.setDisable(false);
                targetPlaceComboBox.setDisable(false);
            }
        }
    }

    private void fillForm(StorageOperation operation) {
        idField.setText(operation.getId() == null ? "" : operation.getId().toString());
        operationTypeComboBox.getSelectionModel().select(operation.getOperationType());
        selectProductById(operation.getProductId());
        selectSourcePlaceById(operation.getSourcePlaceId());
        selectTargetPlaceById(operation.getTargetPlaceId());
        quantityField.setText(operation.getQuantity() == null ? "" : operation.getQuantity().toString());

        updatePlaceFields(operation.getOperationType());
    }

    private StorageOperation buildOperation() {
        OperationType operationType = operationTypeComboBox.getSelectionModel().getSelectedItem();

        if (operationType == null) {
            throw new IllegalArgumentException("Выберите тип операции");
        }

        Product product = productComboBox.getSelectionModel().getSelectedItem();

        if (product == null) {
            throw new IllegalArgumentException("Выберите товар");
        }

        StoragePlace sourcePlace = sourcePlaceComboBox.getSelectionModel().getSelectedItem();
        StoragePlace targetPlace = targetPlaceComboBox.getSelectionModel().getSelectedItem();

        StorageOperation operation = new StorageOperation();

        operation.setId(operationId);
        operation.setOperationType(operationType);
        operation.setProductId(product.getId());
        operation.setSourcePlaceId(sourcePlace == null ? null : sourcePlace.getId());
        operation.setTargetPlaceId(targetPlace == null ? null : targetPlace.getId());
        operation.setQuantity(UiUtils.requiredInteger(quantityField, "Количество"));

        return operation;
    }

    private void selectProductById(Long productId) {
        if (productId == null) {
            productComboBox.getSelectionModel().clearSelection();
            return;
        }

        for (Product product : productComboBox.getItems()) {
            if (productId.equals(product.getId())) {
                productComboBox.getSelectionModel().select(product);
                return;
            }
        }
    }

    private void selectSourcePlaceById(Long placeId) {
        if (placeId == null) {
            sourcePlaceComboBox.getSelectionModel().clearSelection();
            return;
        }

        for (StoragePlace place : sourcePlaceComboBox.getItems()) {
            if (placeId.equals(place.getId())) {
                sourcePlaceComboBox.getSelectionModel().select(place);
                return;
            }
        }
    }

    private void selectTargetPlaceById(Long placeId) {
        if (placeId == null) {
            targetPlaceComboBox.getSelectionModel().clearSelection();
            return;
        }

        for (StoragePlace place : targetPlaceComboBox.getItems()) {
            if (placeId.equals(place.getId())) {
                targetPlaceComboBox.getSelectionModel().select(place);
                return;
            }
        }
    }
}