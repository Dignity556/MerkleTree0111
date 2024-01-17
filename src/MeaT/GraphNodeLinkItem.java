package MeaT;

import graph.Node;

import java.util.ArrayList;

public class GraphNodeLinkItem {
    private Node pre_node;
    private ArrayList<MerkleGraphTree> mgts;

    public GraphNodeLinkItem(){
        this.mgts=new ArrayList<>();
    }

    public GraphNodeLinkItem(Node node)
    {
        this.pre_node=node;
        this.mgts=new ArrayList<>();
    }

    public Node getPre_node() {
        return pre_node;
    }

    public void setPre_node(Node pre_node) {
        this.pre_node = pre_node;
    }

    public ArrayList<MerkleGraphTree> getMgts() {
        return mgts;
    }

    public void setMgts(ArrayList<MerkleGraphTree> mgts) {
        this.mgts = mgts;
    }

    public void addMGTs(MerkleGraphTree mgt)
    {
        mgts.add(mgt);
    }

}
