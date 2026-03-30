package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Repository.Utils.GetNextSerialValueRepository;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@Repository
public class SaveIngredientRepository {
    private final GetNextSerialValueRepository getNextSerialValueRepository;
    private final InsertIngredientStockMovementsRepository insertIngredientStockMovementsRepository;
    private final FindIngredientByIdRepository findIngredientByIdRepository;

    public SaveIngredientRepository(GetNextSerialValueRepository getNextSerialValueRepository,
                                    InsertIngredientStockMovementsRepository insertIngredientStockMovementsRepository,
                                    FindIngredientByIdRepository findIngredientByIdRepository) {
        this.getNextSerialValueRepository = getNextSerialValueRepository;
        this.insertIngredientStockMovementsRepository = insertIngredientStockMovementsRepository;
        this.findIngredientByIdRepository = findIngredientByIdRepository;
    }

    public Ingredient execute(Ingredient toSave) {

        String upsertIngredientSql = """
                    INSERT INTO ingredient (id, name, price, category)
                    VALUES (?, ?, ?, ?::ingredient_category)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        category = EXCLUDED.category,
                        price = EXCLUDED.price
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Integer ingredientId;
            try (PreparedStatement ps = conn.prepareStatement(upsertIngredientSql)) {

                int nextId = getNextSerialValueRepository.execute(conn, "ingredient", "id");
                ingredientId = (toSave.getId() != null) ? toSave.getId() : nextId;

                ps.setInt(1, ingredientId);
                ps.setString(2, toSave.getName());

                if (toSave.getPrice() != null) {
                    ps.setDouble(3, toSave.getPrice());
                } else {
                    ps.setNull(3, Types.DOUBLE);
                }

                ps.setString(4, toSave.getCategory().name());
                ps.executeUpdate();
            }

            insertIngredientStockMovementsRepository.execute(conn, toSave);

            conn.commit();
            return findIngredientByIdRepository.execute(ingredientId);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de l'ingrédient", e);
        }
    }
}