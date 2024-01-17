package blockchain;

import MeaT.MerkleGraphTree;

import java.security.MessageDigest;
import java.util.ArrayList;

public class Block {
    private ArrayList<Transaction> txs=new ArrayList<>();
    private byte[] hashroot;
    private byte[] id;
    private MerkleGraphTree root;


    public MerkleGraphTree getRoot() {
        return root;
    }

    public void setRoot(MerkleGraphTree root) {
        this.root = root;
    }

    public Block(byte[] id){
        this.id=id;
    }

    public ArrayList<Transaction> getTxs() {
        return txs;
    }

    public void setTxs(ArrayList<Transaction> txs) {
        this.txs = txs;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getHashroot() {
        return hashroot;
    }

    public void setHashroot(byte[] hashroot) {
        this.hashroot = hashroot;
    }
}
