package blockchain;

import graph.Node;

public class Transaction {
    private byte[] id;
    private String timestamp;
    private String time_cost;
    private String reputation; //相当于权重
    private Node start_node;
    private Node end_node;


    public Transaction(byte[] id, String timestamp, String time_cost, String reputation, Node start_node, Node end_node) {
        this.id = id;
        this.timestamp = timestamp;
        this.time_cost = time_cost;
        this.reputation = reputation;
        this.start_node = start_node;
        this.end_node = end_node;
    }

    public void setId(byte[] id){
        this.id=id;
    }

    public byte[] getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime_cost() {
        return time_cost;
    }

    public void setTime_cost(String time_cost) {
        this.time_cost = time_cost;
    }

    public String getReputation() {
        return reputation;
    }

    public void setReputation(String reputation) {
        this.reputation = reputation;
    }

    public Node getStart_node() {
        return start_node;
    }

    public void setStart_node(Node start_node) {
        this.start_node = start_node;
    }

    public Node getEnd_node() {
        return end_node;
    }

    public void setEnd_node(Node end_node) {
        this.end_node = end_node;
    }
}