package MeaT;

import graph.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphNodeLink {
    private HashMap<Node, ArrayList<byte[]>> graphnodelink;

    public GraphNodeLink(){
        graphnodelink=new HashMap<>();
    }

//    public void addLink(MerkleGraphTree mgt){
//        //Get the hash value and the node type to locate its location in the Maplink
//        byte[] hash_value=mgt.digest();
//        Node start_node=mgt.leftLeaf().getEdges().get(0).getStart_node();
//        if(!graphnodelink.containsKey(start_node))
//        {
//            ArrayList<byte[]> block_hashs=new ArrayList<>();
//            block_hashs.add(hash_value);
//            graphnodelink.put(start_node,block_hashs);
//        }else{
//            ArrayList<byte[]> hashs=graphnodelink.get(start_node);
//            hashs.add(hash_value);
//        }
//    }

}
