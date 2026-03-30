package com.dishesManagement.TD5_dishes.Repository.Dish;

import com.dishesManagement.TD5_dishes.Entity.DishIngredient;
import com.dishesManagement.TD5_dishes.Entity.Enums.CategoryEnum;
import com.dishesManagement.TD5_dishes.Entity.Enums.Unit;
import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FindIngredientByDishIdRepository {

    public List<DishIngredient> execute(Integer idDish) {
        List<DishIngredient> dishIngredients = new ArrayList<>();
        String sql = """
                    SELECT i.id, i.name, i.price, i.category, 
                           di.required_quantity, di.unit
                    FROM ingredient i 
                    JOIN dish_ingredient di ON di.id_ingredient = i.id 
                    WHERE di.id_dish = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idDish);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(resultSet.getInt("id"));
                    ingredient.setName(resultSet.getString("name"));
                    ingredient.setPrice(resultSet.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));

                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setIngredient(ingredient);
                    dishIngredient.setQuantity(resultSet.getObject("required_quantity") == null
                            ? null : resultSet.getDouble("required_quantity"));
                    dishIngredient.setUnit(Unit.valueOf(resultSet.getString("unit")));

                    dishIngredients.add(dishIngredient);
                }
            }
            return dishIngredients;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients du plat", e);
        }
    }
}