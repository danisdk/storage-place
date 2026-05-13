package ru.storageplace.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.storageplace.dto.StoragePlaceStateView;
import ru.storageplace.repository.StoragePlaceStateRepository;

public class StoragePlaceStateTab {
    private final StoragePlaceStateRepository storagePlaceStateRepository = new StoragePlaceStateRepository();

    private final TableView<StoragePlaceStateView> table = new TableView<>();

    public Tab createTab() {
        configureTable();

        Button refreshButton = new Button("Обновить");
        refreshButton.setOnAction(event -> refresh());

        HBox toolbar = new HBox(10, refreshButton);
        toolbar.getStyleClass().add("toolbar");

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(table);
        root.getStyleClass().add("tab-page");
        BorderPane.setMargin(toolbar, new Insets(0,0,5,0));

        Tab tab = new Tab("Состояние мест", root);

        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                refresh();
            }
        });

        refresh();

        return tab;
    }

    private void configureTable() {
        TableColumn<StoragePlaceStateView, String> placeColumn = new TableColumn<>("Место хранения");
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("storagePlaceName"));

        TableColumn<StoragePlaceStateView, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusName"));

        TableColumn<StoragePlaceStateView, String> totalVolumeColumn = new TableColumn<>("Общий объём");
        totalVolumeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDecimal(cellData.getValue().getTotalVolume()))
        );

        TableColumn<StoragePlaceStateView, String> occupiedVolumeColumn = new TableColumn<>("Занятый объём");
        occupiedVolumeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDecimal(cellData.getValue().getOccupiedVolume()))
        );

        TableColumn<StoragePlaceStateView, String> freeVolumeColumn = new TableColumn<>("Свободный объём");
        freeVolumeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDecimal(cellData.getValue().getFreeVolume()))
        );

        TableColumn<StoragePlaceStateView, String> maxWeightColumn = new TableColumn<>("Макс. вес, кг");
        maxWeightColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDecimal(cellData.getValue().getMaxWeightKg()))
        );

        TableColumn<StoragePlaceStateView, String> occupiedWeightColumn = new TableColumn<>("Занятый вес, кг");
        occupiedWeightColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDecimal(cellData.getValue().getOccupiedWeightKg()))
        );

        TableColumn<StoragePlaceStateView, String> freeWeightColumn = new TableColumn<>("Доступный вес, кг");
        freeWeightColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDecimal(cellData.getValue().getFreeWeightKg()))
        );

        TableColumn<StoragePlaceStateView, String> updatedAtColumn = new TableColumn<>("Дата обновления");
        updatedAtColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDateTime(cellData.getValue().getUpdatedAt()))
        );

        table.getColumns().addAll(
                placeColumn,
                statusColumn,
                totalVolumeColumn,
                occupiedVolumeColumn,
                freeVolumeColumn,
                maxWeightColumn,
                occupiedWeightColumn,
                freeWeightColumn,
                updatedAtColumn
        );

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(
                    storagePlaceStateRepository.findAllView()
            ));
        } catch (RuntimeException e) {
            UiUtils.showError("Ошибка загрузки состояния мест хранения", e);
        }
    }
}