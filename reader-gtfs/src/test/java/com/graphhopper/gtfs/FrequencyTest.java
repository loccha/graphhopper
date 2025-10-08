package com.graphhopper;
import com.graphhopper.gtfs.*;
import com.conveyal.gtfs.model.Frequency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


import com.github.javafaker.Faker;


public class FrequencyTest {

    private final Faker faker = new Faker();

    @Test
    public void testGetIdFormatsCorrectly() {

        Frequency f = new Frequency();

        f.trip_id = faker.lorem().word();

        //generate start_time
        int start_hour = faker.number().numberBetween(0, 20);
        int start_minute = faker.number().numberBetween(0, 59);
        int start_time = start_hour * 3600 + start_minute * 60;
        f.start_time = start_time;

        //generate end_time
        int duration = faker.number().numberBetween(600, 7200); // between 10 minutes and 2 hours
        int end_time = start_time + duration;
        f.end_time = end_time;

        f.headway_secs = faker.number().numberBetween(300, 3600); // between 5 minutes and 1 hour
        f.exact_times = 1;

        String id = f.getId();

        String expected = f.trip_id + "_" +
                String.format("%d:%02d:00", start_hour, start_minute) + "_to_" +
                String.format("%d:%02d:00", (end_time / 3600), (end_time % 3600) / 60) + "_every_" +
                String.format("%dm%02ds", f.headway_secs / 60, f.headway_secs % 60) + "_exact";


        assertEquals(expected, id);
    }
}


