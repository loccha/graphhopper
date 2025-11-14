package com.graphhopper.routing;

import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.PMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.graphhopper.util.Parameters.Algorithms.ASTAR_BI;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutingAlgorithmFactorySimpleTest {

    @Mock Graph graph;
    @Mock Weighting weighting;
    @Mock AlgorithmOptions opts;
    @Mock NodeAccess nodeAccess;

    @Test
    void createsAStarBidirection_whenAlgoIsAStarBi() {
        when(opts.getAlgorithm()).thenReturn(ASTAR_BI);
        when(opts.getTraversalMode()).thenReturn(TraversalMode.NODE_BASED);
        when(opts.getHints()).thenReturn(new PMap());
        when(opts.getMaxVisitedNodes()).thenReturn(12_345);
        when(opts.getTimeoutMillis()).thenReturn(7_000L);

        when(graph.wrapWeighting(weighting)).thenReturn(weighting);
        when(graph.getNodeAccess()).thenReturn(nodeAccess);

        RoutingAlgorithmFactorySimple fac = new RoutingAlgorithmFactorySimple();
        RoutingAlgorithm algo = fac.createAlgo(graph, weighting, opts);

        assertNotNull(algo);
        assertTrue(algo instanceof AStarBidirection);

        verify(graph).wrapWeighting(weighting);
        verify(graph, atLeastOnce()).getNodeAccess();
        verify(opts).getAlgorithm();
        verify(opts).getTraversalMode();
        verify(opts).getHints();
        verify(opts).getMaxVisitedNodes();
        verify(opts).getTimeoutMillis();
    }

    @Test
    void throwsForUnknownAlgorithm() {
        when(opts.getAlgorithm()).thenReturn("totally-unknown");
        assertThrows(IllegalArgumentException.class,
                () -> new RoutingAlgorithmFactorySimple().createAlgo(graph, weighting, opts));
    }
}