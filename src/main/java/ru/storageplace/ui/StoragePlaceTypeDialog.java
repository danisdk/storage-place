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
import ru.storageplace.model.StoragePlaceType;

public class StoragePlaceTypeDialog extends Dialog<StoragePlaceType> {
    private final TextField idField = new TextField();
    private final TextField codeField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField descriptionField = new TextField();

    private final Long typeId;
    private final ButtonType actionButtonType;

    public StoragePlaceTypeDialog(StoragePlaceType type) {
        boolean editMode = type != null;
        this.typeId = editMode ? type.getId() : null;

        setTitle(editMode ? "Карточка типа места хранения" : "Создание типа места хранения");
        setHeaderText(editMode ? "Изменение типа места хранения" : "Создание нового типа места хранения");

        actionButtonType = new ButtonType(
                editMode ? "Сохранить" : "Создать",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(actionButtonType, cancelButtonType);
        getDialogPane().setContent(createForm());

        if (editMode) {
            fillForm(type);
        }

        Node actionButton = getDialogPane().lookupButton(actionButtonType);
        actionButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                buildStoragePlaceType();
            } catch (Exception e) {
                UiUtils.showError("Ошибка заполнения типа места хранения", e);
                event.consume();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == actionButtonType) {
                return buildStoragePlaceType();
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

        grid.add(new Label("Код"), 0, row);
        grid.add(codeField, 1, row++);

        grid.add(new Label("Наименование"), 0, row);
        grid.add(nameField, 1, row++);

        grid.add(new Label("Описание"), 0, row);
        grid.add(descriptionField, 1, row);

        return grid;
    }

    private void fillForm(StoragePlaceType type) {
        idField.setText(type.getId() == null ? "" : type.getId().toString());
        codeField.setText(type.getCode());
        nameField.setText(type.getName());
        descriptionField.setText(type.getDescription() == null ? "" : type.getDescription());
    }

    private StoragePlaceType buildStoragePlaceType() {
        StoragePlaceType type = new StoragePlaceType();

        type.setId(typeId);
        type.setCode(UiUtils.requiredText(codeField, "Код"));
        type.setName(UiUtils.requiredText(nameField, "Наименование"));
        type.setDescription(UiUtils.optionalText(descriptionField));

        return type;
    }
}