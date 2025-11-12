package com.graphhopper.routing;

import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.NodeAccess;
import com.graphhopper.util.PMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.graphhopper.util.Parameters.Algorithms.ASTAR_BI;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoutingAlgorithmFactorySimpleTest {

    @Mock Graph graph;           // collaborator #1
    @Mock Weighting weighting;   // collaborator #2
    @Mock AlgorithmOptions opts;
    @Mock NodeAccess nodeAccess;

    @Test
    public void createsAStarBidirection_whenAlgoIsAStarBi() {
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
        verify(graph).getNodeAccess();
        verify(opts).getAlgorithm();
        verify(opts).getTraversalMode();
        verify(opts).getHints();
        verify(opts).getMaxVisitedNodes();
        verify(opts).getTimeoutMillis();
        verifyNoMoreInteractions(graph, weighting, opts, nodeAccess);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsForUnknownAlgorithm() {
        when(opts.getAlgorithm()).thenReturn("totally-unknown");
        new RoutingAlgorithmFactorySimple().createAlgo(graph, weighting, opts);
    }
}
