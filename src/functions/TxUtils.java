package functions;

import blockchain.Block;
import blockchain.Transaction;
import graph.Edge;
import graph.Node;

import java.util.ArrayList;

public class TxUtils {
    public static Edge txs_to_edges(Transaction tx){
        Edge edge=new Edge(tx.getStart_node(),tx.getEnd_node(),tx.getId(),tx.getTimestamp(),tx.getTime_cost(),tx.getReputation(),tx.getType());
        edge.setUpper_PSFLeaf(tx.getUpper_PSFLeaf());
        edge.setBlock(tx.getBlock());
        edge.setTx(tx);
        tx.setEdge(edge);
        return edge;
    }

    public Node query_node(ArrayList<Node> nodes, String id){
        for(Node node:nodes)
        {
            if (node.getNode_id().equals(id))
            {
                return node;
            }
        }
        return null;
    }

    public Block query_block(ArrayList<Block> blocks, String id){
        for(Block block:blocks)
        {
            if (block.getId().equals(id))
            {
                return block;
            }
        }
        return null;
    }



}
