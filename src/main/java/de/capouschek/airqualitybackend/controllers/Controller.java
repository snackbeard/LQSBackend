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
                this.controllerDataMap.get(id).setDataEco2(generateValues(96, 200, 210));
                this.controllerDataMap.get(id).setDataTvoc(generateValues(96, 100, 110));


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
     * @param userId Id of the requesting user.
     * @param eco2b Old values from eco2.
     * @param tvocsb Old values from tvoc.
     * @return List of the controllers with the values.
     * @throws FetchException
     */
    @GetMapping(value = "/user.{userId}.fetchData.{eco2b}.{tvocsb}")
    public List<ControllerData> fetchData(@PathVariable long userId, @PathVariable int eco2b, @PathVariable int tvocsb) throws FetchException {

        List<ControllerData> data = new ArrayList<>();

        List<Long> subscribedControllerIds = ControllerEsp.getSubscribed(this.service.getConnection(), userId);

        for (Long subscribedControllerId : subscribedControllerIds) {

            ControllerData controllerData = this.controllerDataMap.get(subscribedControllerId);
            List<QualityObject> finalDataTvco = new ArrayList<>();
            List<QualityObject> finalDataEco2 = new ArrayList<>();

            for (int i = controllerData.getDataTvoc().size() - 1; i > controllerData.getDataTvoc().size() - 1 - tvocsb; i--) {
                finalDataTvco.add(controllerData.getDataTvoc().get(i));
            }

            Collections.reverse(finalDataTvco);
            controllerData.setDataTvoc(finalDataTvco);

            for (int i = controllerData.getDataEco2().size() - 1; i > controllerData.getDataEco2().size() - 1 - eco2b; i--) {
                finalDataEco2.add(controllerData.getDataEco2().get(i));
            }

            Collections.reverse(finalDataEco2);
            controllerData.setDataEco2(finalDataEco2);

            data.add(controllerData);

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
    public long registerController(@RequestBody ControllerEsp controllerEsp) throws StoreException, DuplicateException {

        return controllerEsp.register(this.service.getConnection());
    }







    @GetMapping("/gettvocs.{backwards}")
    // TODO: später dann überprüfen, ob es lower/upper überhaupt schon gibt und entsprechend daten zurückgeben
    public List<QualityObject> getTvocs(@PathVariable int backwards) {

        if (tvocs.isEmpty()) {
            tvocs = generateValues(96, 100, 110);
        }

        List<QualityObject> finalList = new ArrayList();


        for (int i = tvocs.size() - 1; i > tvocs.size() - 1 - backwards; i--) {
            finalList.add(tvocs.get(i));
        }


        System.out.println("retrived tvocs back to " + (tvocs.size() -backwards) + ": " + countTvocs);
        this.countTvocs++;

        Collections.reverse(finalList);

        return finalList;

    }

    @GetMapping("/geteco2.{backwards}")
    // TODO: später dann überprüfen, ob es lower/upper überhaupt schon gibt und entsprechend daten zurückgeben
    public List<QualityObject> getEco2(@PathVariable int backwards) {

        if (eco2.isEmpty()) {
            eco2 = generateValues(96, 200, 210);
        }

        List<QualityObject> finalList = new ArrayList<>();

        for (int i = eco2.size() - 1; i > eco2.size() - 1 - backwards; i--) {
            finalList.add(eco2.get(i));
        }

        System.out.println("retrived eco2 back to " + (eco2.size() - backwards) + ": " + countEco2);
        this.countEco2++;

        Collections.reverse(finalList);

        return finalList;
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
