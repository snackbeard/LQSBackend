package de.capouschek.airqualitybackend.classes;

import de.capouschek.airqualitybackend.exceptions.DuplicateException;
import de.capouschek.airqualitybackend.exceptions.FetchException;
import de.capouschek.airqualitybackend.exceptions.StoreException;
import de.capouschek.airqualitybackend.infrastructure.PrimaryKeyGen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ControllerEsp {

    private long objectId;
    private String name;

    public ControllerEsp(){};

    public ControllerEsp(String name) {
        if (name == null || name.equals("") || !(name.trim().equals(name))) {
            throw new AssertionError("name doesn't match expected pattern!");
        }

        this.name = name;
    }

    public int register(Connection connection) throws StoreException, DuplicateException {

        String getControllerSql = "SELECT * FROM Controller where name = ?";

        try {

            PreparedStatement prepGet = connection.prepareStatement(getControllerSql);
            prepGet.setString(1, this.name);

            ResultSet resultSet = prepGet.executeQuery();
            while (resultSet.next()) {
                throw new DuplicateException("Controller with name " + this.name + " already exists");
            }
            resultSet.close();


            this.objectId = PrimaryKeyGen.getNextID(connection);

            String addControllerSql = "INSERT INTO Controller Value (?, ?)";

            PreparedStatement prep = connection.prepareStatement(addControllerSql);
            prep.setLong(1, this.objectId);
            prep.setString(2, this.name);

            prep.executeUpdate();

        } catch (SQLException e) {
            throw new StoreException(e.getMessage());
        }

        return 1;
    }

    public static List<ControllerSubscription> getAll(Connection connection, long userId) throws FetchException {

        List<ControllerSubscription> all = new ArrayList<>();

        String sqlGetSubscribed = "SELECT User_Controller.controllerId, Controller.name FROM User_Controller\n" +
                "LEFT OUTER JOIN Controller ON User_Controller.controllerId = Controller.ID\n" +
                "WHERE User_Controller.userId = ?";

        String sqlGetRemaining = "SELECT * FROM Controller\n" +
                "WHERE ID NOT IN (\n" +
                "\tSELECT controllerId FROM User_Controller WHERE userId = ?\n" +
                ")";

        try {
            PreparedStatement prepGetSubscribed = connection.prepareStatement(sqlGetSubscribed);
            prepGetSubscribed.setLong(1, userId);
            ResultSet rs1 = prepGetSubscribed.executeQuery();
            while (rs1.next()) {
                ControllerEsp tempesp = new ControllerEsp();
                tempesp.setObjectId(rs1.getLong(1));
                tempesp.setName(rs1.getString(2));

                all.add(new ControllerSubscription(true, tempesp));
            }
            rs1.close();

            PreparedStatement prepGetRemaining = connection.prepareStatement(sqlGetRemaining);
            prepGetRemaining.setLong(1, userId);
            ResultSet rs2 = prepGetRemaining.executeQuery();
            while (rs2.next()) {
                ControllerEsp tempesp = new ControllerEsp();
                tempesp.setObjectId(rs2.getLong(1));
                tempesp.setName(rs2.getString(2));

                all.add(new ControllerSubscription(false, tempesp));
            }
            rs2.close();

            Collections.sort(all, Comparator.comparing(item -> item.getController().getObjectId()));

            return all;

        } catch (SQLException e) {
            throw new FetchException("Fehler beim Laden der Controller!");
        }


    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public long getObjectId() {
        return this.objectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
