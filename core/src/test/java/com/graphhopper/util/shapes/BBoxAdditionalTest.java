package com.graphhopper.util.shapes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BBoxAdditionalTest {

    // Test 1
    // Name: intersectsPrimitive_shouldReturnTrueForOverlap_andFalseForDisjoint
    // Intention: Cover primitive overload of intersects(minLon,maxLon,minLat,maxLat) for both outcomes.
    // Data motivation: Base box [30..40 lon, 10..20 lat]; one overlapping window and two with clear gaps.
    // Oracle: Axis-aligned rectangles intersect iff ranges overlap on both axes.
    @Test
    void intersectsPrimitive_shouldReturnTrueForOverlap_andFalseForDisjoint() {
        BBox base = new BBox(30, 40, 10, 20);

        assertTrue(base.intersects(35, 45, 15, 25));  // overlaps lon & lat
        assertFalse(base.intersects(41, 50, 10, 20)); // gap in lon
        assertFalse(base.intersects(30, 40, 21, 30)); // gap in lat
    }

    // Test 2
    // Name: equalsAndHashCode_shouldHandleEqualDifferentNullAndOtherType
    // Intention: Hit equals(Object) true/false branches and ClassCast path; verify hashCode for equal objects.
    // Data motivation: identical boxes, one differing in maxLon; explicit null and non-BBox argument.
    // Oracle: equal objects => equal hashCode; unequal boxes => !equals; equals(null) => false; other type => ClassCastException in this implementation.
    @Test
    void equalsAndHashCode_shouldHandleEqualDifferentNullAndOtherType() {
        BBox a1 = new BBox(30, 40, 10, 20);
        BBox a2 = new BBox(30, 40, 10, 20);
        BBox b  = new BBox(30, 41, 10, 20);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1, b);

        assertFalse(a1.equals(null));                        // annotated @Contract("null -> false")
        assertThrows(ClassCastException.class, () -> a1.equals("not a BBox"));
    }

    // Test 3
    // Name: isValid_shouldReflectOrdering_andElevationSentinels
    // Intention: Match isValid() rules: ordering checks and elevation-sentinel checks.
    // Data motivation: invalid equal/inverted bounds; elevation=true with equal (valid), inverted (invalid), and sentinel extremes (invalid).
    // Oracle: Based on BBox.isValid() implementation (ordering checks and elevation sentinel rules).
    @Test
    void isValid_shouldReflectOrdering_andElevationSentinels() {
        // valid 2D
        assertTrue(new BBox(30, 40, 10, 20).isValid());

        // invalid ordering / equality
        assertFalse(new BBox(40, 40, 10, 20).isValid()); // minLon == maxLon
        assertFalse(new BBox(50, 40, 10, 20).isValid()); // minLon > maxLon
        assertFalse(new BBox(30, 40, 20, 20).isValid()); // minLat == maxLat
        assertFalse(new BBox(30, 40, 25, 20).isValid()); // minLat > maxLat

        // elevation=true and equal elevations => valid
        assertTrue(new BBox(30, 40, 10, 20, 5, 5, true).isValid());

        // elevation=true sentinel extremes => invalid
        assertFalse(new BBox(30, 40, 10, 20, Double.MAX_VALUE, Double.MAX_VALUE, true).isValid());
        assertFalse(new BBox(30, 40, 10, 20, -Double.MAX_VALUE, -Double.MAX_VALUE, true).isValid());

        // elevation ordering inverted => invalid
        assertFalse(new BBox(30, 40, 10, 20, 10, 5, true).isValid());
    }
}
