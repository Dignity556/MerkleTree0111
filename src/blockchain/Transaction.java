package blockchain;

import MeaT.PSTLeafNode;
import graph.Node;

public class Transaction {
    private byte[] id;
    private String timestamp;
    private String time_cost;
    private String reputation; //相当于权重
    private Node start_node;
    private Node end_node;
    private String type;
    private Block block;
    private PSTLeafNode upper_PSFLeaf;//对应到property semantic trie中的leafnode

    public Transaction(byte[] id, String timestamp, String time_cost, String reputation, Node start_node, Node end_node, String type) {
        this.id = id;
        this.timestamp = timestamp;
        this.time_cost = time_cost;
        this.reputation = reputation;
        this.start_node = start_node;
        this.end_node = end_node;
        this.type=type;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PSTLeafNode getUpper_PSFLeaf() {
        return upper_PSFLeaf;
    }

    public void setUpper_PSFLeaf(PSTLeafNode upper_PSFLeaf) {
        this.upper_PSFLeaf = upper_PSFLeaf;
    }
}
