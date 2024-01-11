package graph;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Edge> edges;
    private ArrayList<Node> nodes;
    public Graph(){

    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
}
