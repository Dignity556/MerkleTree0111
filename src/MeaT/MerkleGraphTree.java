package MeaT;

import JDBC.JDBCUtils;
import blockchain.Block;
import blockchain.Transaction;
import functions.TxUtils;
import graph.Edge;
import graph.Node;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a binary Merkle Tree. This consists of two child nodes, and a
 * hash representing those two child nodes. The children can either be leaf nodes
 * that contain data blocks, or can themselves be Merkle Trees.
 */
public class MerkleGraphTree
{
    private byte[] hash_value;
    private GraphLeaf root;
    private Block block;
    private Node subtree;//图树中能用到的，判断子树是哪个节点的交易组成的

    static int father_count=0;

    public byte[] getHash_value() {
        return hash_value;
    }

    public void setHash_value(byte[] hash_value) {
        this.hash_value = hash_value;
    }

    public GraphLeaf getRoot() {
        return root;
    }

    public void setRoot(GraphLeaf root) {
        this.root = root;
    }

    public Node getSubtree() {
        return subtree;
    }

    public void setSubtree(Node subtree) {
        this.subtree = subtree;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public static MerkleGraphTree create_Merkletree(ArrayList<GraphLeaf> leaves) throws NoSuchAlgorithmException, SQLException {
        ArrayList<GraphLeaf> new_leaves=new ArrayList<>();
        int count=0;//记录树中节点的总个数
        if(leaves.size()==1)
        {
            MerkleGraphTree mt=new MerkleGraphTree();
            leaves.get(0).setFather(null);
            leaves.get(0).setId("r_"+leaves.get(0).getSubtree_node().getNode_id()+"_"+leaves.get(0).getBlock().getId());
            Connection conn=new JDBCUtils().connect_database();
            String sql = "insert into merklegraphtree (hash_value,left_child,right_child,id,is_lower_root) value (?,?,?,?,?)";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1,leaves.get(0).getHash_id().toString());
            if(leaves.get(0).getLeft_son()!=null)
            {
                ps.setString(2,leaves.get(0).getLeft_son().getId());
            }else{
                ps.setString(2,"null");
            }
            if(leaves.get(0).getRight_son()!=null)
            {
                ps.setString(3,leaves.get(0).getRight_son().getId());
            }else{
                ps.setString(3,"null");
            }
            ps.setString(4,leaves.get(0).getId());
            ps.setInt(5,1);
            ps.executeUpdate();
            try {
                if (null != ps) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mt.setRoot(leaves.get(0));
            mt.setBlock(leaves.get(0).getBlock());
            mt.setHash_value(leaves.get(0).getHash_id());
            mt.setSubtree(leaves.get(0).getSubtree_node());
            return mt;
        }else{
            for(int i=0;i<leaves.size()-1;i+=2)
            {
                GraphLeaf father=new GraphLeaf();
                father_count+=1;
                father.setLeft_son(leaves.get(i));
                father.setRight_son(leaves.get(i+1));
                father.setSubtree_node(leaves.get(i).getSubtree_node());
                father.setHash_id(GraphLeaf.calculateSHA256(leaves.get(i).getHash_id().toString()+leaves.get(i+1).getHash_id().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("f"+father_count+"_"+father.getSubtree_node().getNode_id()+"_"+ father.getBlock().getId());
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                new_leaves.add(father);
                count+=1;
                Connection conn=new JDBCUtils().connect_database();
                String sql = "insert into merklegraphtree (hash_value,left_child,right_child,id) value (?,?,?,?)";
                PreparedStatement ps=conn.prepareStatement(sql);
                ps.setString(1,leaves.get(i).getHash_id().toString());
                if(leaves.get(i).getLeft_son()!=null)
                {
                    ps.setString(2,leaves.get(i).getLeft_son().getId());
                }else{
                    ps.setString(2,"null");
                }
                if(leaves.get(i).getRight_son()!=null)
                {
                    ps.setString(3,leaves.get(i).getRight_son().getId());
                }else{
                    ps.setString(3,"null");
                }
                ps.setString(4,leaves.get(i).getId());
                ps.executeUpdate();
                try {
                    if (null != ps) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                PreparedStatement ps2=conn.prepareStatement(sql);
                ps2.setString(1,leaves.get(i).getHash_id().toString());
                if(leaves.get(i).getLeft_son()!=null)
                {
                    ps2.setString(2,leaves.get(i).getLeft_son().getId());
                }else{
                    ps2.setString(2,"null");
                }
                if(leaves.get(i).getRight_son()!=null)
                {
                    ps2.setString(3,leaves.get(i).getRight_son().getId());
                }else{
                    ps2.setString(3,"null");
                }
                ps2.setString(4,leaves.get(i).getId());
                ps2.executeUpdate();
                try {
                    if (null != ps2) {
                        ps2.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size()%2==1)
            {
                GraphLeaf new_leaf=leaves.get(leaves.size()-1);
                String label=new_leaf.getSubtree_node().getNode_id();
                new_leaf.setId("l");
                new_leaves.add(new_leaf);
                count+=1;
                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
            MerkleGraphTree mt=create_Merkletree(new_leaves);
            return mt;
        }

    }

    //创建上层mgt
    public static MerkleGraphTree create_upper_Merkletree(ArrayList<GraphLeaf> leaves) throws NoSuchAlgorithmException, SQLException {
        ArrayList<GraphLeaf> new_leaves=new ArrayList<>();
        for (GraphLeaf leaf:leaves)
        {
            System.out.println(leaf.getId());
        }
        int count=0;//记录树中节点的总个数
        if(leaves.size()==1)
        {
            MerkleGraphTree mt=new MerkleGraphTree();
            leaves.get(0).setFather(null);
            leaves.get(0).setId("u_r"+leaves.get(0).getSubtree_node().getNode_id()+"_"+leaves.get(0).getBlock().getId());
            Connection conn=new JDBCUtils().connect_database();
            String sql = "insert into merklegraphtree (hash_value,left_child,right_child,id,is_upper_root) value (?,?,?,?,?)";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1,leaves.get(0).getHash_id().toString());
            if(leaves.get(0).getLeft_son()!=null)
            {
                ps.setString(2,leaves.get(0).getLeft_son().getId());
            }else{
                ps.setString(2,"null");
            }
            if(leaves.get(0).getRight_son()!=null)
            {
                ps.setString(3,leaves.get(0).getRight_son().getId());
            }else{
                ps.setString(3,"null");
            }
            ps.setString(4,leaves.get(0).getId());
            ps.setInt(5,1);
            ps.executeUpdate();
            try {
                if (null != ps) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            mt.setRoot(leaves.get(0));
            mt.setBlock(leaves.get(0).getBlock());
            mt.setHash_value(leaves.get(0).getHash_id());
            mt.setSubtree(leaves.get(0).getSubtree_node());
            return mt;
        }else{
            for(int i=0;i<leaves.size()-1;i+=2)
            {
                GraphLeaf father=new GraphLeaf();
                father_count+=1;
                father.setLeft_son(leaves.get(i));
                father.setRight_son(leaves.get(i+1));
                father.setSubtree_node(leaves.get(i).getSubtree_node());
                father.setHash_id(GraphLeaf.calculateSHA256(leaves.get(i).getHash_id().toString()+leaves.get(i+1).getHash_id().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("uf"+father_count+"_"+father.getSubtree_node().getNode_id()+"_"+ father.getBlock().getId());
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                new_leaves.add(father);
                count+=1;
                Connection conn=new JDBCUtils().connect_database();
                String sql = "insert into merklegraphtree (hash_value,left_child,right_child,id) value (?,?,?,?)";
                PreparedStatement ps=conn.prepareStatement(sql);
                ps.setString(1,leaves.get(i).getHash_id().toString());
                if(leaves.get(i).getLeft_son()!=null)
                {
                    ps.setString(2,leaves.get(i).getLeft_son().getId());
                }else{
                    ps.setString(2,"null");
                }
                if(leaves.get(i).getRight_son()!=null)
                {
                    ps.setString(3,leaves.get(i).getRight_son().getId());
                }else{
                    ps.setString(3,"null");
                }
                ps.setString(4,leaves.get(i).getId());
                ps.executeUpdate();
                try {
                    if (null != ps) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                PreparedStatement ps2=conn.prepareStatement(sql);
                ps2.setString(1,leaves.get(i).getHash_id().toString());
                if(leaves.get(i).getLeft_son()!=null)
                {
                    ps2.setString(2,leaves.get(i).getLeft_son().getId());
                }else{
                    ps2.setString(2,"null");
                }
                if(leaves.get(i).getRight_son()!=null)
                {
                    ps2.setString(3,leaves.get(i).getRight_son().getId());
                }else{
                    ps2.setString(3,"null");
                }
                ps2.setString(4,leaves.get(i).getId());
                ps2.executeUpdate();
                try {
                    if (null != ps2) {
                        ps2.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size()%2==1)
            {
                GraphLeaf new_leaf=leaves.get(leaves.size()-1);
                String label=new_leaf.getSubtree_node().getNode_id();
                new_leaf.setId("l");
                new_leaves.add(new_leaf);
                count+=1;
                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
            MerkleGraphTree mt=create_upper_Merkletree(new_leaves);
            return mt;
        }

    }

    public static MerkleGraphTree java_create_Merkletree(ArrayList<GraphLeaf> leaves) throws NoSuchAlgorithmException, SQLException {
        ArrayList<GraphLeaf> new_leaves=new ArrayList<>();
        int count=0;//记录树中节点的总个数
        if(leaves.size()==1)
        {
            MerkleGraphTree mt=new MerkleGraphTree();
            leaves.get(0).setFather(null);
            leaves.get(0).setId("r");
            mt.setRoot(leaves.get(0));
            mt.setBlock(leaves.get(0).getBlock());
            mt.setHash_value(leaves.get(0).getHash_id());
            mt.setSubtree(leaves.get(0).getSubtree_node());
            return mt;
        }else{
            for(int i=0;i<leaves.size()-1;i+=2)
            {
                GraphLeaf father=new GraphLeaf();
                father.setLeft_son(leaves.get(i));
                father.setRight_son(leaves.get(i+1));
                father.setSubtree_node(leaves.get(i).getSubtree_node());
                father.setHash_id(GraphLeaf.calculateSHA256(leaves.get(i).getHash_id().toString()+leaves.get(i+1).getHash_id().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("f");
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                new_leaves.add(father);
                count+=1;
                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size()%2==1)
            {
                GraphLeaf new_leaf=leaves.get(leaves.size()-1);
                String label=new_leaf.getSubtree_node().getNode_id();
                new_leaf.setId("l");
                new_leaves.add(new_leaf);
                count+=1;
                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
            MerkleGraphTree mt=create_Merkletree(new_leaves);
            return mt;
        }

    }
    public static MerkleGraphTree java_create_upper_Merkletree(ArrayList<GraphLeaf> leaves) throws NoSuchAlgorithmException, SQLException {
        ArrayList<GraphLeaf> new_leaves=new ArrayList<>();
        for (GraphLeaf leaf:leaves)
        {
            System.out.println(leaf.getId());
        }
        int count=0;//记录树中节点的总个数
        if(leaves.size()==1)
        {
            MerkleGraphTree mt=new MerkleGraphTree();
            leaves.get(0).setFather(null);
            leaves.get(0).setId("u_r");
            mt.setRoot(leaves.get(0));
            mt.setBlock(leaves.get(0).getBlock());
            mt.setHash_value(leaves.get(0).getHash_id());
            mt.setSubtree(leaves.get(0).getSubtree_node());
            return mt;
        }else{
            for(int i=0;i<leaves.size()-1;i+=2)
            {
                GraphLeaf father=new GraphLeaf();
                father.setLeft_son(leaves.get(i));
                father.setRight_son(leaves.get(i+1));
                father.setSubtree_node(leaves.get(i).getSubtree_node());
                father.setHash_id(GraphLeaf.calculateSHA256(leaves.get(i).getHash_id().toString()+leaves.get(i+1).getHash_id().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("f");
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                new_leaves.add(father);
                count+=1;
                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size()%2==1)
            {
                GraphLeaf new_leaf=leaves.get(leaves.size()-1);
                String label=new_leaf.getSubtree_node().getNode_id();
                new_leaf.setId("l");
                new_leaves.add(new_leaf);
                count+=1;
                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
            MerkleGraphTree mt=create_upper_Merkletree(new_leaves);
            return mt;
        }

    }

    //查询指定的交易
    public void query_MerkleTree(Transaction tx){
        Block block=tx.getBlock();
        MerkleGraphTree mgt=block.getRoot();

    }

    public Transaction containsTransaction(byte[] Hash, GraphLeaf root) {
        // 递归终止条件：如果当前节点为叶子节点，并且哈希值与目标交易的哈希值相等，则返回 true
        if (root.getLeft_son()==null && root.getHash_id().equals(Hash)) {
            Edge edge=root.getEdge();
            return edge.getTx();
        }

        // 如果当前节点为叶子节点但哈希值不匹配，或者当前节点为非叶子节点，则继续遍历子节点
        if (root.getLeft_son()!=null) {
            // 递归遍历左子节点
            Transaction tx1=containsTransaction(Hash, root.getLeft_son());
            return tx1;

        }
        // 递归遍历右子节点（如果存在）
        if (root.getRight_son()!=null) {
            Transaction tx2=containsTransaction(Hash, root.getRight_son());
            return  tx2;
        }
        // 如果在当前节点及其子节点中都未找到目标交易，则返回 false
        return null;
    }

    public Transaction containsTx_by_id(String id, GraphLeaf graphLeaf) {
        // 递归终止条件：如果当前节点为叶子节点，并且哈希值与目标交易的哈希值相等，则返回 true
        if (graphLeaf.getLeft_son()==null && graphLeaf.getEdge().getTx().getId().equals(id)) {
            Edge edge=graphLeaf.getEdge();
            return edge.getTx();
        }

        // 如果当前节点为叶子节点但哈希值不匹配，或者当前节点为非叶子节点，则继续遍历子节点
        if (graphLeaf.getLeft_son()!=null) {
            // 递归遍历左子节点
            Transaction tx1=containsTx_by_id(id, graphLeaf.getLeft_son());
            return tx1;

        }
        // 递归遍历右子节点（如果存在）
        if (graphLeaf.getRight_son()!=null) {
            Transaction tx2=containsTx_by_id(id, graphLeaf.getRight_son());
            return  tx2;
        }
        // 如果在当前节点及其子节点中都未找到目标交易，则返回 false
        return null;
    }

    public ArrayList<Transaction> iterateTxs_by_id(String node_id, GraphLeaf graphLeaf, ArrayList<Transaction> txs)
    {
        // 递归终止条件：如果当前节点为叶子节点，并且哈希值与目标交易的哈希值相等，则返回 true
        if (graphLeaf.getLeft_son()==null && graphLeaf.getEdge().getStart_node().getNode_id().equals(node_id)) {
            txs.add(graphLeaf.getEdge().getTx());
        }
        // 如果当前节点为叶子节点但哈希值不匹配，或者当前节点为非叶子节点，则继续遍历子节点
        if (graphLeaf.getLeft_son()!=null) {
            // 递归遍历左子节点
            txs=iterateTxs_by_id(node_id, graphLeaf.getLeft_son(),txs);
        }
        // 递归遍历右子节点（如果存在）
        if (graphLeaf.getRight_son()!=null) {
            txs=iterateTxs_by_id(node_id, graphLeaf.getRight_son(),txs);
        }
        // 如果在当前节点及其子节点中都未找到目标交易，则返回 false
        return txs;
    }

}
