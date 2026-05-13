package ru.storageplace;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.storageplace.ui.MainView;
import ru.storageplace.ui.UiUtils;


public class StoragePlaceApplication extends Application {

    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView();

        Scene scene = new Scene(mainView.createView(), 1200, 750);

        scene.getStylesheets().add(UiUtils.getAppStylesheet());
        stage.setTitle("Storage Place");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }
}