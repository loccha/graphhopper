package com.graphhopper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import com.conveyal.gtfs.model.Service;
import com.conveyal.gtfs.model.Calendar;

public class ServiceTest {

    //should return false if two services do not overlap
    @Test
    public void checkOverlapNullCondition() {

        Service s1 = new Service("service_test1");
        Service s2 = new Service("service_test2");

        //left side calendar null
        s1.calendar = null;
        s2 = new Service("service_test2");
        s2.calendar = new Calendar();
        s2.calendar.monday = 1;
        assertFalse(Service.checkOverlap(s1, s2));
        
        //right side calendar null
        s1.calendar = new Calendar();
        s1.calendar.monday = 1;
        s2.calendar = null;
        assertFalse(Service.checkOverlap(s1, s2));

    }


    @Test
    public void checkOvelapOverlapingCalendars() {
        Service s1 = new Service("service_test1");
        Service s2 = new Service("service_test2");

        //no overlapping days
        s1.calendar = new Calendar();
        s2.calendar = new Calendar();
        assertFalse(Service.checkOverlap(s1, s2));

        //both calendars overlap on monday
        s1.calendar.monday = 1;
        s2.calendar.monday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

        //both calendars overlap on tuesday
        s1.calendar = new Calendar();
        s1.calendar.tuesday = 1;
        s2.calendar = new Calendar();
        s2.calendar.tuesday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

        //both calendars overlap on wednesday
        s1.calendar = new Calendar();
        s1.calendar.wednesday = 1;
        s2.calendar = new Calendar();
        s2.calendar.wednesday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

        //both calendars overlap on thursday
        s1.calendar = new Calendar();
        s1.calendar.thursday = 1;
        s2.calendar = new Calendar();
        s2.calendar.thursday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

        //both calendars overlap on friday
        s1.calendar = new Calendar();
        s1.calendar.friday = 1;
        s2.calendar = new Calendar();
        s2.calendar.friday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

        //both calendars overlap on saturday
        s1.calendar = new Calendar();
        s1.calendar.saturday = 1;
        s2.calendar = new Calendar();
        s2.calendar.saturday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

        //both calendars overlap on sunday
        s1.calendar = new Calendar();
        s1.calendar.sunday = 1;
        s2.calendar = new Calendar();
        s2.calendar.sunday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

    }
    
}
