package graph;

import MeaT.GraphNodeLinkItem;
import MeaT.MerkleGraphTree;
import MeaT.PSTLeafNode;
import blockchain.Block;
import blockchain.Transaction;

import java.util.HashMap;

public class Edge {
    private Node start_node;
    private Node end_node;
    private byte[] id;
    private String timestamp;
    private String time_cost;
    private String type;
    private String reputation; //相当于权重
    private Block block;
    private Transaction tx;
    private HashMap<GraphNodeLinkItem, MerkleGraphTree> gnlItem; //每条边在graphnodelink的哪个数据项中
    private PSTLeafNode upper_PSFLeaf;//对应到property semantic trie中的leafnode



    public Edge(Node start_node, Node end_node, byte[] id, String timestamp, String time_cost, String reputation,String type) {
        this.start_node = start_node;
        this.end_node = end_node;
        this.id = id;
        this.timestamp = timestamp;
        this.time_cost = time_cost;
        this.reputation = reputation;
        this.type=type;
        this.gnlItem=new HashMap<>();
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

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PSTLeafNode getUpper_PSFLeaf() {
        return upper_PSFLeaf;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setUpper_PSFLeaf(PSTLeafNode upper_PSFLeaf) {
        this.upper_PSFLeaf = upper_PSFLeaf;
    }

    public HashMap<GraphNodeLinkItem, MerkleGraphTree> getGnlItem() {
        return gnlItem;
    }

    public void setGnlItem(HashMap<GraphNodeLinkItem, MerkleGraphTree> gnlItem) {
        this.gnlItem = gnlItem;
    }

    public Transaction getTx() {
        return tx;
    }

    public void setTx(Transaction tx) {
        this.tx = tx;
    }
}

