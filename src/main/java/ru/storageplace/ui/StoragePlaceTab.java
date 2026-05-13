package ru.storageplace.ui;

import javafx.beans.property.SimpleStringProperty;
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
import ru.storageplace.model.StoragePlace;
import ru.storageplace.model.StoragePlaceType;
import ru.storageplace.model.Warehouse;
import ru.storageplace.repository.StoragePlaceRepository;
import ru.storageplace.repository.StoragePlaceTypeRepository;
import ru.storageplace.repository.WarehouseRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StoragePlaceTab {
    private final StoragePlaceRepository storagePlaceRepository = new StoragePlaceRepository();
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();
    private final StoragePlaceTypeRepository storagePlaceTypeRepository = new StoragePlaceTypeRepository();

    private final TableView<StoragePlace> table = new TableView<>();

    private Map<Long, Warehouse> warehouseById = Map.of();
    private Map<Long, StoragePlaceType> typeById = Map.of();

    public Tab createTab() {
        configureTable();

        Button createButton = new Button("Создать");
        Button editButton = new Button("Изменить");
        Button deleteButton = new Button("Удалить");
        Button refreshButton = new Button("Обновить");

        createButton.setOnAction(event -> openCreateDialog());
        editButton.setOnAction(event -> openEditSelectedDialog());
        deleteButton.setOnAction(event -> deleteSelectedPlace());
        refreshButton.setOnAction(event -> refresh());

        HBox toolbar = new HBox(10, createButton, editButton, deleteButton, refreshButton);
        toolbar.getStyleClass().add("toolbar");

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(table);
        root.getStyleClass().add("tab-page");
        BorderPane.setMargin(toolbar, new Insets(0,0,5,0));

        Tab tab = new Tab("Места хранения", root);
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                refresh();
            }
        });

        refresh();

        return tab;
    }

    private void configureTable() {
        TableColumn<StoragePlace, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<StoragePlace, String> warehouseColumn = new TableColumn<>("Склад");
        warehouseColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(getWarehouseName(cellData.getValue().getWarehouseId()))
        );
        warehouseColumn.setPrefWidth(180);

        TableColumn<StoragePlace, String> typeColumn = new TableColumn<>("Тип");
        typeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(getTypeName(cellData.getValue().getTypeId()))
        );
        typeColumn.setPrefWidth(180);

        TableColumn<StoragePlace, String> numberColumn = new TableColumn<>("Номер");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<StoragePlace, BigDecimal> lengthColumn = new TableColumn<>("Длина, см");
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("lengthCm"));

        TableColumn<StoragePlace, BigDecimal> widthColumn = new TableColumn<>("Ширина, см");
        widthColumn.setCellValueFactory(new PropertyValueFactory<>("widthCm"));

        TableColumn<StoragePlace, BigDecimal> heightColumn = new TableColumn<>("Высота, см");
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("heightCm"));

        TableColumn<StoragePlace, BigDecimal> maxWeightColumn = new TableColumn<>("Макс. вес, кг");
        maxWeightColumn.setCellValueFactory(new PropertyValueFactory<>("maxWeightKg"));

        table.getColumns().setAll(
                idColumn,
                warehouseColumn,
                typeColumn,
                numberColumn,
                lengthColumn,
                widthColumn,
                heightColumn,
                maxWeightColumn
        );

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.setRowFactory(tableView -> {
            TableRow<StoragePlace> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });

            return row;
        });
    }

    private void refresh() {
        loadDictionaries();
        table.setItems(FXCollections.observableArrayList(storagePlaceRepository.findAll()));
    }

    private void loadDictionaries() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<StoragePlaceType> types = storagePlaceTypeRepository.findAll();

        warehouseById = warehouses.stream()
                .collect(Collectors.toMap(Warehouse::getId, warehouse -> warehouse));

        typeById = types.stream()
                .collect(Collectors.toMap(StoragePlaceType::getId, type -> type));
    }

    private void openCreateDialog() {
        StoragePlaceDialog dialog = new StoragePlaceDialog(
                null,
                warehouseRepository.findAll(),
                storagePlaceTypeRepository.findAll()
        );

        Optional<StoragePlace> result = dialog.showAndWait();

        result.ifPresent(place -> {
            try {
                storagePlaceRepository.create(place);
                refresh();
                selectById(place.getId());
                UiUtils.showInfo("Место хранения создано", "Место хранения успешно добавлено");
            } catch (Exception e) {
                UiUtils.showError("Ошибка создания места хранения", e);
            }
        });
    }

    private void openEditSelectedDialog() {
        StoragePlace selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка изменения места хранения",
                    new IllegalArgumentException("Выберите место хранения для изменения")
            );
            return;
        }

        openEditDialog(selected);
    }

    private void openEditDialog(StoragePlace place) {
        StoragePlaceDialog dialog = new StoragePlaceDialog(
                place,
                warehouseRepository.findAll(),
                storagePlaceTypeRepository.findAll()
        );

        Optional<StoragePlace> result = dialog.showAndWait();

        result.ifPresent(updatedPlace -> {
            try {
                storagePlaceRepository.update(updatedPlace);
                refresh();
                selectById(updatedPlace.getId());
                UiUtils.showInfo("Место хранения сохранено", "Изменения успешно сохранены");
            } catch (Exception e) {
                UiUtils.showError("Ошибка сохранения места хранения", e);
            }
        });
    }

    private void deleteSelectedPlace() {
        StoragePlace selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка удаления места хранения",
                    new IllegalArgumentException("Выберите место хранения для удаления")
            );
            return;
        }

        if (!UiUtils.confirm("Удаление места хранения", "Удалить выбранное место хранения?")) {
            return;
        }

        try {
            storagePlaceRepository.deleteById(selected.getId());
            refresh();
            UiUtils.showInfo("Место хранения удалено", "Место хранения успешно удалено");
        } catch (Exception e) {
            UiUtils.showError("Ошибка удаления места хранения", e);
        }
    }

    private String getWarehouseName(Long warehouseId) {
        Warehouse warehouse = warehouseById.get(warehouseId);

        if (warehouse == null) {
            return warehouseId == null ? "" : "ID " + warehouseId;
        }

        return warehouse.getName();
    }

    private String getTypeName(Long typeId) {
        if (typeId == null) {
            return "";
        }

        StoragePlaceType type = typeById.get(typeId);

        if (type == null) {
            return "ID " + typeId;
        }

        return type.getCode() + " — " + type.getName();
    }

    private void selectById(Long id) {
        if (id == null) {
            return;
        }

        for (StoragePlace place : table.getItems()) {
            if (id.equals(place.getId())) {
                table.getSelectionModel().select(place);
                table.scrollTo(place);
                return;
            }
        }
    }
}