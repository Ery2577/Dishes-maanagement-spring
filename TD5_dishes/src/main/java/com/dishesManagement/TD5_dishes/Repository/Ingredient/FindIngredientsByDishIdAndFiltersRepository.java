package com.dishesManagement.TD5_dishes.Repository.Ingredient;

import com.dishesManagement.TD5_dishes.Entity.Ingredient;
import com.dishesManagement.TD5_dishes.Service.DataSource;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FindIngredientsByDishIdAndFiltersRepository {
    private final DataSource dataSource;

    public FindIngredientsByDishIdAndFiltersRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Ingredient> execute(Integer dishId, String name, Double priceAround) {
        List<Ingredient> ingredients = new ArrayList<>();

        String sql = "SELECT i.* FROM ingredient i " +
                "JOIN dish_ingredient di ON i.id = di.id_ingredient " +
                "WHERE di.id_dish = " + dishId;

        if (name != null && !name.isEmpty()) {
            sql += " AND i.name ILIKE '%" + name + "%'";
        }

        if (priceAround != null) {
            double minPrice = priceAround - 50;
            double maxPrice = priceAround + 50;
            sql += " AND i.unit_price BETWEEN " + minPrice + " AND " + maxPrice;
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId((int) rs.getLong("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setUnitPrice(rs.getDouble("unit_price"));
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }
}