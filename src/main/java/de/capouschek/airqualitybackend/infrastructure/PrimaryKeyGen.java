package de.capouschek.airqualitybackend.infrastructure;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class PrimaryKeyGen {

    public static long getNextID(Connection connection) throws SQLException {
        String sql = "SELECT NEXT VALUE FOR primaryId";
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        if(resultSet.next()) {
            return resultSet.getLong(1);
        }
        return 0;
    }
}
