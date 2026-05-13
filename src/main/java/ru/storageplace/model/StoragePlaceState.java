package ru.storageplace.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class StoragePlaceState {
    private Long id;
    private Long storagePlaceId;
    private BigDecimal occupiedVolume;
    private BigDecimal occupiedWeightKg;
    private StoragePlaceStatus status;
    private OffsetDateTime updatedAt;

    public StoragePlaceState() {
    }

    public StoragePlaceState(Long id, Long storagePlaceId,
                             BigDecimal occupiedVolume,
                             BigDecimal occupiedWeightKg,
                             StoragePlaceStatus status,
                             OffsetDateTime updatedAt) {
        this.id = id;
        this.storagePlaceId = storagePlaceId;
        this.occupiedVolume = occupiedVolume;
        this.occupiedWeightKg = occupiedWeightKg;
        this.status = status;
        this.updatedAt = updatedAt;
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

    public BigDecimal getOccupiedVolume() {
        return occupiedVolume;
    }

    public void setOccupiedVolume(BigDecimal occupiedVolume) {
        this.occupiedVolume = occupiedVolume;
    }

    public BigDecimal getOccupiedWeightKg() {
        return occupiedWeightKg;
    }

    public void setOccupiedWeightKg(BigDecimal occupiedWeightKg) {
        this.occupiedWeightKg = occupiedWeightKg;
    }

    public StoragePlaceStatus getStatus() {
        return status;
    }

    public void setStatus(StoragePlaceStatus status) {
        this.status = status;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}