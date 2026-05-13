package ru.storageplace.service;

import ru.storageplace.db.Database;
import ru.storageplace.model.*;
import ru.storageplace.repository.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StorageOperationService {
    private final ProductRepository productRepository = new ProductRepository();
    private final StoragePlaceRepository storagePlaceRepository = new StoragePlaceRepository();
    private final StoragePlaceStateRepository storagePlaceStateRepository = new StoragePlaceStateRepository();
    private final StorageBalanceRepository storageBalanceRepository = new StorageBalanceRepository();
    private final StorageOperationRepository storageOperationRepository = new StorageOperationRepository();

    private final CalculationService calculationService = new CalculationService();

    public StorageOperation calculateAndSave(OperationType operationType,
                                             Long productId,
                                             Long sourcePlaceId,
                                             Long targetPlaceId,
                                             Integer quantity) {
        StorageOperation operation = buildCalculatedOperation(
                null,
                operationType,
                productId,
                sourcePlaceId,
                targetPlaceId,
                quantity
        );

        storageOperationRepository.create(operation);

        return operation;
    }

    public StorageOperation recalculateAndUpdate(Long operationId,
                                                 OperationType operationType,
                                                 Long productId,
                                                 Long sourcePlaceId,
                                                 Long targetPlaceId,
                                                 Integer quantity) {
        if (operationId == null) {
            throw new IllegalArgumentException("Не указан идентификатор операции");
        }

        StorageOperation existingOperation = storageOperationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Операция хранения не найдена"));

        if (existingOperation.getStatus() == OperationStatus.CONFIRMED) {
            throw new IllegalStateException("Выполненную операцию нельзя изменить");
        }

        StorageOperation operation = buildCalculatedOperation(
                operationId,
                operationType,
                productId,
                sourcePlaceId,
                targetPlaceId,
                quantity
        );

        operation.setCreatedAt(existingOperation.getCreatedAt());

        storageOperationRepository.updateCalculation(operation);

        return operation;
    }

    private StorageOperation buildCalculatedOperation(Long operationId,
                                                      OperationType operationType,
                                                      Long productId,
                                                      Long sourcePlaceId,
                                                      Long targetPlaceId,
                                                      Integer quantity) {
        validateCommonInput(operationType, productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

        BigDecimal productVolume = calculationService.calculateProductVolume(product);
        BigDecimal totalVolume = calculationService.calculateTotalVolume(product, quantity);
        BigDecimal totalWeight = calculationService.calculateTotalWeight(product, quantity);

        OperationCheckResult checkResult = checkOperation(
                operationType,
                product,
                sourcePlaceId,
                targetPlaceId,
                quantity,
                productVolume,
                totalVolume,
                totalWeight
        );

        StorageOperation operation = new StorageOperation();

        operation.setId(operationId);
        operation.setOperationType(operationType);
        operation.setProductId(productId);
        operation.setSourcePlaceId(sourcePlaceId);
        operation.setTargetPlaceId(targetPlaceId);
        operation.setQuantity(quantity);

        operation.setProductLengthCm(product.getLengthCm());
        operation.setProductWidthCm(product.getWidthCm());
        operation.setProductHeightCm(product.getHeightCm());
        operation.setProductWeightKg(product.getWeightKg());
        operation.setProductVolume(productVolume);
        operation.setTotalVolume(totalVolume);
        operation.setTotalWeightKg(totalWeight);

        operation.setStatus(checkResult.getStatus());
        operation.setResultMessage(checkResult.getMessage());

        return operation;
    }

    public void confirmOperation(Long operationId) {
        if (operationId == null) {
            throw new IllegalArgumentException("Не указан идентификатор операции");
        }

        StorageOperation operation = storageOperationRepository.findById(operationId)
                .orElseThrow(() -> new IllegalArgumentException("Операция хранения не найдена"));

        if (operation.getStatus() == OperationStatus.CONFIRMED) {
            throw new IllegalStateException("Операция уже выполнена");
        }

        operation = recalculateAndUpdate(
                operation.getId(),
                operation.getOperationType(),
                operation.getProductId(),
                operation.getSourcePlaceId(),
                operation.getTargetPlaceId(),
                operation.getQuantity()
        );

        if (operation.getStatus() != OperationStatus.CALCULATED) {
            throw new IllegalStateException(
                    "Операция не может быть выполнена после повторной проверки: "
                            + operation.getResultMessage()
            );
        }

        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                switch (operation.getOperationType()) {
                    case INCOME -> confirmIncome(connection, operation);
                    case OUTCOME -> confirmOutcome(connection, operation);
                    case TRANSFER -> confirmTransfer(connection, operation);
                }

                markOperationConfirmed(connection, operation.getId());

                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при выполнении складской операции", e);
        }
    }

    private OperationCheckResult checkOperation(OperationType operationType,
                                                Product product,
                                                Long sourcePlaceId,
                                                Long targetPlaceId,
                                                Integer quantity,
                                                BigDecimal productVolume,
                                                BigDecimal totalVolume,
                                                BigDecimal totalWeight) {
        return switch (operationType) {
            case INCOME -> checkIncome(product, targetPlaceId, quantity, productVolume, totalVolume, totalWeight);
            case OUTCOME -> checkOutcome(sourcePlaceId, product.getId(), quantity, productVolume, totalVolume, totalWeight);
            case TRANSFER -> checkTransfer(product, sourcePlaceId, targetPlaceId, quantity, productVolume, totalVolume, totalWeight);
        };
    }

    private OperationCheckResult checkIncome(Product product,
                                             Long targetPlaceId,
                                             Integer quantity,
                                             BigDecimal productVolume,
                                             BigDecimal totalVolume,
                                             BigDecimal totalWeight) {
        if (targetPlaceId == null) {
            return rejected("Для операции поступления необходимо указать целевое место хранения",
                    productVolume, totalVolume, totalWeight);
        }

        StoragePlace targetPlace = storagePlaceRepository.findById(targetPlaceId)
                .orElseThrow(() -> new IllegalArgumentException("Целевое место хранения не найдено"));

        StoragePlaceState targetState = storagePlaceStateRepository.findByStoragePlaceId(targetPlaceId)
                .orElseThrow(() -> new IllegalStateException("Состояние целевого места хранения не найдено"));

        String placementError = checkPlacement(product, targetPlace, targetState, quantity);

        if (placementError != null) {
            return rejected(placementError, productVolume, totalVolume, totalWeight);
        }

        return calculated("Операция поступления рассчитана и может быть выполнена",
                productVolume, totalVolume, totalWeight);
    }

    private OperationCheckResult checkOutcome(Long sourcePlaceId,
                                              Long productId,
                                              Integer quantity,
                                              BigDecimal productVolume,
                                              BigDecimal totalVolume,
                                              BigDecimal totalWeight) {
        if (sourcePlaceId == null) {
            return rejected("Для операции изъятия необходимо указать исходное место хранения",
                    productVolume, totalVolume, totalWeight);
        }

        storagePlaceRepository.findById(sourcePlaceId)
                .orElseThrow(() -> new IllegalArgumentException("Исходное место хранения не найдено"));

        StorageBalance balance = storageBalanceRepository
                .findByStoragePlaceIdAndProductId(sourcePlaceId, productId)
                .orElse(null);

        if (balance == null || balance.getQuantity() < quantity) {
            return rejected("В исходном месте хранения недостаточно товара",
                    productVolume, totalVolume, totalWeight);
        }

        return calculated("Операция изъятия рассчитана и может быть выполнена",
                productVolume, totalVolume, totalWeight);
    }

    private OperationCheckResult checkTransfer(Product product,
                                               Long sourcePlaceId,
                                               Long targetPlaceId,
                                               Integer quantity,
                                               BigDecimal productVolume,
                                               BigDecimal totalVolume,
                                               BigDecimal totalWeight) {
        if (sourcePlaceId == null) {
            return rejected("Для операции перемещения необходимо указать исходное место хранения",
                    productVolume, totalVolume, totalWeight);
        }

        if (targetPlaceId == null) {
            return rejected("Для операции перемещения необходимо указать целевое место хранения",
                    productVolume, totalVolume, totalWeight);
        }

        if (sourcePlaceId.equals(targetPlaceId)) {
            return rejected("Исходное и целевое место хранения не должны совпадать",
                    productVolume, totalVolume, totalWeight);
        }

        StorageBalance balance = storageBalanceRepository
                .findByStoragePlaceIdAndProductId(sourcePlaceId, product.getId())
                .orElse(null);

        if (balance == null || balance.getQuantity() < quantity) {
            return rejected("В исходном месте хранения недостаточно товара для перемещения",
                    productVolume, totalVolume, totalWeight);
        }

        StoragePlace targetPlace = storagePlaceRepository.findById(targetPlaceId)
                .orElseThrow(() -> new IllegalArgumentException("Целевое место хранения не найдено"));

        StoragePlaceState targetState = storagePlaceStateRepository.findByStoragePlaceId(targetPlaceId)
                .orElseThrow(() -> new IllegalStateException("Состояние целевого места хранения не найдено"));

        String placementError = checkPlacement(product, targetPlace, targetState, quantity);

        if (placementError != null) {
            return rejected(placementError, productVolume, totalVolume, totalWeight);
        }

        return calculated("Операция перемещения рассчитана и может быть выполнена",
                productVolume, totalVolume, totalWeight);
    }

    private String checkPlacement(Product product,
                                  StoragePlace place,
                                  StoragePlaceState state,
                                  Integer quantity) {
        BigDecimal totalVolume = calculationService.calculateTotalVolume(product, quantity);
        BigDecimal totalWeight = calculationService.calculateTotalWeight(product, quantity);
        BigDecimal freeVolume = calculationService.calculateFreeVolume(place, state);
        BigDecimal freeWeight = calculationService.calculateFreeWeight(place, state);

        if (product.getLengthCm().compareTo(place.getLengthCm()) > 0) {
            return "Длина товара превышает длину места хранения";
        }

        if (product.getWidthCm().compareTo(place.getWidthCm()) > 0) {
            return "Ширина товара превышает ширину места хранения";
        }

        if (product.getHeightCm().compareTo(place.getHeightCm()) > 0) {
            return "Высота товара превышает высоту места хранения";
        }

        if (totalVolume.compareTo(freeVolume) > 0) {
            return "Недостаточно свободного объёма в выбранном месте хранения";
        }

        if (totalWeight.compareTo(freeWeight) > 0) {
            return "Превышена доступная нагрузка выбранного места хранения";
        }

        return null;
    }

    private void confirmIncome(Connection connection, StorageOperation operation) throws SQLException {
        addBalance(
                connection,
                operation.getTargetPlaceId(),
                operation.getProductId(),
                operation.getQuantity(),
                operation.getTotalVolume(),
                operation.getTotalWeightKg()
        );

        updateStoragePlaceState(
                connection,
                operation.getTargetPlaceId(),
                operation.getTotalVolume(),
                operation.getTotalWeightKg()
        );
    }

    private void confirmOutcome(Connection connection, StorageOperation operation) throws SQLException {
        subtractBalance(
                connection,
                operation.getSourcePlaceId(),
                operation.getProductId(),
                operation.getQuantity(),
                operation.getTotalVolume(),
                operation.getTotalWeightKg()
        );

        updateStoragePlaceState(
                connection,
                operation.getSourcePlaceId(),
                operation.getTotalVolume().negate(),
                operation.getTotalWeightKg().negate()
        );
    }

    private void confirmTransfer(Connection connection, StorageOperation operation) throws SQLException {
        subtractBalance(
                connection,
                operation.getSourcePlaceId(),
                operation.getProductId(),
                operation.getQuantity(),
                operation.getTotalVolume(),
                operation.getTotalWeightKg()
        );

        updateStoragePlaceState(
                connection,
                operation.getSourcePlaceId(),
                operation.getTotalVolume().negate(),
                operation.getTotalWeightKg().negate()
        );

        addBalance(
                connection,
                operation.getTargetPlaceId(),
                operation.getProductId(),
                operation.getQuantity(),
                operation.getTotalVolume(),
                operation.getTotalWeightKg()
        );

        updateStoragePlaceState(
                connection,
                operation.getTargetPlaceId(),
                operation.getTotalVolume(),
                operation.getTotalWeightKg()
        );
    }

    private void addBalance(Connection connection,
                            Long storagePlaceId,
                            Long productId,
                            Integer quantity,
                            BigDecimal totalVolume,
                            BigDecimal totalWeightKg) throws SQLException {
        String sql = """
                INSERT INTO storage_balance (
                    storage_place_id, product_id, quantity, total_volume, total_weight_kg
                )
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (storage_place_id, product_id)
                DO UPDATE SET
                    quantity = storage_balance.quantity + EXCLUDED.quantity,
                    total_volume = storage_balance.total_volume + EXCLUDED.total_volume,
                    total_weight_kg = storage_balance.total_weight_kg + EXCLUDED.total_weight_kg
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, storagePlaceId);
            statement.setLong(2, productId);
            statement.setInt(3, quantity);
            statement.setBigDecimal(4, totalVolume);
            statement.setBigDecimal(5, totalWeightKg);
            statement.executeUpdate();
        }
    }

    private void subtractBalance(Connection connection,
                                 Long storagePlaceId,
                                 Long productId,
                                 Integer quantity,
                                 BigDecimal totalVolume,
                                 BigDecimal totalWeightKg) throws SQLException {
        String sql = """
                UPDATE storage_balance
                SET quantity = quantity - ?,
                    total_volume = total_volume - ?,
                    total_weight_kg = total_weight_kg - ?
                WHERE storage_place_id = ?
                  AND product_id = ?
                  AND quantity >= ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setBigDecimal(2, totalVolume);
            statement.setBigDecimal(3, totalWeightKg);
            statement.setLong(4, storagePlaceId);
            statement.setLong(5, productId);
            statement.setInt(6, quantity);

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Недостаточно товара для выполнения операции");
            }
        }
    }

    private void updateStoragePlaceState(Connection connection,
                                         Long storagePlaceId,
                                         BigDecimal volumeDelta,
                                         BigDecimal weightDelta) throws SQLException {
        String updateSql = """
                UPDATE storage_place_state
                SET occupied_volume = occupied_volume + ?,
                    occupied_weight_kg = occupied_weight_kg + ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE storage_place_id = ?
                  AND occupied_volume + ? >= 0
                  AND occupied_weight_kg + ? >= 0
                """;

        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setBigDecimal(1, volumeDelta);
            statement.setBigDecimal(2, weightDelta);
            statement.setLong(3, storagePlaceId);
            statement.setBigDecimal(4, volumeDelta);
            statement.setBigDecimal(5, weightDelta);

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Не удалось обновить состояние места хранения");
            }
        }

        refreshStoragePlaceStatus(connection, storagePlaceId);
    }

    private void refreshStoragePlaceStatus(Connection connection, Long storagePlaceId) throws SQLException {
        String sql = """
                UPDATE storage_place_state state
                SET status = CASE
                    WHEN state.occupied_volume = 0 AND state.occupied_weight_kg = 0
                        THEN 'FREE'
                    WHEN state.occupied_volume >= (place.length_cm * place.width_cm * place.height_cm)
                         OR state.occupied_weight_kg >= place.max_weight_kg
                        THEN 'OCCUPIED'
                    ELSE 'PARTIALLY_OCCUPIED'
                END,
                updated_at = CURRENT_TIMESTAMP
                FROM storage_place place
                WHERE state.storage_place_id = place.id
                  AND state.storage_place_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, storagePlaceId);
            statement.executeUpdate();
        }
    }

    private void markOperationConfirmed(Connection connection, Long operationId) throws SQLException {
        String sql = """
            UPDATE storage_operation
            SET status = ?,
                result_message = ?,
                confirmed_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, OperationStatus.CONFIRMED.name());
            statement.setString(2, "Операция выполнена");
            statement.setLong(3, operationId);

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Не удалось подтвердить операцию хранения");
            }
        }
    }

    private void validateCommonInput(OperationType operationType, Long productId, Integer quantity) {
        if (operationType == null) {
            throw new IllegalArgumentException("Не указан тип операции");
        }

        if (productId == null) {
            throw new IllegalArgumentException("Не указан товар");
        }

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Количество товара должно быть больше нуля");
        }
    }

    private OperationCheckResult calculated(String message,
                                            BigDecimal productVolume,
                                            BigDecimal totalVolume,
                                            BigDecimal totalWeightKg) {
        return new OperationCheckResult(
                OperationStatus.CALCULATED,
                message,
                productVolume,
                totalVolume,
                totalWeightKg
        );
    }

    private OperationCheckResult rejected(String message,
                                          BigDecimal productVolume,
                                          BigDecimal totalVolume,
                                          BigDecimal totalWeightKg) {
        return new OperationCheckResult(
                OperationStatus.REJECTED,
                message,
                productVolume,
                totalVolume,
                totalWeightKg
        );
    }
}