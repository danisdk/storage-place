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
import ru.storageplace.model.StoragePlaceType;
import ru.storageplace.repository.StoragePlaceTypeRepository;

import java.util.Optional;

public class StoragePlaceTypeTab {
    private final StoragePlaceTypeRepository storagePlaceTypeRepository = new StoragePlaceTypeRepository();
    private final TableView<StoragePlaceType> table = new TableView<>();

    public Tab createTab() {
        configureTable();

        Button createButton = new Button("Создать");
        Button editButton = new Button("Изменить");
        Button deleteButton = new Button("Удалить");
        Button refreshButton = new Button("Обновить");

        createButton.setOnAction(event -> openCreateDialog());
        editButton.setOnAction(event -> openEditSelectedDialog());
        deleteButton.setOnAction(event -> deleteSelectedType());
        refreshButton.setOnAction(event -> refresh());

        HBox toolbar = new HBox(10, createButton, editButton, deleteButton, refreshButton);
        toolbar.getStyleClass().add("toolbar");

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(table);
        root.getStyleClass().add("tab-page");
        BorderPane.setMargin(toolbar, new Insets(0,0,5,0));

        Tab tab = new Tab("Типы мест хранения", root);
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                refresh();
            }
        });

        refresh();

        return tab;
    }

    private void configureTable() {
        TableColumn<StoragePlaceType, Long> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<StoragePlaceType, String> codeColumn = new TableColumn<>("Код");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<StoragePlaceType, String> nameColumn = new TableColumn<>("Наименование");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(220);

        TableColumn<StoragePlaceType, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(320);

        table.getColumns().setAll(idColumn, codeColumn, nameColumn, descriptionColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.setRowFactory(tableView -> {
            TableRow<StoragePlaceType> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });

            return row;
        });
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(storagePlaceTypeRepository.findAll()));
    }

    private void openCreateDialog() {
        StoragePlaceTypeDialog dialog = new StoragePlaceTypeDialog(null);
        Optional<StoragePlaceType> result = dialog.showAndWait();

        result.ifPresent(type -> {
            try {
                storagePlaceTypeRepository.create(type);
                refresh();
                selectById(type.getId());
                UiUtils.showInfo("Тип места хранения создан", "Тип места хранения успешно добавлен");
            } catch (Exception e) {
                UiUtils.showError("Ошибка создания типа места хранения", e);
            }
        });
    }

    private void openEditSelectedDialog() {
        StoragePlaceType selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка изменения типа места хранения",
                    new IllegalArgumentException("Выберите тип места хранения для изменения")
            );
            return;
        }

        openEditDialog(selected);
    }

    private void openEditDialog(StoragePlaceType type) {
        StoragePlaceTypeDialog dialog = new StoragePlaceTypeDialog(type);
        Optional<StoragePlaceType> result = dialog.showAndWait();

        result.ifPresent(updatedType -> {
            try {
                storagePlaceTypeRepository.update(updatedType);
                refresh();
                selectById(updatedType.getId());
                UiUtils.showInfo("Тип места хранения сохранён", "Изменения успешно сохранены");
            } catch (Exception e) {
                UiUtils.showError("Ошибка сохранения типа места хранения", e);
            }
        });
    }

    private void deleteSelectedType() {
        StoragePlaceType selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            UiUtils.showError(
                    "Ошибка удаления типа места хранения",
                    new IllegalArgumentException("Выберите тип места хранения для удаления")
            );
            return;
        }

        if (!UiUtils.confirm("Удаление типа места хранения", "Удалить выбранный тип места хранения?")) {
            return;
        }

        try {
            storagePlaceTypeRepository.deleteById(selected.getId());
            refresh();
            UiUtils.showInfo("Тип места хранения удалён", "Тип места хранения успешно удалён");
        } catch (Exception e) {
            UiUtils.showError("Ошибка удаления типа места хранения", e);
        }
    }

    private void selectById(Long id) {
        if (id == null) {
            return;
        }

        for (StoragePlaceType type : table.getItems()) {
            if (id.equals(type.getId())) {
                table.getSelectionModel().select(type);
                table.scrollTo(type);
                return;
            }
        }
    }
}