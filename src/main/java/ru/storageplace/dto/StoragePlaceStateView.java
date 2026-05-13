package ru.storageplace.dto;

import ru.storageplace.model.StoragePlaceStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class StoragePlaceStateView {
    private final Long storagePlaceId;
    private final String storagePlaceNumber;
    private final BigDecimal totalVolume;
    private final BigDecimal occupiedVolume;
    private final BigDecimal freeVolume;
    private final BigDecimal maxWeightKg;
    private final BigDecimal occupiedWeightKg;
    private final BigDecimal freeWeightKg;
    private final StoragePlaceStatus status;
    private final OffsetDateTime updatedAt;

    public StoragePlaceStateView(Long storagePlaceId,
                                 String storagePlaceNumber,
                                 BigDecimal totalVolume,
                                 BigDecimal occupiedVolume,
                                 BigDecimal freeVolume,
                                 BigDecimal maxWeightKg,
                                 BigDecimal occupiedWeightKg,
                                 BigDecimal freeWeightKg,
                                 StoragePlaceStatus status,
                                 OffsetDateTime updatedAt) {
        this.storagePlaceId = storagePlaceId;
        this.storagePlaceNumber = storagePlaceNumber;
        this.totalVolume = totalVolume;
        this.occupiedVolume = occupiedVolume;
        this.freeVolume = freeVolume;
        this.maxWeightKg = maxWeightKg;
        this.occupiedWeightKg = occupiedWeightKg;
        this.freeWeightKg = freeWeightKg;
        this.status = status;
        this.updatedAt = updatedAt;
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

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public BigDecimal getOccupiedVolume() {
        return occupiedVolume;
    }

    public BigDecimal getFreeVolume() {
        return freeVolume;
    }

    public BigDecimal getMaxWeightKg() {
        return maxWeightKg;
    }

    public BigDecimal getOccupiedWeightKg() {
        return occupiedWeightKg;
    }

    public BigDecimal getFreeWeightKg() {
        return freeWeightKg;
    }

    public StoragePlaceStatus getStatus() {
        return status;
    }

    public String getStatusName() {
        return status.getDisplayName();
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}