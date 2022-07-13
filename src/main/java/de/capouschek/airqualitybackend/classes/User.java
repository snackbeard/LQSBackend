package de.capouschek.airqualitybackend.classes;

import de.capouschek.airqualitybackend.exceptions.DuplicateException;
import de.capouschek.airqualitybackend.exceptions.FetchException;
import de.capouschek.airqualitybackend.exceptions.LoginFailedException;
import de.capouschek.airqualitybackend.exceptions.StoreException;
import de.capouschek.airqualitybackend.infrastructure.PrimaryKeyGen;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User {

    private long objectId;
    private String name;
    private String password;


    public User(String name, String password) {
        if (name == null || name.equals("") || !(name.trim().equals(name))) {
            throw new AssertionError("Ungültiger Name!");
        }

        this.name = name;

        if (password == null || password.equals("")) {
            throw new AssertionError("Passwort darf nicht leer sein!");
        }

        this.password = password;
    }

    public void register(Connection connection) throws StoreException, DuplicateException {

        String getUserSql = "SELECT * FROM User where name = ?";

        try {

            PreparedStatement prepGet = connection.prepareStatement(getUserSql);
            prepGet.setString(1, this.name);

            ResultSet resultSet = prepGet.executeQuery();
            while (resultSet.next()) {
                throw new DuplicateException("User with name " + this.name + " already exists");
            }
            resultSet.close();


            this.objectId = PrimaryKeyGen.getNextID(connection);

            String addUserSql = "INSERT INTO User VALUE (?, ?, ?)";

            PreparedStatement prep = connection.prepareStatement(addUserSql);
            prep.setLong(1, this.objectId);
            prep.setString(2, this.name);
            prep.setString(3, this.password);

            prep.executeUpdate();

        } catch (SQLException e) {
            throw new StoreException(e.getMessage());
        }
    }

    public void login(Connection connection) throws FetchException, LoginFailedException {

        String loginSql = "SELECT * FROM User where name = ?";

        try {

            PreparedStatement prep = connection.prepareStatement(loginSql);
            prep.setString(1, this.name);

            ResultSet resultSet = prep.executeQuery();

            while (resultSet.next()) {

                this.objectId = resultSet.getLong(1);

                if (!(this.password.equals(resultSet.getString(3)))) {
                    throw new LoginFailedException("Wrong credentials!");
                }
                resultSet.close();
                return;
            }
            throw new LoginFailedException("Unknown account!");


        } catch (SQLException e) {
            throw new FetchException(e.getMessage());
        }
    }

    public static boolean subscribeToController(Connection connection, long userId, long controllerId) throws StoreException, DuplicateException {

        String sqlGet = "SELECT * FROM User_Controller WHERE userId = ? AND controllerId = ?";
        String sql = "INSERT INTO User_Controller (userId, color, controllerId) VALUES (?, ?, ?)";

        try {
            PreparedStatement prepGet = connection.prepareStatement(sqlGet);
            prepGet.setLong(1, userId);
            prepGet.setLong(2, controllerId);
            ResultSet resultSetGet = prepGet.executeQuery();
            while (resultSetGet.next()) {
                throw new DuplicateException("Already subscribed to this controller!");
            }

            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setLong(1, userId);
            prep.setString(2, "0xff64b5f6"); // default color
            prep.setLong(3, controllerId);
            prep.executeUpdate();

        } catch (SQLException e) {
            throw new StoreException("Fehler beim Subscriben!");
        }

        return true;

    }

    public static boolean unsubscribeFromController(Connection connection, long userId, long controllerId) throws StoreException {
        String sql = "DELETE FROM User_Controller WHERE userId = ? AND controllerId = ?";

        try {
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setLong(1, userId);
            prep.setLong(2, controllerId);
            prep.executeUpdate();

        } catch (SQLException e) {
            throw new StoreException("Fehler beim unsubscriben!");
        }

        return true;
    }

    public long getObjectId() {
        return this.objectId;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
}
