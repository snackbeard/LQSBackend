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

    public ControllerEsp(){}

    public ControllerEsp(String name) {
        if (name == null || name.equals("") || !(name.trim().equals(name))) {
            throw new AssertionError("name doesn't match expected pattern!");
        }

        this.name = name;
    }

    public static List<ControllerColorId> getSubscribed(Connection connection, long userId) throws FetchException {
        String sql = "SELECT controllerId, color FROM User_Controller WHERE userId = ?";

        ArrayList<ControllerColorId> list = new ArrayList<>();

        try {
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setLong(1, userId);
            ResultSet resultSet = prep.executeQuery();
            while (resultSet.next()) {
                list.add(new ControllerColorId(resultSet.getLong(1), resultSet.getString(2)));
            }

            return list;

        } catch (SQLException e) {
            throw new FetchException("Fehler beim Laden der Sensordaten!");
        }
    }

    public long register(Connection connection) throws StoreException {

        String getControllerSql = "SELECT * FROM Controller where name = ?";

        try {

            PreparedStatement prepGet = connection.prepareStatement(getControllerSql);
            prepGet.setString(1, this.name);

            ResultSet resultSet = prepGet.executeQuery();
            while (resultSet.next()) {
                System.out.println("Known controller tried to register, returned id: " + resultSet.getLong(1));
                return resultSet.getLong(1);
            }
            resultSet.close();


            this.objectId = PrimaryKeyGen.getNextID(connection);

            String addControllerSql = "INSERT INTO Controller Value (?, ?)";

            PreparedStatement prep = connection.prepareStatement(addControllerSql);
            prep.setLong(1, this.objectId);
            prep.setString(2, this.name);

            prep.executeUpdate();

            System.out.println("New controlle registered with id: " + this.objectId);

            return this.objectId;

        } catch (SQLException e) {
            throw new StoreException(e.getMessage());
        }
    }

    public static List<ControllerSubscription> getAll(Connection connection, long userId) throws FetchException {

        List<ControllerSubscription> all = new ArrayList<>();

        String sqlGetSubscribed = "SELECT User_Controller.controllerId, User_Controller.color, Controller.name FROM User_Controller\n" +
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
                tempesp.setName(rs1.getString(3));

                all.add(new ControllerSubscription(true, tempesp, rs1.getString(2)));
            }
            rs1.close();

            PreparedStatement prepGetRemaining = connection.prepareStatement(sqlGetRemaining);
            prepGetRemaining.setLong(1, userId);
            ResultSet rs2 = prepGetRemaining.executeQuery();
            while (rs2.next()) {
                ControllerEsp tempesp = new ControllerEsp();
                tempesp.setObjectId(rs2.getLong(1));
                tempesp.setName(rs2.getString(2));

                all.add(new ControllerSubscription(false, tempesp, "0x00000000"));
            }
            rs2.close();

            Collections.sort(all, Comparator.comparing(item -> item.getController().getObjectId()));

            return all;

        } catch (SQLException e) {
            throw new FetchException("Fehler beim Laden der Controller!");
        }


    }

    public static boolean changeColor(Connection connection, long userId, long controllerId, ColorObject color) throws StoreException {
        String sql = "UPDATE User_Controller\n" +
                "SET color = ?\n" +
                "WHERE userId = ?\n" +
                "AND controllerId = ?";

        try {
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setString(1, color.getColor());
            prep.setLong(2, userId);
            prep.setLong(3, controllerId);
            prep.executeUpdate();

            return true;

        } catch (SQLException e) {
            throw new StoreException("Fehler beim Speichern der Farbe");
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
