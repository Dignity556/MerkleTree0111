package MeaT;

import graph.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphNodeLink {
    private HashMap<Node, GraphNodeLinkItem> items;

    public GraphNodeLink(){
        items=new HashMap<>();
    }

    public void addLink(MerkleGraphTree mgt){
        //Get the hash value and the node type to locate its location in the Maplink
        byte[] hash_value=mgt.getHash_value();
        Node start_node=mgt.getSubtree();
        if(!items.containsKey(start_node))
        {
            GraphNodeLinkItem new_item=new GraphNodeLinkItem(start_node);
            new_item.addMGTs(mgt);
            items.put(start_node,new_item);
        }else{
            GraphNodeLinkItem exist_item=items.get(start_node);
            exist_item.addMGTs(mgt);
        }
    }

}
