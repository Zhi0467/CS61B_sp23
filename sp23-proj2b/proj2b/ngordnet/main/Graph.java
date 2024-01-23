package ngordnet.main;

import java.util.*;

// create a directed graph API using adjacency list which is a hashmap from Integer to a list of Integers
// with methods: addVertex, addEdge, traversal, etc.
public class Graph {
    private Map<Integer, List<Integer>> adj;
    public Graph() {
        adj =  new HashMap<>();
    }

    public void addVertex(int v) {
        if (!adj.containsKey(v)) {
            adj.put(v, new ArrayList<>());
        }
    }
    public void addEdge(int parent, int child) {
        if (!adj.containsKey(parent) || !adj.containsKey(child)) {
            throw new IllegalArgumentException("Vertex not in graph");
        }
        List<Integer> children = adj.get(parent);
        children.add(child);
        adj.put(parent, children);
    }

    public List<Integer> getChildren(int v) {
        if (!adj.containsKey(v)) {
            throw new IllegalArgumentException("Vertex not in graph");
        }
        return adj.get(v);
    }

    public int sizeSubtree(int v) {
        if (getChildren(v).size() == 0) {
            return 1;
        }
        int size = 1;
        for (int child : getChildren(v)) {
            size += sizeSubtree(child);
        }
        return size;
    }

    public Set<Integer> dfs(int start) {
        // Create a stack for DFS
        Stack<Integer> stack = new Stack<Integer>();

        Set<Integer> hyponyms = new HashSet<>();
        // Mark the starting node as visited and push it onto the stack
        int visited = 1;
        stack.push(start);

        while (!stack.empty()) {
            // Pop a vertex from the stack and print it
            start = stack.pop();
            hyponyms.add(start);
            // Get all adjacent vertices of the popped vertex
            // If an adjacent vertex has not been visited, then mark it
            // visited and push it onto the stack
            List<Integer> children = this.getChildren(start);
            for (int i = 0; i < children.size(); i++) {
                stack.push(children.get(i));
                visited++;
            }
        }
        return hyponyms;
    }


}
