package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Repository.Utils.GetNextSerialValueRepository;
import com.dishesManagement.TD5_dishes.Service.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CreateIngredientsRepository {
    private final GetNextSerialValueRepository getNextSerialValueRepository;

    public CreateIngredientsRepository(GetNextSerialValueRepository getNextSerialValueRepository) {
        this.getNextSerialValueRepository = getNextSerialValueRepository;
    }

    public List<Ingredient> execute(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }

        List<Ingredient> savedIngredients = new ArrayList<>();
        String insertSql = """
                    INSERT INTO ingredient (id, name, category, price)
                    VALUES (?, ?, ?::ingredient_category, ?)
                """;

        try (Connection conn = DataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {

                    int idToUse = (ingredient.getId() != null)
                            ? ingredient.getId()
                            : getNextSerialValueRepository.execute(conn, "ingredient", "id");

                    ps.setInt(1, idToUse);
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());

                    ps.executeUpdate();

                    ingredient.setId(idToUse);
                    savedIngredients.add(ingredient);
                }
                conn.commit();
                return savedIngredients;

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de la création groupée des ingrédients", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}