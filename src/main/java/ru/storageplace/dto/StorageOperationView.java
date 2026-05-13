package ru.storageplace.dto;

import ru.storageplace.model.OperationStatus;
import ru.storageplace.model.OperationType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class StorageOperationView {
    private final Long id;
    private final OperationType operationType;
    private final Long productId;
    private final String productName;
    private final String productArticle;
    private final Long sourcePlaceId;
    private final String sourcePlaceNumber;
    private final Long targetPlaceId;
    private final String targetPlaceNumber;
    private final Integer quantity;
    private final BigDecimal productVolume;
    private final BigDecimal totalVolume;
    private final BigDecimal totalWeightKg;
    private final OperationStatus status;
    private final String resultMessage;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime confirmedAt;

    public StorageOperationView(Long id,
                                OperationType operationType,
                                Long productId,
                                String productName,
                                String productArticle,
                                Long sourcePlaceId,
                                String sourcePlaceNumber,
                                Long targetPlaceId,
                                String targetPlaceNumber,
                                Integer quantity,
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
        this.productName = productName;
        this.productArticle = productArticle;
        this.sourcePlaceId = sourcePlaceId;
        this.sourcePlaceNumber = sourcePlaceNumber;
        this.targetPlaceId = targetPlaceId;
        this.targetPlaceNumber = targetPlaceNumber;
        this.quantity = quantity;
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

    public OperationType getOperationType() {
        return operationType;
    }

    public String getOperationTypeName() {
        return operationType.getDisplayName();
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

    public Long getSourcePlaceId() {
        return sourcePlaceId;
    }

    public String getSourcePlaceNumber() {
        return sourcePlaceNumber;
    }

    public String getSourcePlaceName() {
        if (sourcePlaceId == null) {
            return "—";
        }

        return "ID " + sourcePlaceId + " — место " + sourcePlaceNumber;
    }

    public Long getTargetPlaceId() {
        return targetPlaceId;
    }

    public String getTargetPlaceNumber() {
        return targetPlaceNumber;
    }

    public String getTargetPlaceName() {
        if (targetPlaceId == null) {
            return "—";
        }

        return "ID " + targetPlaceId + " — место " + targetPlaceNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getProductVolume() {
        return productVolume;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public BigDecimal getTotalWeightKg() {
        return totalWeightKg;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public String getStatusName() {
        return status.getDisplayName();
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }
}