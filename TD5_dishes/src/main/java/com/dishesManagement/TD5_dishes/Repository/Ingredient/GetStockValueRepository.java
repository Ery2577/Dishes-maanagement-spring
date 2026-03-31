package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.Enums.Unit;
import com.dishesManagement.TD5_dishes.Entity.StockValue;
import com.dishesManagement.TD5_dishes.Service.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;

@Repository
public class GetStockValueRepository {

    public StockValue execute(Instant t, Integer ingredientId) {
        String sql = """
                     SELECT unit,
                            SUM(CASE
                                    WHEN type = 'IN' THEN quantity
                                    WHEN type = 'OUT' THEN -1 * quantity
                                    ELSE 0 END) AS actual_quantity
                     FROM stock_movement
                     WHERE id_ingredient = ? AND creation_datetime <= ?
                     GROUP BY unit
                     """;

        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, ingredientId);
            preparedStatement.setTimestamp(2, Timestamp.from(t));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    StockValue stockValue = new StockValue();
                    stockValue.setQuantity(resultSet.getDouble("actual_quantity"));
                    stockValue.setUnit(Unit.valueOf(resultSet.getString("unit")));
                    return stockValue;
                }
            }

            throw new RuntimeException("Aucun mouvement de stock trouvé pour l'ingrédient " + ingredientId);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du stock", e);
        }
    }
}