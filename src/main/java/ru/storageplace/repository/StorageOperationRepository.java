package ru.storageplace.repository;

import ru.storageplace.db.Database;
import ru.storageplace.dto.StorageOperationView;
import ru.storageplace.model.OperationStatus;
import ru.storageplace.model.OperationType;
import ru.storageplace.model.StorageOperation;

import java.sql.Types;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StorageOperationRepository {

    public List<StorageOperation> findAll() {
        String sql = """
                SELECT id, operation_type, product_id, source_place_id, target_place_id,
                       quantity, product_length_cm, product_width_cm, product_height_cm,
                       product_weight_kg, product_volume, total_volume, total_weight_kg,
                       status, result_message, created_at, confirmed_at
                FROM storage_operation
                ORDER BY id
                """;

        List<StorageOperation> operations = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                operations.add(mapRow(resultSet));
            }

            return operations;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении операций хранения", e);
        }
    }

    public List<StorageOperationView> findAllView() {
        String sql = """
            SELECT so.id,
                   so.operation_type,
                   so.product_id,
                   p.name AS product_name,
                   p.article AS product_article,
                   so.source_place_id,
                   source_place.number AS source_place_number,
                   so.target_place_id,
                   target_place.number AS target_place_number,
                   so.quantity,
                   so.product_volume,
                   so.total_volume,
                   so.total_weight_kg,
                   so.status,
                   so.result_message,
                   so.created_at,
                   so.confirmed_at
            FROM storage_operation so
            INNER JOIN product p ON p.id = so.product_id
            LEFT JOIN storage_place source_place ON source_place.id = so.source_place_id
            LEFT JOIN storage_place target_place ON target_place.id = so.target_place_id
            ORDER BY so.id
            """;

        List<StorageOperationView> operations = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                operations.add(mapViewRow(resultSet));
            }

            return operations;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка операций хранения", e);
        }
    }

    public Optional<StorageOperation> findById(Long id) {
        String sql = """
            SELECT id, operation_type, product_id, source_place_id, target_place_id,
                   quantity, product_length_cm, product_width_cm, product_height_cm,
                   product_weight_kg, product_volume, total_volume, total_weight_kg,
                   status, result_message, created_at, confirmed_at
            FROM storage_operation
            WHERE id = ?
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении операции хранения", e);
        }
    }

    public Long create(StorageOperation operation) {
        String sql = """
            INSERT INTO storage_operation (
                operation_type, product_id, source_place_id, target_place_id, quantity,
                product_length_cm, product_width_cm, product_height_cm, product_weight_kg,
                product_volume, total_volume, total_weight_kg,
                status, result_message
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id, created_at
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, operation.getOperationType().name());
            statement.setLong(2, operation.getProductId());
            setNullableLong(statement, 3, operation.getSourcePlaceId());
            setNullableLong(statement, 4, operation.getTargetPlaceId());
            statement.setInt(5, operation.getQuantity());

            statement.setBigDecimal(6, operation.getProductLengthCm());
            statement.setBigDecimal(7, operation.getProductWidthCm());
            statement.setBigDecimal(8, operation.getProductHeightCm());
            statement.setBigDecimal(9, operation.getProductWeightKg());
            statement.setBigDecimal(10, operation.getProductVolume());
            statement.setBigDecimal(11, operation.getTotalVolume());
            statement.setBigDecimal(12, operation.getTotalWeightKg());

            statement.setString(13, operation.getStatus().name());
            statement.setString(14, operation.getResultMessage());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");

                    operation.setId(id);
                    operation.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));

                    return id;
                }

                throw new SQLException("PostgreSQL не вернул id созданной операции");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании операции хранения", e);
        }
    }

    public void updateCalculation(StorageOperation operation) {
        String sql = """
            UPDATE storage_operation
            SET operation_type = ?,
                product_id = ?,
                source_place_id = ?,
                target_place_id = ?,
                quantity = ?,
                product_length_cm = ?,
                product_width_cm = ?,
                product_height_cm = ?,
                product_weight_kg = ?,
                product_volume = ?,
                total_volume = ?,
                total_weight_kg = ?,
                status = ?,
                result_message = ?,
                confirmed_at = NULL
            WHERE id = ?
              AND status <> ?
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, operation.getOperationType().name());
            statement.setLong(2, operation.getProductId());
            setNullableLong(statement, 3, operation.getSourcePlaceId());
            setNullableLong(statement, 4, operation.getTargetPlaceId());
            statement.setInt(5, operation.getQuantity());

            statement.setBigDecimal(6, operation.getProductLengthCm());
            statement.setBigDecimal(7, operation.getProductWidthCm());
            statement.setBigDecimal(8, operation.getProductHeightCm());
            statement.setBigDecimal(9, operation.getProductWeightKg());
            statement.setBigDecimal(10, operation.getProductVolume());
            statement.setBigDecimal(11, operation.getTotalVolume());
            statement.setBigDecimal(12, operation.getTotalWeightKg());

            statement.setString(13, operation.getStatus().name());
            statement.setString(14, operation.getResultMessage());
            statement.setLong(15, operation.getId());
            statement.setString(16, OperationStatus.CONFIRMED.name());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Операция не найдена или уже выполнена");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении операции хранения", e);
        }
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    private StorageOperation mapRow(ResultSet resultSet) throws SQLException {
        Long sourcePlaceId = resultSet.getLong("source_place_id");
        if (resultSet.wasNull()) {
            sourcePlaceId = null;
        }

        Long targetPlaceId = resultSet.getLong("target_place_id");
        if (resultSet.wasNull()) {
            targetPlaceId = null;
        }

        return new StorageOperation(
                resultSet.getLong("id"),
                OperationType.valueOf(resultSet.getString("operation_type")),
                resultSet.getLong("product_id"),
                sourcePlaceId,
                targetPlaceId,
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("product_length_cm"),
                resultSet.getBigDecimal("product_width_cm"),
                resultSet.getBigDecimal("product_height_cm"),
                resultSet.getBigDecimal("product_weight_kg"),
                resultSet.getBigDecimal("product_volume"),
                resultSet.getBigDecimal("total_volume"),
                resultSet.getBigDecimal("total_weight_kg"),
                OperationStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("result_message"),
                resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                resultSet.getObject("confirmed_at", java.time.OffsetDateTime.class)
        );
    }

    private StorageOperationView mapViewRow(ResultSet resultSet) throws SQLException {
        Long sourcePlaceId = resultSet.getLong("source_place_id");
        if (resultSet.wasNull()) {
            sourcePlaceId = null;
        }

        Long targetPlaceId = resultSet.getLong("target_place_id");
        if (resultSet.wasNull()) {
            targetPlaceId = null;
        }

        return new StorageOperationView(
                resultSet.getLong("id"),
                OperationType.valueOf(resultSet.getString("operation_type")),
                resultSet.getLong("product_id"),
                resultSet.getString("product_name"),
                resultSet.getString("product_article"),
                sourcePlaceId,
                resultSet.getString("source_place_number"),
                targetPlaceId,
                resultSet.getString("target_place_number"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("product_volume"),
                resultSet.getBigDecimal("total_volume"),
                resultSet.getBigDecimal("total_weight_kg"),
                OperationStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("result_message"),
                resultSet.getObject("created_at", java.time.OffsetDateTime.class),
                resultSet.getObject("confirmed_at", java.time.OffsetDateTime.class)
        );
    }
}