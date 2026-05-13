package ru.storageplace.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.net.URL;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public final class UiUtils {

    private static final ZoneId LOCAL_ZONE_ID = ZoneId.systemDefault();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final String APP_STYLESHEET_PATH = "/ru/storageplace/styles/app.css";

    private UiUtils() {
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static String requiredText(TextField field, String fieldName) {
        String value = field.getText();

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\" обязательно для заполнения");
        }

        return value.trim();
    }

    public static String optionalText(TextField field) {
        String value = field.getText();

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    public static BigDecimal requiredDecimal(TextField field, String fieldName) {
        String value = requiredText(field, fieldName)
                .replace(',', '.');

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\" должно содержать число");
        }
    }

    public static Integer requiredInteger(TextField field, String fieldName) {
        String value = requiredText(field, fieldName);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Поле \"" + fieldName + "\" должно содержать целое число");
        }
    }

    public static String formatDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }

        return dateTime
                .atZoneSameInstant(LOCAL_ZONE_ID)
                .format(DATE_TIME_FORMATTER);
    }

    public static String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "—";
        }

        return value
                .setScale(3, RoundingMode.HALF_UP)
                .toPlainString();
    }


    public static String getAppStylesheet() {
        URL url = UiUtils.class.getResource(APP_STYLESHEET_PATH);

        if (url == null) {
            throw new IllegalStateException("CSS-файл app.css не найден: " + APP_STYLESHEET_PATH);
        }

        return url.toExternalForm();
    }

    public static void applyDialogStyle(Dialog<?> dialog) {
        applyDialogPaneStyle(dialog.getDialogPane());
    }

    public static void applyDialogPaneStyle(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(getAppStylesheet());
    }
}