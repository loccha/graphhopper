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

        //one of the calendar has a service
        s1.calendar.monday = 1;
        assertFalse(Service.checkOverlap(s1, s2));

        //both has services but not at the same time
        s2.calendar.tuesday = 1;

        //both calendars overlap on monday
        s2.calendar.monday = 1;
        assertTrue(Service.checkOverlap(s1, s2));

    }
    
}
