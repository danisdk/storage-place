package ru.storageplace.repository;

import ru.storageplace.db.Database;
import ru.storageplace.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    public List<Product> findAll() {
        String sql = """
                SELECT id, name, article, length_cm, width_cm, height_cm, weight_kg
                FROM product
                ORDER BY id
                """;

        List<Product> products = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapRow(resultSet));
            }

            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка товаров", e);
        }
    }

    public Optional<Product> findById(Long id) {
        String sql = """
                SELECT id, name, article, length_cm, width_cm, height_cm, weight_kg
                FROM product
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
            throw new RuntimeException("Ошибка при получении товара по id", e);
        }
    }

    public Long create(Product product) {
        String sql = """
                INSERT INTO product (name, article, length_cm, width_cm, height_cm, weight_kg)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getArticle());
            statement.setBigDecimal(3, product.getLengthCm());
            statement.setBigDecimal(4, product.getWidthCm());
            statement.setBigDecimal(5, product.getHeightCm());
            statement.setBigDecimal(6, product.getWeightKg());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    product.setId(id);
                    return id;
                }

                throw new SQLException("PostgreSQL не вернул id созданного товара");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании товара", e);
        }
    }

    public void update(Product product) {
        String sql = """
                UPDATE product
                SET name = ?,
                    article = ?,
                    length_cm = ?,
                    width_cm = ?,
                    height_cm = ?,
                    weight_kg = ?
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getArticle());
            statement.setBigDecimal(3, product.getLengthCm());
            statement.setBigDecimal(4, product.getWidthCm());
            statement.setBigDecimal(5, product.getHeightCm());
            statement.setBigDecimal(6, product.getWeightKg());
            statement.setLong(7, product.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Товар с id = " + product.getId() + " не найден");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении товара", e);
        }
    }

    public void deleteById(Long id) {
        String sql = """
                DELETE FROM product
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении товара", e);
        }
    }

    private Product mapRow(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("article"),
                resultSet.getBigDecimal("length_cm"),
                resultSet.getBigDecimal("width_cm"),
                resultSet.getBigDecimal("height_cm"),
                resultSet.getBigDecimal("weight_kg")
        );
    }
}