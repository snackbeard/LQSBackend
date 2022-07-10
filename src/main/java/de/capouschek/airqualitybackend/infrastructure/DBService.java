package de.capouschek.airqualitybackend.infrastructure;

import de.capouschek.airqualitybackend.exceptions.ServiceInitializationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBService {

    private final Connection connection;

    public DBService(String url, String user, String password) throws ServiceInitializationException {
        try {
            this.connection = this.createConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServiceInitializationException(e.getMessage());
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    protected Connection createConnection(String jdbcUrl, String user, String password)
            throws ClassNotFoundException, SQLException {

        Class.forName("org.mariadb.jdbc.Driver");
        return DriverManager.getConnection(jdbcUrl, user, password);

    }

}
