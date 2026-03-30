package com.dishesManagement.TD5_dishes.Repository.Order;

import com.dishesManagement.TD5_dishes.Entity.Order;
import com.dishesManagement.TD5_dishes.Repository.Utils.GetNextSerialValueRepository;
import com.dishesManagement.TD5_dishes.Service.DBConnection;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Repository
public class SaveOrderRepository {
    private final GetNextSerialValueRepository getNextSerialValueRepository;
    private final DetachOrdersRepository detachOrdersRepository;
    private final AttachOrdersRepository attachOrdersRepository;
    private final FindOrderByReferenceRepository findOrderByReferenceRepository;

    public SaveOrderRepository(GetNextSerialValueRepository getNextSerialValueRepository,
                               DetachOrdersRepository detachOrdersRepository,
                               AttachOrdersRepository attachOrdersRepository,
                               FindOrderByReferenceRepository findOrderByReferenceRepository) {
        this.getNextSerialValueRepository = getNextSerialValueRepository;
        this.detachOrdersRepository = detachOrdersRepository;
        this.attachOrdersRepository = attachOrdersRepository;
        this.findOrderByReferenceRepository = findOrderByReferenceRepository;
    }

    public Order execute(Order order) {
        String upsertOrderSql = """
                INSERT INTO "order" (id, reference, creation_datetime)
                VALUES (?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Integer orderId;
            try (PreparedStatement ps = conn.prepareStatement(upsertOrderSql)) {
                int nextId = getNextSerialValueRepository.execute(conn, "\"order\"", "id");

                orderId = (order.getId() != null) ? order.getId() : nextId;

                ps.setInt(1, orderId);
                ps.setString(2, order.getReference());
                ps.setTimestamp(3, Timestamp.from(order.getCreationDatetime()));
                ps.executeUpdate();
            }

            detachOrdersRepository.execute(conn, orderId);
            attachOrdersRepository.execute(conn, orderId, order.getDishOrderList());

            conn.commit();
            return findOrderByReferenceRepository.execute(order.getReference());

        } catch (SQLException e) {
            if (e.getMessage().contains("order_reference_unique")) {
                throw new RuntimeException("Order already exists with reference " + order.getReference());
            }
            throw new RuntimeException(e);
        }
    }
}