package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Entity.Enums.CategoryEnum;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FindIngredientByIdRepository {
    private final FindStockMovementsByIngredientIdRepository findStockMovementsByIngredientIdRepository;

    public FindIngredientByIdRepository(FindStockMovementsByIngredientIdRepository findStockMovementsByIngredientIdRepository) {
        this.findStockMovementsByIngredientIdRepository = findStockMovementsByIngredientIdRepository;
    }

    public Ingredient execute(Integer id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(resultSet.getInt("id"));
                    ingredient.setName(resultSet.getString("name"));
                    ingredient.setPrice(resultSet.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));

                    ingredient.setStockMovementList(findStockMovementsByIngredientIdRepository.execute(id));

                    return ingredient;
                }
            }
            throw new RuntimeException("Ingredient not found with id: " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}