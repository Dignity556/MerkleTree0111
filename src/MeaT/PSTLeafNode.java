package MeaT;

import blockchain.Transaction;
import graph.Edge;

import java.util.ArrayList;

public class PSTLeafNode {
    private Transaction tx;
    private Edge edge;
    private PSTBranchNodeItem preBranch;
    private ArrayList<Transaction> txs;

    public PSTLeafNode(){
        this.txs=new ArrayList<>();
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

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public void setTX_PSTLeaf(){
        tx.setUpper_PSFLeaf(this);
    }

    public void addTx(Transaction tx)
    {
        txs.add(tx);
    }


}
