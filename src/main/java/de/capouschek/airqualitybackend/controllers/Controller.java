package de.capouschek.airqualitybackend.controllers;

import de.capouschek.airqualitybackend.classes.QualityObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RestController
public class Controller {

    private int countTvocs = 1;
    private int countEco2 = 1;

    private List<QualityObject> tvocs = new ArrayList<>();
    private List<QualityObject> eco2 = new ArrayList<>();

    @GetMapping("/gettvocs.{backwards}")
    // TODO: später dann überprüfen, ob es lower/upper überhaupt schon gibt und entsprechend daten zurückgeben
    public List<QualityObject> getRandomObjects(@PathVariable int backwards) {

        if (tvocs.isEmpty()) {
            tvocs = generateValues(96, 100, 110);
        }

        List<QualityObject> finalList = new ArrayList();


        for (int i = tvocs.size() - 1; i > tvocs.size() - 1 - backwards; i--) {
            finalList.add(tvocs.get(i));
        }

        this.countTvocs++;
        System.out.println("retrived tvocs back to " + (tvocs.size() -backwards) + ": " + countTvocs);

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
