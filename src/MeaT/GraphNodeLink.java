package MeaT;

import blockchain.Block;
import graph.Node;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class GraphNodeLink {
    private HashMap<Node, GraphNodeLinkItem> items;


    public GraphNodeLink(){
        items=new HashMap<>();
    }

    public HashMap<Node, GraphNodeLinkItem> getItems() {
        return items;
    }

    public void setItems(HashMap<Node, GraphNodeLinkItem> items) {
        this.items = items;
    }

    public void addLink(MerkleGraphTree mgt){
        //Get the node type to locate its location in the GraphNodeLinkItems
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


    public MerkleGraphTree create_upper_MGT(GraphNodeLink gnl, Block block) throws NoSuchAlgorithmException {
        //直接根据blockid，从每个item中找到merklegraphtree的root
        ArrayList<GraphLeaf> leafNodes=new ArrayList<>();
        for(Node node: items.keySet()){
            if(gnl.getItems().get(node).getMgts().containsKey(block))
            {
                GraphLeaf sub_root=gnl.getItems().get(node).getMgts().get(block).getRoot();
                leafNodes.add(sub_root);
            }
        }
        MerkleGraphTree block_root=MerkleGraphTree.create_Merkletree(leafNodes);
        block.setHashroot(block_root.getHash_value());
        block.setRoot(block_root);
        return block_root;
    }

}
