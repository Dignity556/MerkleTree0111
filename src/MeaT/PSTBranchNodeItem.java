package MeaT;

import blockchain.Transaction;

import java.util.ArrayList;

public class PSTBranchNodeItem {
    private String property_item;
    private ArrayList<Transaction> pre_txs;
    private PSTLeafNode next_leaf;
    private PSTExtensionNode next_extension;
    private String id;

    public PSTBranchNodeItem(){
        this.pre_txs=new ArrayList<>();
    }

    public String getProperty_item() {
        return property_item;
    }

    public void setProperty_item(String property_item) {
        this.property_item = property_item;
    }

    public ArrayList<Transaction> getPre_txs() {
        return pre_txs;
    }

    public void setPre_txs(ArrayList<Transaction> pre_txs) {
        this.pre_txs = pre_txs;
    }

    public PSTLeafNode getNext_leaf() {
        return next_leaf;
    }

    public void setNext_leaf(PSTLeafNode next_leaf) {
        this.next_leaf = next_leaf;
    }

    public PSTExtensionNode getNext_extension() {
        return next_extension;
    }

    public void setNext_extension(PSTExtensionNode next_extension) {
        this.next_extension = next_extension;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
