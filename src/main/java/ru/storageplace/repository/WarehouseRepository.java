package ru.storageplace.repository;

import ru.storageplace.db.Database;
import ru.storageplace.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarehouseRepository {

    public List<Warehouse> findAll() {
        String sql = """
                SELECT id, name, address, description
                FROM warehouse
                ORDER BY id
                """;

        List<Warehouse> warehouses = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                warehouses.add(mapRow(resultSet));
            }

            return warehouses;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка складов", e);
        }
    }

    public Optional<Warehouse> findById(Long id) {
        String sql = """
                SELECT id, name, address, description
                FROM warehouse
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
            throw new RuntimeException("Ошибка при получении склада по id", e);
        }
    }

    public Long create(Warehouse warehouse) {
        String sql = """
                INSERT INTO warehouse (name, address, description)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, warehouse.getName());
            statement.setString(2, warehouse.getAddress());
            statement.setString(3, warehouse.getDescription());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    warehouse.setId(id);
                    return id;
                }

                throw new SQLException("PostgreSQL не вернул id созданного склада");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании склада", e);
        }
    }

    public void update(Warehouse warehouse) {
        String sql = """
                UPDATE warehouse
                SET name = ?,
                    address = ?,
                    description = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, warehouse.getName());
            statement.setString(2, warehouse.getAddress());
            statement.setString(3, warehouse.getDescription());
            statement.setLong(4, warehouse.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Склад с id = " + warehouse.getId() + " не найден");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении склада", e);
        }
    }

    public void deleteById(Long id) {
        String sql = """
                DELETE FROM warehouse
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении склада", e);
        }
    }

    private Warehouse mapRow(ResultSet resultSet) throws SQLException {
        return new Warehouse(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("address"),
                resultSet.getString("description")
        );
    }
}