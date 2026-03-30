package com.dishesManagement.TD5_dishes.Repository.Order;

import com.dishesManagement.TD5_dishes.Entity.Dish;
import com.dishesManagement.TD5_dishes.Entity.DishOrder;
import com.dishesManagement.TD5_dishes.Repository.Dish.FindDishByIdRepository;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FindDishOrderByIdOrderRepository {
    private final FindDishByIdRepository findDishByIdRepository;

    public FindDishOrderByIdOrderRepository(FindDishByIdRepository findDishByIdRepository) {
        this.findDishByIdRepository = findDishByIdRepository;
    }

    public List<DishOrder> execute(Integer idOrder) {
        List<DishOrder> dishOrders = new ArrayList<>();
        String sql = "SELECT id, id_dish, quantity FROM dish_order WHERE id_order = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idOrder);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Dish dish = findDishByIdRepository.execute(resultSet.getInt("id_dish"));

                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setId(resultSet.getInt("id"));
                    dishOrder.setQuantity(resultSet.getInt("quantity"));
                    dishOrder.setDish(dish);

                    dishOrders.add(dishOrder);
                }
            }
            return dishOrders;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des plats de la commande", e);
        }
    }
}