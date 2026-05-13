package ru.storageplace.repository;

import ru.storageplace.db.Database;
import ru.storageplace.model.StoragePlace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StoragePlaceRepository {

    public List<StoragePlace> findAll() {
        String sql = """
                SELECT id, warehouse_id, type_id, number,
                       length_cm, width_cm, height_cm, max_weight_kg
                FROM storage_place
                ORDER BY id
                """;

        List<StoragePlace> places = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                places.add(mapRow(resultSet));
            }

            return places;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении мест хранения", e);
        }
    }

    public Optional<StoragePlace> findById(Long id) {
        String sql = """
                SELECT id, warehouse_id, type_id, number,
                       length_cm, width_cm, height_cm, max_weight_kg
                FROM storage_place
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
            throw new RuntimeException("Ошибка при получении места хранения по id", e);
        }
    }

    public Long create(StoragePlace place) {
        String insertPlaceSql = """
                INSERT INTO storage_place (
                    warehouse_id, type_id, number,
                    length_cm, width_cm, height_cm, max_weight_kg
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        String insertStateSql = """
                INSERT INTO storage_place_state (storage_place_id)
                VALUES (?)
                """;

        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Long placeId;

                try (PreparedStatement statement = connection.prepareStatement(insertPlaceSql)) {
                    statement.setLong(1, place.getWarehouseId());
                    setNullableLong(statement, 2, place.getTypeId());
                    statement.setString(3, place.getNumber());
                    statement.setBigDecimal(4, place.getLengthCm());
                    statement.setBigDecimal(5, place.getWidthCm());
                    statement.setBigDecimal(6, place.getHeightCm());
                    statement.setBigDecimal(7, place.getMaxWeightKg());

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new SQLException("PostgreSQL не вернул id созданного места хранения");
                        }

                        placeId = resultSet.getLong("id");
                        place.setId(placeId);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(insertStateSql)) {
                    statement.setLong(1, placeId);
                    statement.executeUpdate();
                }

                connection.commit();
                return placeId;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании места хранения", e);
        }
    }

    public void update(StoragePlace place) {
        String sql = """
                UPDATE storage_place
                SET warehouse_id = ?,
                    type_id = ?,
                    number = ?,
                    length_cm = ?,
                    width_cm = ?,
                    height_cm = ?,
                    max_weight_kg = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, place.getWarehouseId());
            setNullableLong(statement, 2, place.getTypeId());
            statement.setString(3, place.getNumber());
            statement.setBigDecimal(4, place.getLengthCm());
            statement.setBigDecimal(5, place.getWidthCm());
            statement.setBigDecimal(6, place.getHeightCm());
            statement.setBigDecimal(7, place.getMaxWeightKg());
            statement.setLong(8, place.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Место хранения с id = " + place.getId() + " не найдено");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении места хранения", e);
        }
    }

    public void deleteById(Long id) {
        String sql = """
                DELETE FROM storage_place
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении места хранения", e);
        }
    }

    private StoragePlace mapRow(ResultSet resultSet) throws SQLException {
        Long typeId = resultSet.getLong("type_id");

        if (resultSet.wasNull()) {
            typeId = null;
        }

        return new StoragePlace(
                resultSet.getLong("id"),
                resultSet.getLong("warehouse_id"),
                typeId,
                resultSet.getString("number"),
                resultSet.getBigDecimal("length_cm"),
                resultSet.getBigDecimal("width_cm"),
                resultSet.getBigDecimal("height_cm"),
                resultSet.getBigDecimal("max_weight_kg")
        );
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }
}