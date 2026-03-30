package com.dishesManagement.TD5_dishes.Repository.Utils;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class GetNextSerialValueRepository {
    private final GetSerialSequenceNameRepository getSerialSequenceNameRepository;

    public GetNextSerialValueRepository(GetSerialSequenceNameRepository getSerialSequenceNameRepository) {
        this.getSerialSequenceNameRepository = getSerialSequenceNameRepository;
    }

    public int execute(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceNameRepository.execute(conn, tableName, columnName);

        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
