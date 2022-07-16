package de.capouschek.airqualitybackend.controllers;

import de.capouschek.airqualitybackend.classes.*;
import de.capouschek.airqualitybackend.exceptions.*;
import de.capouschek.airqualitybackend.infrastructure.DBService;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class Controller {

    // https://www.codejava.net/frameworks/spring-boot/form-authentication-with-jdbc-and-mysql

    private DBService service;

    private int countTvocs = 1;
    private int countEco2 = 1;

    private List<QualityObject> tvocs = new ArrayList<>();
    private List<QualityObject> eco2 = new ArrayList<>();

    private Map<Long, ControllerData> controllerDataMap = new HashMap<>();

    public Controller() throws FetchException {
        System.out.println("controller constructor");
        try {
            // create service instance
            service = new DBService("jdbc:mariadb://192.168.178.99:3306/lqs", "dbuser", "password");
        } catch (ServiceInitializationException e) {
            System.out.println(e.getMessage());
        }

        try {
            // fetch all controllers with names, objectId and add them to the Map
            String sql = "SELECT ID, name FROM Controller";
            PreparedStatement prep = this.service.getConnection().prepareStatement(sql);
            ResultSet resultSet = prep.executeQuery();
            while (resultSet.next()) {

                long id = resultSet.getLong(1);

                this.controllerDataMap
                        .put(id, new ControllerData(resultSet.getString(2)));

                // TODO: remove fake Data
                // this.controllerDataMap.get(id).setDataEco2(generateValues(96, 200, 210));
                // this.controllerDataMap.get(id).setDataTvoc(generateValues(96, 100, 110));


            }

        } catch (SQLException e) {
            throw new FetchException("Failed to initiate Controllers!");
        }
    }

    /**
     * Add User to Database.
     * @param user User Object
     * @return new User Object
     * @throws StoreException
     * @throws DuplicateException
     */
    @PostMapping(value = "/user")
    public User addUser(@RequestBody User user) throws StoreException, DuplicateException {

        user.register(service.getConnection());

        return user;

    }

    /**
     * Login User.
     * @param user object for login.
     * @return User object if login is ok.
     * @throws LoginFailedException
     * @throws FetchException
     */
    @PostMapping(value = "/user.login")
    public User login(@RequestBody User user) throws LoginFailedException, FetchException {

        user.login(this.service.getConnection());

        return user;

    }

    /**
     * Get data of controllers subscribed by user.
     * @param userId Id of the user.
     * @param backwards Amount of old values.
     * @return List of Data.
     * @throws FetchException
     */
    @GetMapping(value = "/user.{userId}.fetchDataTvoc.{backwards}")
    public List<ControllerSingleData> fetchDataTvoc(@PathVariable long userId, @PathVariable int backwards) throws FetchException {

        System.out.println("fetchData for tvoc : " + userId + " | backwards: " + backwards);

        List<ControllerSingleData> data = new ArrayList<>();

        List<ControllerColorId> subscribedControllerIds = ControllerEsp.getSubscribed(this.service.getConnection(), userId);

        for (ControllerColorId subscribedControllerId : subscribedControllerIds) {

            ControllerData controllerData = this.controllerDataMap.get(subscribedControllerId.getObjectId());

            ControllerSingleData fetchData = new ControllerSingleData(this.controllerDataMap.get(subscribedControllerId.getObjectId()).getName(), subscribedControllerId.getColor());
            List<QualityObject> finalDataTvoc = new ArrayList<>();

            if (controllerData.getDataTvoc().size() < backwards) {
                backwards = controllerData.getDataTvoc().size();
            }

            for (int i = controllerData.getDataTvoc().size() - 1; i > controllerData.getDataTvoc().size() - 1 - backwards; i--) {
                finalDataTvoc.add(controllerData.getDataTvoc().get(i));
            }

            Collections.reverse(finalDataTvoc);
            fetchData.setData(finalDataTvoc);

            data.add(fetchData);

        }


        return data;

    }

    /**
     * Get data of controllers subscribed by user.
     * @param userId Id of the user.
     * @param backwards Amount of old values.
     * @return List of Data.
     * @throws FetchException
     */
    @GetMapping(value = "/user.{userId}.fetchDataEco2.{backwards}")
    public List<ControllerSingleData> fetchDataEco2(@PathVariable long userId, @PathVariable int backwards) throws FetchException {

        System.out.println("fetchData for eco2: " + userId + " | backwards: " + backwards);

        List<ControllerSingleData> data = new ArrayList<>();

        List<ControllerColorId> subscribedControllerIds = ControllerEsp.getSubscribed(this.service.getConnection(), userId);

        for (ControllerColorId subscribedControllerId : subscribedControllerIds) {

            ControllerData controllerData = this.controllerDataMap.get(subscribedControllerId.getObjectId());

            ControllerSingleData fetchData = new ControllerSingleData(this.controllerDataMap.get(subscribedControllerId.getObjectId()).getName(), subscribedControllerId.getColor());
            List<QualityObject> finalDataEco2 = new ArrayList<>();

            if (controllerData.getDataEco2().size() < backwards) {
                backwards = controllerData.getDataEco2().size();
            }

            for (int i = controllerData.getDataEco2().size() - 1; i > controllerData.getDataEco2().size() - 1 - backwards; i--) {
                finalDataEco2.add(controllerData.getDataEco2().get(i));
            }

            Collections.reverse(finalDataEco2);
            fetchData.setData(finalDataEco2);

            data.add(fetchData);

        }


        return data;

    }

    /**
     * Subscribe a user to a controller.
     * @param userId Id of the user.
     * @param controllerId Id of the controller.
     * @return true if successful.
     * @throws StoreException
     * @throws DuplicateException
     */
    @GetMapping(value = "/user.{userId}.subscribeTo.{controllerId}")
    public boolean subscribeToController(@PathVariable long userId, @PathVariable long controllerId) throws StoreException, DuplicateException {

        return User.subscribeToController(this.service.getConnection(), userId, controllerId);

    }

    /**
     * Unsubscribe user from controller.
     * @param userId Id of the user.
     * @param controllerId Id of the controller.
     * @return true if successful.
     * @throws StoreException
     */
    @GetMapping(value = "/user.{userId}.unsubscribeFrom.{controllerId}")
    public boolean unsubscribeFromController(@PathVariable long userId, @PathVariable long controllerId) throws StoreException {

        return User.unsubscribeFromController(this.service.getConnection(), userId, controllerId);

    }

    @PostMapping(value = "/user.{userId}.changeColorOf.{controllerId}")
    public boolean changeControllerColor(@PathVariable long userId, @PathVariable long controllerId, @RequestBody ColorObject color) throws StoreException {
        return ControllerEsp.changeColor(this.service.getConnection(), userId, controllerId, color);
    }

    /**
     * Get all controllers with the information if the user is already subscribed to them.
     * @param userId Id of the user.
     * @return List of all Controllers.
     * @throws FetchException
     */
    @GetMapping(value = "/controller.{userId}")
    public List<ControllerSubscription> getAllControllers(@PathVariable long userId) throws FetchException {

        System.out.println("getAllControllers for user: " + userId);

        return ControllerEsp.getAll(this.service.getConnection(), userId);

    }

    /**
     * Register a new controller.
     * @param controllerEsp Object of the new controller.
     * @return Newly generated objectId.
     * @throws StoreException
     * @throws DuplicateException
     */
    @PostMapping(value = "/controller.register")
    public long registerController(@RequestBody ControllerEsp controllerEsp) throws StoreException {

        return controllerEsp.register(this.service.getConnection());
    }

    @PostMapping(value = "/controller.sendDataEco2")
    public void recieveDataEco2(@RequestBody SensorData sensorData) {
        if (this.controllerDataMap.get(sensorData.getControllerId()).getDataEco2().size() == 96) {
            this.controllerDataMap.get(sensorData.getControllerId()).getDataEco2().remove(0);
        }

        this.controllerDataMap
                .get(sensorData.getControllerId()).getDataEco2()
                .add(new QualityObject(sensorData.getValue(), getTimeNowAsString()));

    }

    @PostMapping(value = "/controller.sendDataTvoc")
    public void recieveDataTvoc(@RequestBody SensorData sensorData) {
        if (this.controllerDataMap.get(sensorData.getControllerId()).getDataTvoc().size() == 96) {
            this.controllerDataMap.get(sensorData.getControllerId()).getDataTvoc().remove(0);
        }

        System.out.println("new tvoc data: " + sensorData.getControllerId() + " | " + sensorData.getValue());

        this.controllerDataMap
                .get(sensorData.getControllerId()).getDataTvoc().add(new QualityObject(sensorData.getValue(), getTimeNowAsString()));
    }

    private String getTimeNowAsString() {
        LocalDateTime local = LocalDateTime.now();
        int hour = local.getHour();
        int minute = local.getMinute();

        String hourS = "" + hour;
        String minuteS = "" + minute;

        if (hour < 10) {
            hourS = "0" + hour;
        }

        if (minute < 10) {
            minuteS = "0" + minute;
        }

        return "" + hourS + ":" + minuteS;
    }

    private List<QualityObject> generateValues(int amount, int rangeLower, int rangeUpper) {
        List<QualityObject> list = new ArrayList<>(amount);
        Random rand = new Random();

        for (int i = 0; i < amount; i++) {


            LocalDateTime local = LocalDateTime.now().minusMinutes(15 * i);
            int hour = local.getHour();
            int minute = local.getMinute();

            String hourS = "" + hour;
            String minuteS = "" + minute;

            if (hour < 10) {
                hourS = "0" + hour;
            }

            if (minute < 10) {
                minuteS = "0" + minute;
            }

            String time = "" + hourS + ":" + minuteS;

            list.add(i, new QualityObject(rand.nextInt(rangeLower, rangeUpper), time));

        }

        Collections.reverse(list);

        return list;
    }

}
