package functions;

import blockchain.Transaction;
import graph.Edge;

public class TxUtils {
    public static Edge txs_to_edges(Transaction tx){
        Edge edge=new Edge(tx.getStart_node(),tx.getEnd_node(),tx.getId(),tx.getTimestamp(),tx.getTime_cost(),tx.getReputation(),tx.getType());
        edge.setUpper_PSFLeaf(tx.getUpper_PSFLeaf());
        edge.setBlock(tx.getBlock());
        edge.setTx(tx);
        tx.setEdge(edge);
        return edge;
    }



}
