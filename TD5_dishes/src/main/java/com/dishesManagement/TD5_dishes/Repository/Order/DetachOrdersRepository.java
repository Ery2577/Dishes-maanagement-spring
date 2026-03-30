package com.dishesManagement.TD5_dishes.Repository.Order;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class DetachOrdersRepository {
    public void execute(Connection conn, Integer idOrder) {
        String sql = "DELETE FROM dish_order WHERE id_order = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOrder);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression des anciens plats de la commande", e);
        }
    }
}
