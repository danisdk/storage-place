package ru.storageplace.repository;

import ru.storageplace.db.Database;
import ru.storageplace.model.StoragePlaceType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StoragePlaceTypeRepository {

    public List<StoragePlaceType> findAll() {
        String sql = """
                SELECT id, code, name, description
                FROM storage_place_type
                ORDER BY id
                """;

        List<StoragePlaceType> types = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                types.add(mapRow(resultSet));
            }

            return types;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении типов мест хранения", e);
        }
    }

    public Optional<StoragePlaceType> findById(Long id) {
        String sql = """
                SELECT id, code, name, description
                FROM storage_place_type
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
            throw new RuntimeException("Ошибка при получении типа места хранения по id", e);
        }
    }

    public Long create(StoragePlaceType type) {
        String sql = """
                INSERT INTO storage_place_type (code, name, description)
                VALUES (?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, type.getCode());
            statement.setString(2, type.getName());
            statement.setString(3, type.getDescription());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    type.setId(id);
                    return id;
                }

                throw new SQLException("PostgreSQL не вернул id созданного типа места хранения");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании типа места хранения", e);
        }
    }

    public void update(StoragePlaceType type) {
        String sql = """
                UPDATE storage_place_type
                SET code = ?,
                    name = ?,
                    description = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, type.getCode());
            statement.setString(2, type.getName());
            statement.setString(3, type.getDescription());
            statement.setLong(4, type.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Тип места хранения с id = " + type.getId() + " не найден");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении типа места хранения", e);
        }
    }

    public void deleteById(Long id) {
        String sql = """
                DELETE FROM storage_place_type
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении типа места хранения", e);
        }
    }

    private StoragePlaceType mapRow(ResultSet resultSet) throws SQLException {
        return new StoragePlaceType(
                resultSet.getLong("id"),
                resultSet.getString("code"),
                resultSet.getString("name"),
                resultSet.getString("description")
        );
    }
}