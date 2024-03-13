package merkletree;

import JDBC.JDBCUtils;
import MeaT.GraphLeaf;
import MeaT.MerkleGraphTree;
import blockchain.Block;
import blockchain.Transaction;
import graph.Edge;

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
public class MerkleTree
{
    private byte[] hash_value;
    private Leaf root;
    private Block block;
    //private Node subtree;//图树中能用到的，判断子树是哪个节点的交易组成的
    static int father_count=0;//记录father节点总数；

    public byte[] getHash_value() {
        return hash_value;
    }

    public void setHash_value(byte[] hash_value) {
        this.hash_value = hash_value;
    }

    public Leaf getRoot() {
        return root;
    }

    public void setRoot(Leaf root) {
        this.root = root;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public static MerkleTree create_Merkletree(ArrayList<Leaf> leaves) throws NoSuchAlgorithmException, SQLException {
        ArrayList<Leaf> new_leaves=new ArrayList<>();
        int count=0;//记录树中节点的总个数
        if(leaves.size()==1)
        {
            MerkleTree mt=new MerkleTree();
            leaves.get(0).setFather(null);
            leaves.get(0).setId("r"+leaves.get(0).getBlock().getId());
            //写入数据库
            Connection conn=new JDBCUtils().connect_database();
            String sql = "insert into merkletree (hash_value,left_child,right_child,id,is_root) value (?,?,?,?,?)";
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
            mt.setBlock(leaves.get(0).getBlock());
            mt.setRoot(leaves.get(0));
            mt.setHash_value(leaves.get(0).getHash_id());
            return mt;
        }else{
            for(int i=0;i<leaves.size()-1;i+=2)
            {
                Leaf father=new Leaf();
                father_count+=1;
                father.setLeft_son(leaves.get(i));
                father.setRight_son(leaves.get(i+1));
                father.setHash_id(Leaf.calculateSHA256(leaves.get(i).getHash_id().toString()+leaves.get(i+1).getHash_id().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("f"+father_count+"_"+leaves.get(0).getBlock().getId());
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                new_leaves.add(father);
                count+=1;
                //写入数据库
                Connection conn=new JDBCUtils().connect_database();
                String sql = "insert into merkletree (hash_value,left_child,right_child,id) value (?,?,?,?)";
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
                try {
                    if (null != conn) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size()%2==1)
            {
                Leaf new_leaf=leaves.get(leaves.size()-1);
                new_leaf.setId("l");
                new_leaves.add(new_leaf);
                count+=1;
                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
        }
        MerkleTree mt=create_Merkletree(new_leaves);
        return mt;
    }

    public static MerkleTree create_java_Merkletree(ArrayList<Leaf> leaves) throws NoSuchAlgorithmException, SQLException {
        ArrayList<Leaf> new_leaves=new ArrayList<>();
        int count=0;//记录树中节点的总个数

        if(leaves.size()==1)
        {
            MerkleTree mt=new MerkleTree();
            leaves.get(0).setFather(null);
            leaves.get(0).setId("r"+leaves.get(0).getBlock().getId());
            mt.setBlock(leaves.get(0).getBlock());
            mt.setRoot(leaves.get(0));
            mt.setHash_value(leaves.get(0).getHash_id());
            return mt;
        }else{
            for(int i=0;i<leaves.size()-1;i+=2)
            {
                Leaf father=new Leaf();
                father_count+=1;
                father.setLeft_son(leaves.get(i));
                father.setRight_son(leaves.get(i+1));
                father.setHash_id(Leaf.calculateSHA256(leaves.get(i).getHash_id().toString()+leaves.get(i+1).getHash_id().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("f"+father_count+"_"+leaves.get(0).getBlock().getId());
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                new_leaves.add(father);
                count+=1;
                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size()%2==1)
            {
                Leaf new_leaf=leaves.get(leaves.size()-1);
                new_leaf.setId("l");
                new_leaves.add(new_leaf);
                count+=1;
                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
        }
        MerkleTree mt=create_java_Merkletree(new_leaves);
        return mt;
    }

    public Transaction containsTx_by_id(String id, Leaf leaf) {
        // 递归终止条件：如果当前节点为叶子节点，并且哈希值与目标交易的哈希值相等，则返回 true
        if (leaf.getLeft_son()==null && leaf.getTransaction().getId().equals(id)) {
            return leaf.getTransaction();
        }

        // 如果当前节点为叶子节点但哈希值不匹配，或者当前节点为非叶子节点，则继续遍历子节点
        if (leaf.getLeft_son()!=null) {
            // 递归遍历左子节点
            Transaction tx1=containsTx_by_id(id, leaf.getLeft_son());
            return tx1;

        }
        // 递归遍历右子节点（如果存在）
        if (leaf.getRight_son()!=null) {
            Transaction tx2=containsTx_by_id(id, leaf.getRight_son());
            return  tx2;
        }
        // 如果在当前节点及其子节点中都未找到目标交易，则返回 false
        return null;
    }

    public ArrayList<Transaction> iterateTx_by_node(String node_id,Leaf leaf,ArrayList<Transaction> txs) {
        // 递归终止条件：如果当前节点为叶子节点，并且哈希值与目标交易的哈希值相等，则返回 true
        if (leaf.getLeft_son()==null && leaf.getTransaction().getStart_node().getNode_id().equals(node_id)) {
            txs.add(leaf.getTransaction());
        }

        // 如果当前节点为叶子节点但哈希值不匹配，或者当前节点为非叶子节点，则继续遍历子节点
        if (leaf.getLeft_son()!=null) {
            // 递归遍历左子节点
            txs=iterateTx_by_node(node_id, leaf.getLeft_son(),txs);
        }
        // 递归遍历右子节点（如果存在）
        if (leaf.getRight_son()!=null) {
            txs=iterateTx_by_node(node_id, leaf.getRight_son(),txs);
        }
        // 如果在当前节点及其子节点中都未找到目标交易，则返回 false
        return txs;
    }

    public ArrayList<Transaction> queryTx_by_properties(String type, String[] time_cost, String[] repu,Leaf leaf){
        ArrayList<Transaction> txs=new ArrayList<>();
        double time_cost_max=Double.valueOf(time_cost[1]);
        double time_cost_min=Double.valueOf(time_cost[0]);
        double repu_max=Double.valueOf(repu[1]);
        double repu_min=Double.valueOf(repu[0]);
        // 递归终止条件：如果当前节点为叶子节点，并且哈希值与目标交易的哈希值相等，则返回 true
        if (leaf.getLeft_son()==null && leaf.getTransaction().getType().equals(type) &&
               Double.valueOf(leaf.getTransaction().getReputation())>repu_min && Double.valueOf(leaf.getTransaction().getReputation())<repu_max
                && Double.valueOf(leaf.getTransaction().getTime_cost())>time_cost_min && Double.valueOf(leaf.getTransaction().getTime_cost())<time_cost_max) {
            txs.add(leaf.getTransaction());
        }

        // 如果当前节点为叶子节点但哈希值不匹配，或者当前节点为非叶子节点，则继续遍历子节点
        if (leaf.getLeft_son()!=null) {
            // 递归遍历左子节点
            txs=queryTx_by_properties(type,time_cost,repu, leaf.getLeft_son());
        }
        // 递归遍历右子节点（如果存在）
        if (leaf.getRight_son()!=null) {
            txs=queryTx_by_properties(type,time_cost,repu, leaf.getLeft_son());
        }
        // 如果在当前节点及其子节点中都未找到目标交易，则返回 false
        return txs;
    }

}
