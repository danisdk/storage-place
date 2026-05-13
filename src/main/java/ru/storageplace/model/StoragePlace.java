package ru.storageplace.model;

import java.math.BigDecimal;

public class StoragePlace {
    private Long id;
    private Long warehouseId;
    private Long typeId;
    private String number;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private BigDecimal maxWeightKg;

    public StoragePlace() {
    }

    public StoragePlace(Long id, Long warehouseId, Long typeId, String number,
                        BigDecimal lengthCm, BigDecimal widthCm,
                        BigDecimal heightCm, BigDecimal maxWeightKg) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.typeId = typeId;
        this.number = number;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.heightCm = heightCm;
        this.maxWeightKg = maxWeightKg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public BigDecimal getLengthCm() {
        return lengthCm;
    }

    public void setLengthCm(BigDecimal lengthCm) {
        this.lengthCm = lengthCm;
    }

    public BigDecimal getWidthCm() {
        return widthCm;
    }

    public void setWidthCm(BigDecimal widthCm) {
        this.widthCm = widthCm;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getMaxWeightKg() {
        return maxWeightKg;
    }

    public void setMaxWeightKg(BigDecimal maxWeightKg) {
        this.maxWeightKg = maxWeightKg;
    }

    public String toString() {
        return "ID " + id + " — место " + number;
    }
}