package ru.storageplace.model;

public enum StoragePlaceStatus {
    FREE("Свободно"),
    PARTIALLY_OCCUPIED("Частично занято"),
    OCCUPIED("Занято");

    private final String displayName;

    StoragePlaceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}