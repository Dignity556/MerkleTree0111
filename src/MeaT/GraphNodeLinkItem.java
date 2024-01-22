package MeaT;

import blockchain.Block;
import graph.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphNodeLinkItem {
    private Node pre_node;
    private String id;
    private HashMap<String,MerkleGraphTree> mgts;

    public GraphNodeLinkItem(){
        this.mgts=new HashMap<String,MerkleGraphTree>();
    }

    public GraphNodeLinkItem(Node node)
    {
        this.pre_node=node;
        this.mgts=new HashMap<String,MerkleGraphTree>();
    }

    public Node getPre_node() {
        return pre_node;
    }

    public void setPre_node(Node pre_node) {
        this.pre_node = pre_node;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, MerkleGraphTree> getMgts() {
        return mgts;
    }

    public void setMgts(HashMap<String, MerkleGraphTree> mgts) {
        this.mgts = mgts;
    }

    public void addMGTs(MerkleGraphTree mgt)
    {
        if (!mgts.containsKey(mgt.getBlock().getId()))
        {
            mgts.put(mgt.getBlock().getId(), mgt);
        }
    }



}
