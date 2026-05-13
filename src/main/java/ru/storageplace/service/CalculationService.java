package ru.storageplace.service;

import ru.storageplace.model.Product;
import ru.storageplace.model.StoragePlace;
import ru.storageplace.model.StoragePlaceState;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationService {

    private static final int SCALE = 3;

    public BigDecimal calculateProductVolume(Product product) {
        return normalize(
                product.getLengthCm()
                        .multiply(product.getWidthCm())
                        .multiply(product.getHeightCm())
        );
    }

    public BigDecimal calculateTotalVolume(Product product, Integer quantity) {
        return normalize(
                calculateProductVolume(product)
                        .multiply(BigDecimal.valueOf(quantity))
        );
    }

    public BigDecimal calculateTotalWeight(Product product, Integer quantity) {
        return normalize(
                product.getWeightKg()
                        .multiply(BigDecimal.valueOf(quantity))
        );
    }

    public BigDecimal calculateStoragePlaceVolume(StoragePlace place) {
        return normalize(
                place.getLengthCm()
                        .multiply(place.getWidthCm())
                        .multiply(place.getHeightCm())
        );
    }

    public BigDecimal calculateFreeVolume(StoragePlace place, StoragePlaceState state) {
        return normalize(
                calculateStoragePlaceVolume(place)
                        .subtract(state.getOccupiedVolume())
        );
    }

    public BigDecimal calculateFreeWeight(StoragePlace place, StoragePlaceState state) {
        return normalize(
                place.getMaxWeightKg()
                        .subtract(state.getOccupiedWeightKg())
        );
    }

    private BigDecimal normalize(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

//    public boolean isProductFitsByDimensions(Product product, StoragePlace place) {
//        return product.getLengthCm().compareTo(place.getLengthCm()) <= 0
//                && product.getWidthCm().compareTo(place.getWidthCm()) <= 0
//                && product.getHeightCm().compareTo(place.getHeightCm()) <= 0;
//    }

//    public boolean canPlaceProduct(Product product,
//                                   StoragePlace place,
//                                   StoragePlaceState state,
//                                   Integer quantity) {
//        BigDecimal totalVolume = calculateTotalVolume(product, quantity);
//        BigDecimal totalWeight = calculateTotalWeight(product, quantity);
//
//        return isProductFitsByDimensions(product, place)
//                && totalVolume.compareTo(calculateFreeVolume(place, state)) <= 0
//                && totalWeight.compareTo(calculateFreeWeight(place, state)) <= 0;
//    }
}