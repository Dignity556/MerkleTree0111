package MeaT;

import blockchain.Block;
import blockchain.Transaction;
import graph.Edge;
import graph.Node;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

// Each transaction is transformed into a leaf of Merkle Tree;
// The leaf is the SHA256 value of the transaction;
public class GraphLeaf
{
//    private final List<Transaction> transactions;
//    public Leaf(final List<Transaction> transactions)
//    {
//        this.transactions = transactions;
//    }
//    public List<Transaction> getTransactions()
//    {
//        return (transactions);
//    }

    private Edge edge;
    private byte[] hash_id;
    private GraphLeaf father;
    private GraphLeaf left_son;
    private GraphLeaf right_son;
    private Node subtree_node;


    public GraphLeaf(Edge edge) throws NoSuchAlgorithmException {
        this.edge=edge;
        hash_id=calculateSHA256(edge.getId().toString());
    }

    public GraphLeaf(){

    }

    public static byte[] calculateSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public GraphLeaf getFather() {
        return father;
    }

    public void setFather(GraphLeaf father) {
        this.father = father;
    }

    public GraphLeaf getLeft_son() {
        return left_son;
    }

    public void setLeft_son(GraphLeaf left_son) {
        this.left_son = left_son;
    }

    public GraphLeaf getRight_son() {
        return right_son;
    }

    public void setRight_son(GraphLeaf right_son) {
        this.right_son = right_son;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public byte[] getHash_id() {
        return hash_id;
    }

    public void setHash_id(byte[] hash_id) {
        this.hash_id = hash_id;
    }

    public Node getSubtree_node() {
        return subtree_node;
    }

    public void setSubtree_node(Node subtree_node) {
        this.subtree_node = subtree_node;
    }
}

