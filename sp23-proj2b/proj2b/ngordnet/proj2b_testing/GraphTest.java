package ngordnet.proj2b_testing;

import ngordnet.main.Graph;
import org.junit.jupiter.api.Test;
import static com.google.common.truth.Truth.assertThat;

public class GraphTest {
    // test the Graph.java class using Google Truth
    @Test
    public void testGraph() {
        Graph g = new Graph();
        for (int i = 1; i <= 11; i++) {
            g.addVertex(i);
        }
        g.addEdge(1, 3);
        g.addEdge(4, 2);
        g.addEdge(5, 2);
        g.addEdge(3, 6);
        g.addEdge(3, 7);
        g.addEdge(6, 8);
        g.addEdge(6, 9);
        g.addEdge(6, 10);
        g.addEdge(7, 11);

        assertThat(g.dfs(1)).containsExactly(1, 3, 6, 7, 8, 9, 10, 11);
        assertThat(g.dfs(4)).containsExactly(4, 2);
        assertThat(g.dfs(5)).containsExactly(5, 2);
        assertThat(g.dfs(6)).containsExactly(6, 8, 9, 10);
        assertThat(g.dfs(7)).containsExactly(7, 11);
        assertThat(g.dfs(8)).containsExactly(8);
        assertThat(g.dfs(9)).containsExactly(9);
    }
}
