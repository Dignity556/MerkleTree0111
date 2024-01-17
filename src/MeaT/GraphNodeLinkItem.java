package MeaT;

import blockchain.Block;
import graph.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphNodeLinkItem {
    private Node pre_node;
    private HashMap<Block,MerkleGraphTree> mgts;

    public GraphNodeLinkItem(){
        this.mgts=new HashMap<Block,MerkleGraphTree>();
    }

    public GraphNodeLinkItem(Node node)
    {
        this.pre_node=node;
        this.mgts=new HashMap<Block,MerkleGraphTree>();
    }

    public Node getPre_node() {
        return pre_node;
    }

    public void setPre_node(Node pre_node) {
        this.pre_node = pre_node;
    }

    public HashMap<Block, MerkleGraphTree> getMgts() {
        return mgts;
    }

    public void setMgts(HashMap<Block, MerkleGraphTree> mgts) {
        this.mgts = mgts;
    }

    public void addMGTs(MerkleGraphTree mgt)
    {
        if (!mgts.containsKey(mgt.getBlock()))
        {
            mgts.put(mgt.getBlock(), mgt);
        }
    }



}
