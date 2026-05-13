package ru.storageplace.model;

public enum OperationType {
    INCOME("Поступление"),
    OUTCOME("Изъятие"),
    TRANSFER("Перемещение");

    private final String displayName;

    OperationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}