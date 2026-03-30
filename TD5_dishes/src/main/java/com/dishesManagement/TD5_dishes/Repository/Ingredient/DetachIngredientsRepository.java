package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.DishIngredient;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class DetachIngredientsRepository {
    public void execute(Connection conn, List<DishIngredient> dishIngredients) {
        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return;
        }

        Map<Integer, List<DishIngredient>> dishIngredientsGroupByDishId = dishIngredients.stream()
                .collect(Collectors.groupingBy(di -> di.getDish().getId()));

        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ?";

        dishIngredientsGroupByDishId.forEach((dishId, list) -> {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
