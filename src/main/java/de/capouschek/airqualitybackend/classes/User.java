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

    public void store(Connection connection) throws StoreException, DuplicateException {

        List<User> userList = new ArrayList<>();
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

    public User(String name, String password) {
        if (name == null || name.equals("") || !(name.trim().equals(name))) {
            throw new AssertionError("name doesn't match expected pattern!");
        }

        this.name = name;

        if (password == null || password.equals("")) {
            throw new AssertionError("password can't be null!");
        }

        this.password = password;
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
