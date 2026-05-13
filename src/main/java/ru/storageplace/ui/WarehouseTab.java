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
import ru.storageplace.model.Warehouse;
import ru.storageplace.repository.WarehouseRepository;

import java.util.Optional;

public class WarehouseTab {
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();
    private final TableView<Warehouse> table = new TableView<>();

    public Tab createTab() {
        configureTable();

        Button createButton = new Button("Создать");
        Button editButton = new Button("Изменить");
        Button deleteButton = new Button("Удалить");
        Button refreshButton = new Button("Обновить");

        createButton.setOnAction(event -> openCreateDialog());
        editButton.setOnAction(event -> openEditSelectedDialog());
        deleteButton.setOnAction(event -> deleteSelectedWarehouse());
        refreshButton.setOnAction(event -> refresh());

        HBox toolbar = new HBox(10, createButton, editButton, deleteButton, refreshButton);
        toolbar.getStyleClass().add("toolbar");
        BorderPane.setMargin(toolbar, new Insets(0,0,5,0));

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(table);
        root.getStyleClass().add("tab-page");

        Tab tab = new Tab("Склады", root);
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                refresh();
            }
        });

        refresh();

        return tab;
    }

    private void configureTable() {
        TableColumn<Warehouse, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Warehouse, String> nameColumn = new TableColumn<>("Наименование");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(220);

        TableColumn<Warehouse, String> addressColumn = new TableColumn<>("Адрес");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressColumn.setPrefWidth(260);

        TableColumn<Warehouse, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(300);

        table.getColumns().setAll(idColumn, nameColumn, addressColumn, descriptionColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.setRowFactory(tableView -> {
            TableRow<Warehouse> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });

            return row;
        });
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(warehouseRepository.findAll()));
    }

    private void openCreateDialog() {
        WarehouseDialog dialog = new WarehouseDialog(null);
        Optional<Warehouse> result = dialog.showAndWait();

        result.ifPresent(warehouse -> {
            try {
                warehouseRepository.create(warehouse);
                refresh();
                selectById(warehouse.getId());
                UiUtils.showInfo("Склад создан", "Склад успешно добавлен");
            } catch (Exception e) {
                UiUtils.showError("Ошибка создания склада", e);
            }
        });
    }

    private void openEditSelectedDialog() {
        Warehouse selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка изменения склада",
                    new IllegalArgumentException("Выберите склад для изменения")
            );
            return;
        }

        openEditDialog(selected);
    }

    private void openEditDialog(Warehouse warehouse) {
        WarehouseDialog dialog = new WarehouseDialog(warehouse);
        Optional<Warehouse> result = dialog.showAndWait();

        result.ifPresent(updatedWarehouse -> {
            try {
                warehouseRepository.update(updatedWarehouse);
                refresh();
                selectById(updatedWarehouse.getId());
                UiUtils.showInfo("Склад сохранён", "Изменения успешно сохранены");
            } catch (Exception e) {
                UiUtils.showError("Ошибка сохранения склада", e);
            }
        });
    }

    private void deleteSelectedWarehouse() {
        Warehouse selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка удаления склада",
                    new IllegalArgumentException("Выберите склад для удаления")
            );
            return;
        }

        if (!UiUtils.confirm("Удаление склада", "Удалить выбранный склад?")) {
            return;
        }

        try {
            warehouseRepository.deleteById(selected.getId());
            refresh();
            UiUtils.showInfo("Склад удалён", "Склад успешно удалён");
        } catch (Exception e) {
            UiUtils.showError("Ошибка удаления склада", e);
        }
    }

    private void selectById(Long id) {
        if (id == null) {
            return;
        }

        for (Warehouse warehouse : table.getItems()) {
            if (id.equals(warehouse.getId())) {
                table.getSelectionModel().select(warehouse);
                table.scrollTo(warehouse);
                return;
            }
        }
    }
}