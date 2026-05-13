package ru.storageplace.ui;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class MainView {

    public Parent createView() {
        BorderPane root = new BorderPane();

        Label title = new Label("Storage Place");
        title.getStyleClass().add("app-title");

        TabPane tabPane = new TabPane();

        tabPane.getTabs().addAll(
                new ProductTab().createTab(),
                new WarehouseTab().createTab(),
                new StoragePlaceTypeTab().createTab(),
                new StoragePlaceTab().createTab(),
                new StoragePlaceStateTab().createTab(),
                new StorageBalanceTab().createTab(),
                new StorageOperationTab().createTab()
        );

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        root.setTop(title);
        root.setCenter(tabPane);
        root.getStyleClass().add("app-root");
        BorderPane.setMargin(tabPane, new Insets(14, 14, 14, 14));

        return root;
    }
}