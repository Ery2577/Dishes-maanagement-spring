package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.DishIngredient;
import com.dishesManagement.TD5_dishes.Repository.Utils.GetNextSerialValueRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AttachIngredientsRepository {
    private final GetNextSerialValueRepository getNextSerialValueRepository;

    public AttachIngredientsRepository(GetNextSerialValueRepository getNextSerialValueRepository) {
        this.getNextSerialValueRepository = getNextSerialValueRepository;
    }

    public void execute(Connection conn, List<DishIngredient> ingredients) throws SQLException {
        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }

        String sql = """
                    INSERT INTO dish_ingredient (id, id_ingredient, id_dish, required_quantity, unit)
                    VALUES (?, ?, ?, ?, ?::unit)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient dishIngredient : ingredients) {
                int nextId = getNextSerialValueRepository.execute(conn, "dish_ingredient", "id");

                ps.setInt(1, nextId);
                ps.setInt(2, dishIngredient.getIngredient().getId());
                ps.setInt(3, dishIngredient.getDish().getId());
                ps.setDouble(4, dishIngredient.getQuantity());
                ps.setString(5, dishIngredient.getUnit().name());

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
