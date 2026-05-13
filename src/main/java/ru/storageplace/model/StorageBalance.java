package ru.storageplace.model;

import java.math.BigDecimal;

public class StorageBalance {
    private Long id;
    private Long storagePlaceId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalVolume;
    private BigDecimal totalWeightKg;

    public StorageBalance() {
    }

    public StorageBalance(Long id, Long storagePlaceId, Long productId,
                          Integer quantity, BigDecimal totalVolume,
                          BigDecimal totalWeightKg) {
        this.id = id;
        this.storagePlaceId = storagePlaceId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalVolume = totalVolume;
        this.totalWeightKg = totalWeightKg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoragePlaceId() {
        return storagePlaceId;
    }

    public void setStoragePlaceId(Long storagePlaceId) {
        this.storagePlaceId = storagePlaceId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }

    public BigDecimal getTotalWeightKg() {
        return totalWeightKg;
    }

    public void setTotalWeightKg(BigDecimal totalWeightKg) {
        this.totalWeightKg = totalWeightKg;
    }
}