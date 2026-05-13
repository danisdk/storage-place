package ru.storageplace.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.storageplace.model.Product;
import ru.storageplace.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class ProductTab {
    private final ProductRepository productRepository = new ProductRepository();
    private final TableView<Product> table = new TableView<>();

    public Tab createTab() {
        configureTable();

        Button createButton = new Button("Создать");
        Button editButton = new Button("Изменить");
        Button deleteButton = new Button("Удалить");
        Button refreshButton = new Button("Обновить");

        createButton.setOnAction(event -> openCreateDialog());
        editButton.setOnAction(event -> openEditSelectedDialog());
        deleteButton.setOnAction(event -> deleteSelectedProduct());
        refreshButton.setOnAction(event -> refresh());

        HBox toolbar = new HBox(10, createButton, editButton, deleteButton, refreshButton);
        toolbar.getStyleClass().add("toolbar");

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(table);
        root.getStyleClass().add("tab-page");
        BorderPane.setMargin(toolbar, new Insets(0,0,5,0));

        Tab tab = new Tab("Товары", root);
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                refresh();
            }
        });

        refresh();

        return tab;
    }

    private void configureTable() {
        TableColumn<Product, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Наименование");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(220);

        TableColumn<Product, String> articleColumn = new TableColumn<>("Артикул");
        articleColumn.setCellValueFactory(new PropertyValueFactory<>("article"));

        TableColumn<Product, BigDecimal> lengthColumn = new TableColumn<>("Длина, см");
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("lengthCm"));

        TableColumn<Product, BigDecimal> widthColumn = new TableColumn<>("Ширина, см");
        widthColumn.setCellValueFactory(new PropertyValueFactory<>("widthCm"));

        TableColumn<Product, BigDecimal> heightColumn = new TableColumn<>("Высота, см");
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("heightCm"));

        TableColumn<Product, BigDecimal> weightColumn = new TableColumn<>("Вес, кг");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weightKg"));

        table.getColumns().setAll(
                idColumn,
                nameColumn,
                articleColumn,
                lengthColumn,
                widthColumn,
                heightColumn,
                weightColumn
        );

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.setRowFactory(tableView -> {
            TableRow<Product> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });

            return row;
        });
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(productRepository.findAll()));
    }

    private void openCreateDialog() {
        ProductDialog dialog = new ProductDialog(null);
        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            try {
                productRepository.create(product);
                refresh();
                selectById(product.getId());
                UiUtils.showInfo("Товар создан", "Товар успешно добавлен");
            } catch (Exception e) {
                UiUtils.showError("Ошибка создания товара", e);
            }
        });
    }

    private void openEditSelectedDialog() {
        Product selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка изменения товара",
                    new IllegalArgumentException("Выберите товар для изменения")
            );
            return;
        }

        openEditDialog(selected);
    }

    private void openEditDialog(Product product) {
        ProductDialog dialog = new ProductDialog(product);
        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(updatedProduct -> {
            try {
                productRepository.update(updatedProduct);
                refresh();
                selectById(updatedProduct.getId());
                UiUtils.showInfo("Товар сохранён", "Изменения успешно сохранены");
            } catch (Exception e) {
                UiUtils.showError("Ошибка сохранения товара", e);
            }
        });
    }

    private void deleteSelectedProduct() {
        Product selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка удаления товара",
                    new IllegalArgumentException("Выберите товар для удаления")
            );
            return;
        }

        if (!UiUtils.confirm("Удаление товара", "Удалить выбранный товар?")) {
            return;
        }

        try {
            productRepository.deleteById(selected.getId());
            refresh();
            UiUtils.showInfo("Товар удалён", "Товар успешно удалён");
        } catch (Exception e) {
            UiUtils.showError("Ошибка удаления товара", e);
        }
    }

    private void selectById(Long id) {
        if (id == null) {
            return;
        }

        for (Product product : table.getItems()) {
            if (id.equals(product.getId())) {
                table.getSelectionModel().select(product);
                table.scrollTo(product);
                return;
            }
        }
    }
}