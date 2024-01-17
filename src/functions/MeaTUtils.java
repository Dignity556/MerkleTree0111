package functions;

import blockchain.Transaction;
import functions.TxUtils;
import graph.Edge;
import graph.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class MeaTUtils {
    //Filter the edges with the same start_node into the same merkle graph tree
    public static HashMap<Node,ArrayList<Edge>> start_node_filter(ArrayList<Transaction> txs){
        TxUtils txu=new TxUtils();
        ArrayList<Edge> edges=new ArrayList<Edge>();
        //Transmit all the transactions into edges
        for (Transaction tx:txs)
        {
            edges.add(txu.txs_to_edges(tx));
        }
        //Filter the edges into different arraylists by the start node
        HashMap<Node,ArrayList<Edge>> edge_map=new HashMap<>();
        for(Edge e: edges)
        {
            if (edge_map.containsKey(e.getStart_node())){
                edge_map.get(e.getStart_node()).add(e);
            }else {
                ArrayList<Edge> part_edge=new ArrayList<Edge>();
                part_edge.add(e);
                edge_map.put(e.getStart_node(),part_edge);
            }
        }
        return edge_map;
    }


}
