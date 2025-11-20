package com.graphhopper.resources;

import com.graphhopper.storage.StorableProperties;
import com.graphhopper.resources.RouteResource;
import com.graphhopper.util.*;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import com.graphhopper.http.GHRequestTransformer;
import com.graphhopper.http.ProfileResolver;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteResourceTest {

    @Mock PMap hints;
    @Mock GraphHopperConfig ghConfig;
    @Mock GraphHopper graphHopper;
    @Mock ProfileResolver profileResolver;
    @Mock GHRequestTransformer ghRequestTransformer;
    @Mock StorableProperties storableProperties;

    List<String> expectedSnapPreventions;
    String expectedDate;
    Map<String, String> mockProperties;


    @BeforeEach
    public void setup() {
        expectedSnapPreventions = java.util.Arrays.asList("example1", "example2", "example3");
        expectedDate = "yyyy-mm-dd";

        mockProperties = new HashMap<>();        
        mockProperties.put("datareader.data.date", expectedDate);

        lenient().when(graphHopper.getProperties()).thenReturn(storableProperties);
        lenient().when(storableProperties.getAll()).thenReturn(mockProperties);
    }


    @Test
    public void constructorInitializesFieldsCorrectly() {

        when(ghConfig.getString("routing.snap_preventions_default", "")).thenReturn("example1,example2,example3");
        
        RouteResource routeResource = new RouteResource(
            ghConfig, 
            graphHopper, 
            profileResolver, 
            ghRequestTransformer, 
            true
        );

        assertEquals(expectedDate, routeResource.getOsmDate());
        assertEquals(
            expectedSnapPreventions, 
            routeResource.getSnapPreventionsDefault()
        );
    }

    @Test
    public void constructorHandlesDirtyEntriesWhenFillingSnapPreventionsDefaultField() {

        when(ghConfig.getString("routing.snap_preventions_default", "")).thenReturn("example1, , , example2,example3 ,");
        
        RouteResource routeResource = new RouteResource(
            ghConfig, 
            graphHopper, 
            profileResolver, 
            ghRequestTransformer, 
            true
        );

        assertEquals(
            expectedSnapPreventions, 
            routeResource.getSnapPreventionsDefault()
        );
    }

    @Test
    public void verifyKeysAreSuccessfullyRemovedWithRemoveLegacyParameters() {
        RouteResource.removeLegacyParameters(hints);

        verify(hints).remove("weighting");
        verify(hints).remove("vehicle");
        verify(hints).remove("edge_based");
        verify(hints).remove("turn_costs");
    }
}
