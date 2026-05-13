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
import ru.storageplace.dto.StorageOperationView;
import ru.storageplace.model.OperationStatus;
import ru.storageplace.model.StorageOperation;
import ru.storageplace.repository.ProductRepository;
import ru.storageplace.repository.StorageOperationRepository;
import ru.storageplace.repository.StoragePlaceRepository;
import ru.storageplace.service.StorageOperationService;

import java.math.BigDecimal;
import java.util.Optional;

public class StorageOperationTab {
    private final StorageOperationRepository storageOperationRepository = new StorageOperationRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final StoragePlaceRepository storagePlaceRepository = new StoragePlaceRepository();
    private final StorageOperationService storageOperationService = new StorageOperationService();

    private final TableView<StorageOperationView> table = new TableView<>();

    public Tab createTab() {
        configureTable();

        Button createButton = new Button("Рассчитать");
        Button editButton = new Button("Изменить расчёт");
        Button recalculateButton = new Button("Пересчитать");
        Button confirmButton = new Button("Выполнить");
        Button refreshButton = new Button("Обновить");

        createButton.setOnAction(event -> openCreateDialog());
        editButton.setOnAction(event -> openEditSelectedDialog());
        recalculateButton.setOnAction(event -> recalculateSelectedOperation());
        confirmButton.setOnAction(event -> confirmSelectedOperation());
        refreshButton.setOnAction(event -> refresh());

        HBox toolbar = new HBox(
                10,
                createButton,
                editButton,
                recalculateButton,
                confirmButton,
                refreshButton
        );
        toolbar.getStyleClass().add("toolbar");

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(table);
        root.getStyleClass().add("tab-page");
        BorderPane.setMargin(toolbar, new Insets(0,0,5,0));

        Tab tab = new Tab("Операции", root);
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                refresh();
            }
        });

        refresh();

        return tab;
    }

    private void configureTable() {
        TableColumn<StorageOperationView, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<StorageOperationView, String> typeColumn = new TableColumn<>("Тип");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("operationTypeName"));

        TableColumn<StorageOperationView, String> productColumn = new TableColumn<>("Товар");
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productDisplayName"));

        TableColumn<StorageOperationView, String> sourceColumn = new TableColumn<>("Исходное место");
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("sourcePlaceName"));

        TableColumn<StorageOperationView, String> targetColumn = new TableColumn<>("Целевое место");
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("targetPlaceName"));

        TableColumn<StorageOperationView, Integer> quantityColumn = new TableColumn<>("Количество");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<StorageOperationView, BigDecimal> productVolumeColumn = new TableColumn<>("Объём ед.");
        productVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("productVolume"));

        TableColumn<StorageOperationView, BigDecimal> totalVolumeColumn = new TableColumn<>("Общий объём");
        totalVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));

        TableColumn<StorageOperationView, BigDecimal> totalWeightColumn = new TableColumn<>("Общий вес, кг");
        totalWeightColumn.setCellValueFactory(new PropertyValueFactory<>("totalWeightKg"));

        TableColumn<StorageOperationView, String> statusColumn = new TableColumn<>("Статус");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("statusName"));

        TableColumn<StorageOperationView, String> messageColumn = new TableColumn<>("Результат проверки");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("resultMessage"));
        messageColumn.setPrefWidth(280);

        TableColumn<StorageOperationView, String> createdAtColumn = new TableColumn<>("Создана");
        createdAtColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDateTime(cellData.getValue().getCreatedAt()))
        );

        TableColumn<StorageOperationView, String> confirmedAtColumn = new TableColumn<>("Выполнена");
        confirmedAtColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtils.formatDateTime(cellData.getValue().getConfirmedAt()))
        );

        table.getColumns().addAll(
                idColumn,
                typeColumn,
                productColumn,
                sourceColumn,
                targetColumn,
                quantityColumn,
                productVolumeColumn,
                totalVolumeColumn,
                totalWeightColumn,
                statusColumn,
                messageColumn,
                createdAtColumn,
                confirmedAtColumn
        );

        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tableView -> {
            TableRow<StorageOperationView> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialogById(row.getItem().getId());
                }
            });

            return row;
        });
    }

    private void openCreateDialog() {
        StorageOperationDialog dialog = new StorageOperationDialog(
                null,
                productRepository.findAll(),
                storagePlaceRepository.findAll()
        );

        Optional<StorageOperation> result = dialog.showAndWait();

        result.ifPresent(operation -> {
            try {
                StorageOperation savedOperation = storageOperationService.calculateAndSave(
                        operation.getOperationType(),
                        operation.getProductId(),
                        operation.getSourcePlaceId(),
                        operation.getTargetPlaceId(),
                        operation.getQuantity()
                );

                refresh();
                selectById(savedOperation.getId());

                UiUtils.showInfo(
                        "Операция создана",
                        savedOperation.getResultMessage()
                );
            } catch (Exception e) {
                UiUtils.showError("Ошибка создания операции", e);
            }
        });
    }

    private void openEditSelectedDialog() {
        StorageOperationView selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка изменения операции",
                    new IllegalArgumentException("Выберите операцию для изменения")
            );
            return;
        }

        openEditDialogById(selected.getId());
    }

    private void openEditDialogById(Long operationId) {
        StorageOperation operation = storageOperationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Операция хранения не найдена"));

        openEditDialog(operation);
    }

    private void openEditDialog(StorageOperation operation) {
        if (operation.getStatus() == OperationStatus.CONFIRMED) {
            UiUtils.showError(
                    "Ошибка изменения операции",
                    new IllegalStateException("Выполненную операцию нельзя изменить")
            );
            return;
        }

        StorageOperationDialog dialog = new StorageOperationDialog(
                operation,
                productRepository.findAll(),
                storagePlaceRepository.findAll()
        );

        Optional<StorageOperation> result = dialog.showAndWait();

        result.ifPresent(updatedOperation -> {
            try {
                StorageOperation savedOperation = storageOperationService.recalculateAndUpdate(
                        operation.getId(),
                        updatedOperation.getOperationType(),
                        updatedOperation.getProductId(),
                        updatedOperation.getSourcePlaceId(),
                        updatedOperation.getTargetPlaceId(),
                        updatedOperation.getQuantity()
                );

                refresh();
                selectById(savedOperation.getId());

                UiUtils.showInfo(
                        "Операция сохранена",
                        savedOperation.getResultMessage()
                );
            } catch (Exception e) {
                UiUtils.showError("Ошибка сохранения операции", e);
            }
        });
    }

    private void recalculateSelectedOperation() {
        StorageOperationView selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка пересчёта операции",
                    new IllegalArgumentException("Выберите операцию для пересчёта")
            );
            return;
        }

        StorageOperation operation = storageOperationRepository.findById(selected.getId())
                .orElseThrow(() -> new IllegalArgumentException("Операция хранения не найдена"));

        if (operation.getStatus() == OperationStatus.CONFIRMED) {
            UiUtils.showError(
                    "Ошибка пересчёта операции",
                    new IllegalStateException("Выполненную операцию нельзя пересчитать")
            );
            return;
        }

        try {
            StorageOperation recalculatedOperation = storageOperationService.recalculateAndUpdate(
                    operation.getId(),
                    operation.getOperationType(),
                    operation.getProductId(),
                    operation.getSourcePlaceId(),
                    operation.getTargetPlaceId(),
                    operation.getQuantity()
            );

            refresh();
            selectById(recalculatedOperation.getId());

            UiUtils.showInfo(
                    "Операция пересчитана",
                    recalculatedOperation.getResultMessage()
            );
        } catch (Exception e) {
            UiUtils.showError("Ошибка пересчёта операции", e);
        }
    }

    private void confirmSelectedOperation() {
        StorageOperationView selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка выполнения операции",
                    new IllegalArgumentException("Выберите операцию для выполнения")
            );
            return;
        }

        try {
            storageOperationService.confirmOperation(selected.getId());

            refresh();
            selectById(selected.getId());

            UiUtils.showInfo("Операция выполнена", "Операция хранения успешно выполнена");
        } catch (Exception e) {
            UiUtils.showError("Ошибка выполнения операции", e);
        }
    }

    private void selectById(Long id) {
        if (id == null) {
            return;
        }

        for (StorageOperationView operation : table.getItems()) {
            if (id.equals(operation.getId())) {
                table.getSelectionModel().select(operation);
                table.scrollTo(operation);
                return;
            }
        }
    }

    private void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(
                    storageOperationRepository.findAllView()
            ));
        } catch (RuntimeException e) {
            UiUtils.showError("Ошибка загрузки операций хранения", e);
        }
    }
}