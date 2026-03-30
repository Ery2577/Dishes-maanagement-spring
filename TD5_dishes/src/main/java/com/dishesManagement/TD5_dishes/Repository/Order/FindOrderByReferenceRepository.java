package com.dishesManagement.TD5_dishes.Repository.Order;

import com.dishesManagement.TD5_dishes.Entity.Order;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class FindOrderByReferenceRepository {
    private final FindDishOrderByIdOrderRepository findDishOrderByIdOrderRepository;

    public FindOrderByReferenceRepository(FindDishOrderByIdOrderRepository findDishOrderByIdOrderRepository) {
        this.findDishOrderByIdOrderRepository = findDishOrderByIdOrderRepository;
    }

    public Order execute(String reference) {
        String sql = "SELECT id, reference, creation_datetime FROM \"order\" WHERE reference LIKE ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, reference);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Order order = new Order();
                    Integer idOrder = resultSet.getInt("id");
                    order.setId(idOrder);
                    order.setReference(resultSet.getString("reference"));
                    order.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());

                    order.setDishOrderList(findDishOrderByIdOrderRepository.execute(idOrder));

                    return order;
                }
            }
            throw new RuntimeException("Order not found with reference " + reference);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}