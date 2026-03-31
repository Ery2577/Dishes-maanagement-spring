package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.Enums.MovementTypeEnum;
import com.dishesManagement.TD5_dishes.Entity.Enums.Unit;
import com.dishesManagement.TD5_dishes.Entity.StockMovement;
import com.dishesManagement.TD5_dishes.Entity.StockValue;
import com.dishesManagement.TD5_dishes.Service.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FindStockMovementsByIngredientIdRepository {

    public List<StockMovement> execute(Integer id) {
        List<StockMovement> stockMovementList = new ArrayList<>();
        String sql = """
                    SELECT id, quantity, unit, type, creation_datetime
                    FROM stock_movement
                    WHERE id_ingredient = ?
                """;

        try (Connection connection = DataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    StockMovement stockMovement = new StockMovement();
                    stockMovement.setId(resultSet.getInt("id"));
                    stockMovement.setType(MovementTypeEnum.valueOf(resultSet.getString("type")));
                    stockMovement.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());

                    StockValue stockValue = new StockValue();
                    stockValue.setQuantity(resultSet.getDouble("quantity"));
                    stockValue.setUnit(Unit.valueOf(resultSet.getString("unit")));
                    stockMovement.setValue(stockValue);

                    stockMovementList.add(stockMovement);
                }
            }
            return stockMovementList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}