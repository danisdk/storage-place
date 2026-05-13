package ru.storageplace.repository;

import ru.storageplace.db.Database;
import ru.storageplace.model.StoragePlaceState;
import ru.storageplace.model.StoragePlaceStatus;
import ru.storageplace.dto.StoragePlaceStateView;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StoragePlaceStateRepository {

    public List<StoragePlaceState> findAll() {
        String sql = """
                SELECT id, storage_place_id, occupied_volume,
                       occupied_weight_kg, status, updated_at
                FROM storage_place_state
                ORDER BY id
                """;

        List<StoragePlaceState> states = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                states.add(mapRow(resultSet));
            }

            return states;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении состояний мест хранения", e);
        }
    }

    public List<StoragePlaceStateView> findAllView() {
        String sql = """
            SELECT sp.id AS storage_place_id,
                   sp.number AS storage_place_number,
                   sp.length_cm * sp.width_cm * sp.height_cm AS total_volume,
                   COALESCE(sps.occupied_volume, 0) AS occupied_volume,
                   sp.length_cm * sp.width_cm * sp.height_cm - COALESCE(sps.occupied_volume, 0) AS free_volume,
                   sp.max_weight_kg AS max_weight_kg,
                   COALESCE(sps.occupied_weight_kg, 0) AS occupied_weight_kg,
                   sp.max_weight_kg - COALESCE(sps.occupied_weight_kg, 0) AS free_weight_kg,
                   COALESCE(sps.status, 'FREE') AS status,
                   sps.updated_at AS updated_at
            FROM storage_place sp
            LEFT JOIN storage_place_state sps ON sps.storage_place_id = sp.id
            ORDER BY sp.id
            """;

        List<StoragePlaceStateView> states = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                states.add(new StoragePlaceStateView(
                        resultSet.getLong("storage_place_id"),
                        resultSet.getString("storage_place_number"),
                        resultSet.getBigDecimal("total_volume"),
                        resultSet.getBigDecimal("occupied_volume"),
                        resultSet.getBigDecimal("free_volume"),
                        resultSet.getBigDecimal("max_weight_kg"),
                        resultSet.getBigDecimal("occupied_weight_kg"),
                        resultSet.getBigDecimal("free_weight_kg"),
                        StoragePlaceStatus.valueOf(resultSet.getString("status")),
                        resultSet.getObject("updated_at", OffsetDateTime.class)
                ));
            }

            return states;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении состояния мест хранения", e);
        }
    }

    public Optional<StoragePlaceState> findByStoragePlaceId(Long storagePlaceId) {
        String sql = """
            SELECT id, storage_place_id, occupied_volume,
                   occupied_weight_kg, status, updated_at
            FROM storage_place_state
            WHERE storage_place_id = ?
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, storagePlaceId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении состояния места хранения", e);
        }
    }

    private StoragePlaceState mapRow(ResultSet resultSet) throws SQLException {
        return new StoragePlaceState(
                resultSet.getLong("id"),
                resultSet.getLong("storage_place_id"),
                resultSet.getBigDecimal("occupied_volume"),
                resultSet.getBigDecimal("occupied_weight_kg"),
                StoragePlaceStatus.valueOf(resultSet.getString("status")),
                resultSet.getObject("updated_at", OffsetDateTime.class)
        );
    }
}