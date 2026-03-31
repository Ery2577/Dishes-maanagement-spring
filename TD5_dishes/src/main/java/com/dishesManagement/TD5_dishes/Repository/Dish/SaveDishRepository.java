package com.dishesManagement.TD5_dishes.Repository.Dish;

import com.dishesManagement.TD5_dishes.Entity.Dish;
import com.dishesManagement.TD5_dishes.Entity.DishIngredient;
import com.dishesManagement.TD5_dishes.Repository.Ingredient.AttachIngredientsRepository;
import com.dishesManagement.TD5_dishes.Repository.Ingredient.DetachIngredientsRepository;
import com.dishesManagement.TD5_dishes.Repository.Utils.GetNextSerialValueRepository;
import com.dishesManagement.TD5_dishes.Service.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
public class SaveDishRepository {
    private final GetNextSerialValueRepository getNextSerialValueRepository;
    private final DetachIngredientsRepository detachIngredientsRepository;
    private final AttachIngredientsRepository attachIngredientsRepository;
    private final FindDishByIdRepository findDishByIdRepository;

    public SaveDishRepository(GetNextSerialValueRepository getNextSerialValueRepository,
                              DetachIngredientsRepository detachIngredientsRepository,
                              AttachIngredientsRepository attachIngredientsRepository,
                              FindDishByIdRepository findDishByIdRepository) {
        this.getNextSerialValueRepository = getNextSerialValueRepository;
        this.detachIngredientsRepository = detachIngredientsRepository;
        this.attachIngredientsRepository = attachIngredientsRepository;
        this.findDishByIdRepository = findDishByIdRepository;
    }

    public Dish execute(Dish toSave) {
        String upsertDishSql = """
                    INSERT INTO dish (id, selling_price, name, dish_type)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                """;

        try (Connection conn = DataSource.getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {

                int nextId = getNextSerialValueRepository.execute(conn, "dish", "id");
                dishId = (toSave.getId() != null) ? toSave.getId() : nextId;

                ps.setInt(1, dishId);
                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());
                ps.executeUpdate();
            }

            List<DishIngredient> ingredients = toSave.getDishIngredients();
            detachIngredientsRepository.execute(conn, ingredients);
            attachIngredientsRepository.execute(conn, ingredients);

            conn.commit();
            return findDishByIdRepository.execute(dishId);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du plat", e);
        }
    }
}