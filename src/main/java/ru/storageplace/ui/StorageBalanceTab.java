package ru.storageplace.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import ru.storageplace.dto.StorageBalanceView;
import ru.storageplace.repository.StorageBalanceRepository;

import java.math.BigDecimal;

public class StorageBalanceTab {
    private final StorageBalanceRepository storageBalanceRepository = new StorageBalanceRepository();

    private final TableView<StorageBalanceView> table = new TableView<>();

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

        refresh();

        return new Tab("Остатки", root);
    }

    private void configureTable() {
        TableColumn<StorageBalanceView, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<StorageBalanceView, String> placeColumn = new TableColumn<>("Место хранения");
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("storagePlaceName"));

        TableColumn<StorageBalanceView, String> productColumn = new TableColumn<>("Товар");
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productDisplayName"));

        TableColumn<StorageBalanceView, Integer> quantityColumn = new TableColumn<>("Количество");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<StorageBalanceView, BigDecimal> volumeColumn = new TableColumn<>("Общий объём");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));

        TableColumn<StorageBalanceView, BigDecimal> weightColumn = new TableColumn<>("Общий вес, кг");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("totalWeightKg"));

        table.getColumns().addAll(
                idColumn,
                placeColumn,
                productColumn,
                quantityColumn,
                volumeColumn,
                weightColumn
        );

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(
                    storageBalanceRepository.findAllView()
            ));
        } catch (RuntimeException e) {
            UiUtils.showError("Ошибка загрузки остатков товаров", e);
        }
    }
}