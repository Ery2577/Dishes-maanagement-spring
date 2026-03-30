package com.dishesManagement.TD5_dishes.Repository.Order;

import com.dishesManagement.TD5_dishes.Entity.DishOrder;
import com.dishesManagement.TD5_dishes.Repository.Utils.GetNextSerialValueRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AttachOrdersRepository {
    private final GetNextSerialValueRepository getNextSerialValueRepository;

    public AttachOrdersRepository(GetNextSerialValueRepository getNextSerialValueRepository) {
        this.getNextSerialValueRepository = getNextSerialValueRepository;
    }

    public void execute(Connection conn, Integer orderId, List<DishOrder> dishOrders) throws SQLException {
        if (dishOrders == null || dishOrders.isEmpty()) {
            return;
        }

        String attachSql = """
                    INSERT INTO dish_order (id, id_order, id_dish, quantity)
                    VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            int nextId = getNextSerialValueRepository.execute(conn, "dish_order", "id");

            for (DishOrder dishOrder : dishOrders) {
                ps.setInt(1, nextId);
                ps.setInt(2, orderId);
                ps.setInt(3, dishOrder.getDish().getId());
                ps.setDouble(4, dishOrder.getQuantity());

                ps.addBatch();
                nextId++;
            }
            ps.executeBatch();
        }
    }
}
