package com.dishesManagement.TD5_dishes.Repository.Dish;

import com.dishesManagement.TD5_dishes.Entity.Dish;
import com.dishesManagement.TD5_dishes.Entity.Enums.DishTypeEnum;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FindAllDishesRepository {
    private final FindIngredientByDishIdRepository findIngredientByDishIdRepository;

    public FindAllDishesRepository(FindIngredientByDishIdRepository findIngredientByDishIdRepository) {
        this.findIngredientByDishIdRepository = findIngredientByDishIdRepository;
    }

    public List<Dish> execute() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT id, name, dish_type, selling_price FROM dish";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dish dish = new Dish();
                int id = rs.getInt("id");
                dish.setId(id);
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setPrice(rs.getObject("selling_price") == null ? null : rs.getDouble("selling_price"));

                dish.setDishIngredients(findIngredientByDishIdRepository.execute(id));
                dishes.add(dish);
            }
            return dishes;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des plats", e);
        }
    }
}