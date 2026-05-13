package ru.storageplace.model;

public enum OperationStatus {
    CALCULATED("Рассчитана"),
    CONFIRMED("Выполнена"),
    REJECTED("Отклонена");

    private final String displayName;

    OperationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}