package blockchain;

import java.security.MessageDigest;
import java.util.ArrayList;

public class Block {
    private ArrayList<Transaction> txs=new ArrayList<>();
    private MessageDigest hashroot;
    private byte[] id;

    public Block(byte[] id){
        this.id=id;
    }

    public ArrayList<Transaction> getTxs() {
        return txs;
    }

    public void setTxs(ArrayList<Transaction> txs) {
        this.txs = txs;
    }

    public MessageDigest getHashroot() {
        return hashroot;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }
}
