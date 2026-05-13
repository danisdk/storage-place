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
import ru.storageplace.model.Product;

public class ProductDialog extends Dialog<Product> {
    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField articleField = new TextField();
    private final TextField lengthField = new TextField();
    private final TextField widthField = new TextField();
    private final TextField heightField = new TextField();
    private final TextField weightField = new TextField();

    private final Long productId;
    private final ButtonType actionButtonType;

    public ProductDialog(Product product) {
        boolean editMode = product != null;
        this.productId = editMode ? product.getId() : null;

        setTitle(editMode ? "Карточка товара" : "Создание товара");
        setHeaderText(editMode ? "Изменение данных товара" : "Создание нового товара");

        actionButtonType = new ButtonType(
                editMode ? "Сохранить" : "Создать",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButtonType = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(actionButtonType, cancelButtonType);
        getDialogPane().setContent(createForm());

        if (editMode) {
            fillForm(product);
        }

        Node actionButton = getDialogPane().lookupButton(actionButtonType);
        actionButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                buildProduct();
            } catch (Exception e) {
                UiUtils.showError("Ошибка заполнения товара", e);
                event.consume();
            }
        });

        setResultConverter(buttonType -> {
            if (buttonType == actionButtonType) {
                return buildProduct();
            }

            return null;
        });
//        String url = UiUtils.getAppStylesheet();

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

        grid.add(new Label("Артикул"), 0, row);
        grid.add(articleField, 1, row++);

        grid.add(new Label("Длина, см"), 0, row);
        grid.add(lengthField, 1, row++);

        grid.add(new Label("Ширина, см"), 0, row);
        grid.add(widthField, 1, row++);

        grid.add(new Label("Высота, см"), 0, row);
        grid.add(heightField, 1, row++);

        grid.add(new Label("Вес, кг"), 0, row);
        grid.add(weightField, 1, row);

        return grid;
    }

    private void fillForm(Product product) {
        idField.setText(product.getId() == null ? "" : product.getId().toString());
        nameField.setText(product.getName());
        articleField.setText(product.getArticle());
        lengthField.setText(product.getLengthCm().toString());
        widthField.setText(product.getWidthCm().toString());
        heightField.setText(product.getHeightCm().toString());
        weightField.setText(product.getWeightKg().toString());
    }

    private Product buildProduct() {
        Product product = new Product();

        product.setId(productId);
        product.setName(UiUtils.requiredText(nameField, "Наименование"));
        product.setArticle(UiUtils.requiredText(articleField, "Артикул"));
        product.setLengthCm(UiUtils.requiredDecimal(lengthField, "Длина"));
        product.setWidthCm(UiUtils.requiredDecimal(widthField, "Ширина"));
        product.setHeightCm(UiUtils.requiredDecimal(heightField, "Высота"));
        product.setWeightKg(UiUtils.requiredDecimal(weightField, "Вес"));

        return product;
    }
}