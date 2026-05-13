package ru.storageplace.dto;

import java.math.BigDecimal;

public class StorageBalanceView {
    private final Long id;
    private final Long storagePlaceId;
    private final String storagePlaceNumber;
    private final Long productId;
    private final String productName;
    private final String productArticle;
    private final Integer quantity;
    private final BigDecimal totalVolume;
    private final BigDecimal totalWeightKg;

    public StorageBalanceView(Long id,
                              Long storagePlaceId,
                              String storagePlaceNumber,
                              Long productId,
                              String productName,
                              String productArticle,
                              Integer quantity,
                              BigDecimal totalVolume,
                              BigDecimal totalWeightKg) {
        this.id = id;
        this.storagePlaceId = storagePlaceId;
        this.storagePlaceNumber = storagePlaceNumber;
        this.productId = productId;
        this.productName = productName;
        this.productArticle = productArticle;
        this.quantity = quantity;
        this.totalVolume = totalVolume;
        this.totalWeightKg = totalWeightKg;
    }

    public Long getId() {
        return id;
    }

    public Long getStoragePlaceId() {
        return storagePlaceId;
    }

    public String getStoragePlaceNumber() {
        return storagePlaceNumber;
    }

    public String getStoragePlaceName() {
        return "ID " + storagePlaceId + " — место " + storagePlaceNumber;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductArticle() {
        return productArticle;
    }

    public String getProductDisplayName() {
        return productName + " (" + productArticle + ")";
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public BigDecimal getTotalWeightKg() {
        return totalWeightKg;
    }
}