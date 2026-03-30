package com.dishesManagement.TD5_dishes.Repository.Dish;

import com.dishesManagement.TD5_dishes.Entity.Dish;
import com.dishesManagement.TD5_dishes.Entity.Enums.DishTypeEnum;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FindDishByIdRepository {
    private final FindIngredientByDishIdRepository findIngredientByDishIdRepository;

    public FindDishByIdRepository(FindIngredientByDishIdRepository findIngredientByDishIdRepository) {
        this.findIngredientByDishIdRepository = findIngredientByDishIdRepository;
    }

    public Dish execute(Integer id) {
        String sql = """
                    SELECT id, name, dish_type, selling_price 
                    FROM dish 
                    WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Dish dish = new Dish();
                    dish.setId(resultSet.getInt("id"));
                    dish.setName(resultSet.getString("name"));
                    dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                    dish.setPrice(resultSet.getObject("selling_price") == null
                            ? null : resultSet.getDouble("selling_price"));

                    dish.setDishIngredients(findIngredientByDishIdRepository.execute(id));

                    return dish;
                }
            }
            throw new RuntimeException("Dish not found with id: " + id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat", e);
        }
    }
}