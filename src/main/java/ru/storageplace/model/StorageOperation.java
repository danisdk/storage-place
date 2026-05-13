package ru.storageplace.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class StorageOperation {
    private Long id;
    private OperationType operationType;
    private Long productId;
    private Long sourcePlaceId;
    private Long targetPlaceId;
    private Integer quantity;

    private BigDecimal productLengthCm;
    private BigDecimal productWidthCm;
    private BigDecimal productHeightCm;
    private BigDecimal productWeightKg;
    private BigDecimal productVolume;
    private BigDecimal totalVolume;
    private BigDecimal totalWeightKg;

    private OperationStatus status;
    private String resultMessage;
    private OffsetDateTime createdAt;
    private OffsetDateTime confirmedAt;

    public StorageOperation() {
    }

    public StorageOperation(Long id,
                            OperationType operationType,
                            Long productId,
                            Long sourcePlaceId,
                            Long targetPlaceId,
                            Integer quantity,
                            BigDecimal productLengthCm,
                            BigDecimal productWidthCm,
                            BigDecimal productHeightCm,
                            BigDecimal productWeightKg,
                            BigDecimal productVolume,
                            BigDecimal totalVolume,
                            BigDecimal totalWeightKg,
                            OperationStatus status,
                            String resultMessage,
                            OffsetDateTime createdAt,
                            OffsetDateTime confirmedAt) {
        this.id = id;
        this.operationType = operationType;
        this.productId = productId;
        this.sourcePlaceId = sourcePlaceId;
        this.targetPlaceId = targetPlaceId;
        this.quantity = quantity;
        this.productLengthCm = productLengthCm;
        this.productWidthCm = productWidthCm;
        this.productHeightCm = productHeightCm;
        this.productWeightKg = productWeightKg;
        this.productVolume = productVolume;
        this.totalVolume = totalVolume;
        this.totalWeightKg = totalWeightKg;
        this.status = status;
        this.resultMessage = resultMessage;
        this.createdAt = createdAt;
        this.confirmedAt = confirmedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSourcePlaceId() {
        return sourcePlaceId;
    }

    public void setSourcePlaceId(Long sourcePlaceId) {
        this.sourcePlaceId = sourcePlaceId;
    }

    public Long getTargetPlaceId() {
        return targetPlaceId;
    }

    public void setTargetPlaceId(Long targetPlaceId) {
        this.targetPlaceId = targetPlaceId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getProductLengthCm() {
        return productLengthCm;
    }

    public void setProductLengthCm(BigDecimal productLengthCm) {
        this.productLengthCm = productLengthCm;
    }

    public BigDecimal getProductWidthCm() {
        return productWidthCm;
    }

    public void setProductWidthCm(BigDecimal productWidthCm) {
        this.productWidthCm = productWidthCm;
    }

    public BigDecimal getProductHeightCm() {
        return productHeightCm;
    }

    public void setProductHeightCm(BigDecimal productHeightCm) {
        this.productHeightCm = productHeightCm;
    }

    public BigDecimal getProductWeightKg() {
        return productWeightKg;
    }

    public void setProductWeightKg(BigDecimal productWeightKg) {
        this.productWeightKg = productWeightKg;
    }

    public BigDecimal getProductVolume() {
        return productVolume;
    }

    public void setProductVolume(BigDecimal productVolume) {
        this.productVolume = productVolume;
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

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}