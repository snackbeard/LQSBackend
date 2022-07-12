package de.capouschek.airqualitybackend.controllers;

import de.capouschek.airqualitybackend.classes.ControllerEsp;
import de.capouschek.airqualitybackend.classes.ControllerSubscription;
import de.capouschek.airqualitybackend.classes.QualityObject;
import de.capouschek.airqualitybackend.classes.User;
import de.capouschek.airqualitybackend.exceptions.*;
import de.capouschek.airqualitybackend.infrastructure.DBService;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RestController
public class Controller {

    // https://www.codejava.net/frameworks/spring-boot/form-authentication-with-jdbc-and-mysql

    public Controller() {
        System.out.println("controller constructor");
        try {
            service = new DBService("jdbc:mariadb://192.168.178.99:3306/lqs", "dbuser", "password");
        } catch (ServiceInitializationException e) {
            System.out.println(e.getMessage());
        }
    }

    private DBService service;

    private int countTvocs = 1;
    private int countEco2 = 1;

    private List<QualityObject> tvocs = new ArrayList<>();
    private List<QualityObject> eco2 = new ArrayList<>();

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


    @GetMapping(value = "/user.{userId}.subscribeTo.{controllerId}")
    public boolean subscribeToController(@PathVariable long userId, @PathVariable long controllerId) throws StoreException, DuplicateException {

        return User.subscribeToController(this.service.getConnection(), userId, controllerId);

    }

    @GetMapping(value = "/user.{userId}.unsubscribeFrom.{controllerId}")
    public boolean unsubscribeFromController(@PathVariable long userId, @PathVariable long controllerId) throws StoreException {

        return User.unsubscribeFromController(this.service.getConnection(), userId, controllerId);

    }

    @GetMapping(value = "/controller.{userId}")
    public List<ControllerSubscription> getAllControllers(@PathVariable long userId) throws FetchException {

        System.out.println("getAllControllers for user: " + userId);

        return ControllerEsp.getAll(this.service.getConnection(), userId);

    }

    @PostMapping(value = "/controller.register")
    public int registerController(@RequestBody ControllerEsp controllerEsp) throws StoreException, DuplicateException {

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
