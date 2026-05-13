package ru.storageplace.repository;

import ru.storageplace.db.Database;
import ru.storageplace.dto.StorageBalanceView;
import ru.storageplace.model.StorageBalance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class StorageBalanceRepository {

    public List<StorageBalance> findAll() {
        String sql = """
                SELECT id, storage_place_id, product_id, quantity,
                       total_volume, total_weight_kg
                FROM storage_balance
                ORDER BY id
                """;

        List<StorageBalance> balances = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                balances.add(mapRow(resultSet));
            }

            return balances;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении остатков товаров", e);
        }
    }

    public List<StorageBalanceView> findAllView() {
        String sql = """
            SELECT sb.id,
                   sb.storage_place_id,
                   sp.number AS storage_place_number,
                   sb.product_id,
                   p.name AS product_name,
                   p.article AS product_article,
                   sb.quantity,
                   sb.total_volume,
                   sb.total_weight_kg
            FROM storage_balance sb
            INNER JOIN storage_place sp ON sp.id = sb.storage_place_id
            INNER JOIN product p ON p.id = sb.product_id
            ORDER BY sp.id, p.name
            """;

        List<StorageBalanceView> balances = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                balances.add(new StorageBalanceView(
                        resultSet.getLong("id"),
                        resultSet.getLong("storage_place_id"),
                        resultSet.getString("storage_place_number"),
                        resultSet.getLong("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("product_article"),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("total_volume"),
                        resultSet.getBigDecimal("total_weight_kg")
                ));
            }

            return balances;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении остатков товаров", e);
        }
    }

    public Optional<StorageBalance> findByStoragePlaceIdAndProductId(Long storagePlaceId, Long productId) {
        String sql = """
            SELECT id, storage_place_id, product_id, quantity,
                   total_volume, total_weight_kg
            FROM storage_balance
            WHERE storage_place_id = ? AND product_id = ?
            """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, storagePlaceId);
            statement.setLong(2, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении остатка товара", e);
        }
    }

    private StorageBalance mapRow(ResultSet resultSet) throws SQLException {
        return new StorageBalance(
                resultSet.getLong("id"),
                resultSet.getLong("storage_place_id"),
                resultSet.getLong("product_id"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("total_volume"),
                resultSet.getBigDecimal("total_weight_kg")
        );
    }
}