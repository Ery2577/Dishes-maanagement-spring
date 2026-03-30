package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Entity.StockMovement;
import com.dishesManagement.TD5_dishes.Repository.Utils.GetNextSerialValueRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class InsertIngredientStockMovementsRepository {
    private final GetNextSerialValueRepository getNextSerialValueRepository;

    public InsertIngredientStockMovementsRepository(GetNextSerialValueRepository getNextSerialValueRepository) {
        this.getNextSerialValueRepository = getNextSerialValueRepository;
    }

    public void execute(Connection conn, Ingredient ingredient) {
        List<StockMovement> stockMovementList = ingredient.getStockMovementList();
        if (stockMovementList == null || stockMovementList.isEmpty()) {
            return;
        }

        String sql = """
                INSERT INTO stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime)
                VALUES (?, ?, ?, ?::movement_type, ?::unit, ?)
                ON CONFLICT (id) DO NOTHING
                """;

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (StockMovement stockMovement : stockMovementList) {
                int nextId = (stockMovement.getId() != null)
                        ? stockMovement.getId()
                        : getNextSerialValueRepository.execute(conn, "stock_movement", "id");

                preparedStatement.setInt(1, nextId);
                preparedStatement.setInt(2, ingredient.getId());
                preparedStatement.setDouble(3, stockMovement.getValue().getQuantity());
                preparedStatement.setString(4, stockMovement.getType().name());
                preparedStatement.setString(5, stockMovement.getValue().getUnit().name());
                preparedStatement.setTimestamp(6, Timestamp.from(stockMovement.getCreationDatetime()));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
