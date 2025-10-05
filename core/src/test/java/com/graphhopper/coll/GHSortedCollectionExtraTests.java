package com.graphhopper.coll;

import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GHSortedCollectionExtraTests {

    @Test
    void pollKeyAndRemove_coverMinOrderDuplicatesAndNonMinRemoval() {
        GHSortedCollection c = new GHSortedCollection();

        // keys -> values
        c.insert(7, 50);
        c.insert(8, 20);
        c.insert(9, 20);   // duplicate value to exercise tie-branch
        c.insert(10, 100);

        assertEquals(4, c.getSize());
        assertEquals(20, c.peekValue());

        int first  = c.pollKey();
        int second = c.pollKey();
        // Order among equals is not specified; check as a set
        Set<Integer> expectedMinKeys = new HashSet<>(Arrays.asList(8, 9));
        Set<Integer> actualMinKeys   = new HashSet<>(Arrays.asList(first, second));
        assertEquals(expectedMinKeys, actualMinKeys);

        assertEquals(2, c.getSize());      // (7,50) and (10,100) remain

        // Remove a non-min element; exercises a different removal branch
        c.remove(10, 100);
        assertEquals(1, c.getSize());
        assertEquals(7, c.peekKey());
        assertEquals(50, c.peekValue());

        // Final poll empties the collection
        assertEquals(7, c.pollKey());
        assertTrue(c.isEmpty());
    }
    @Test
    void toStringAndClear_coverEmptyAndNonEmpty_andSlidingMean() {
        GHSortedCollection c = new GHSortedCollection();

        // Empty branch in toString()/isEmpty()
        String s0 = c.toString();
        assertTrue(s0.startsWith("size=0"), s0);

        // Non-empty branch + minEntry path
        c.insert(1, 5);
        c.insert(2, 5);   // same value -> still two buckets total
        c.insert(3, 7);

        String s1 = c.toString();
        assertTrue(s1.contains("size=3"), s1);
        assertTrue(s1.contains("treeMap.size=2"), s1); // values 5 and 7
        assertTrue(s1.contains("minEntry=(" + c.peekKey() + "=>" + c.peekValue() + ")"), s1);

        // Cover previously untested methods
        assertEquals(20, c.getSlidingMeanValue());

        c.clear();
        assertTrue(c.isEmpty());
        assertEquals(0, c.getSize());
    }
}

