package merkletree;

import blockchain.Block;
import blockchain.Transaction;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

// Each transaction is transformed into a leaf of Merkle Tree;
// The leaf is the SHA256 value of the transaction;
public class Leaf
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

    private Transaction transaction;
    private byte[] hash_id;
    private Leaf father;
    private Leaf left_son;
    private Leaf right_son;


    public Leaf(Transaction transaction) throws NoSuchAlgorithmException {
        this.transaction=transaction;
        hash_id=calculateSHA256(transaction.getId().toString());
    }

    public Leaf(){

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

    public Leaf getFather() {
        return father;
    }

    public void setFather(Leaf father) {
        this.father = father;
    }

    public Leaf getLeft_son() {
        return left_son;
    }

    public void setLeft_son(Leaf left_son) {
        this.left_son = left_son;
    }

    public Leaf getRight_son() {
        return right_son;
    }

    public void setRight_son(Leaf right_son) {
        this.right_son = right_son;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public byte[] getHash_id() {
        return hash_id;
    }

    public void setHash_id(byte[] hash_id) {
        this.hash_id = hash_id;
    }
}

