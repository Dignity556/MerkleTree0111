package MeaT;

import blockchain.Transaction;
import graph.Edge;

import java.util.ArrayList;
import java.util.HashMap;

public class PSTLeafNode {
    private Transaction tx;
    private ArrayList<Edge> edges;
    private PSTBranchNodeItem preBranch;
    private ArrayList<Transaction> txs;

    public PSTLeafNode(){
        this.txs=new ArrayList<>();
        this.edges=new ArrayList<>();
    }

    public PSTBranchNodeItem getPreBranch() {
        return preBranch;
    }

    public void setPreBranch(PSTBranchNodeItem preBranch) {
        this.preBranch = preBranch;
    }

    public Transaction getTx() {
        return tx;
    }

    public void setTx(Transaction tx) {
        this.tx = tx;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public void setTX_PSTLeaf(){
        tx.setUpper_PSFLeaf(this);
    }

    public void addTx(Transaction tx)
    {
        txs.add(tx);
    }

    public ArrayList<Transaction> getTxs() {
        return txs;
    }

    public void setTxs(ArrayList<Transaction> txs) {
        this.txs = txs;
    }
}
