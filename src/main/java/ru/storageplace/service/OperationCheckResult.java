package ru.storageplace.service;

import ru.storageplace.model.OperationStatus;

import java.math.BigDecimal;

public class OperationCheckResult {
    private final OperationStatus status;
    private final String message;
    private final BigDecimal productVolume;
    private final BigDecimal totalVolume;
    private final BigDecimal totalWeightKg;

    public OperationCheckResult(OperationStatus status,
                                String message,
                                BigDecimal productVolume,
                                BigDecimal totalVolume,
                                BigDecimal totalWeightKg) {
        this.status = status;
        this.message = message;
        this.productVolume = productVolume;
        this.totalVolume = totalVolume;
        this.totalWeightKg = totalWeightKg;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
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

    public boolean isCalculated() {
        return status == OperationStatus.CALCULATED;
    }
}