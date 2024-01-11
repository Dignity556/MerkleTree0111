package graph;

public class Node {
    private String node_id;
    private String attribute;

    public Node(String node_id, String attribute) {
        this.node_id = node_id;
        this.attribute = attribute;
    }
    public Node(String node_id){
        this.node_id=node_id;
    }

    public String getNode_id() {
        return node_id;

    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}