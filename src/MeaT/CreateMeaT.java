package MeaT;

import JDBC.JDBCUtils;
import blockchain.Block;
import blockchain.Transaction;
import dataset.DataProcessing;
import functions.MeaTUtils;
import graph.Edge;
import graph.Node;
import merkletree.Leaf;
import merkletree.MerkleTree;

import javax.xml.crypto.dsig.TransformService;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreateMeaT {
    public Random ran=new Random();
    public ArrayList<Transaction> txs=new ArrayList<>();
    public ArrayList<Block> blocks=new ArrayList<>();
    public String[] types={"food","electronic","face","furniture"};
    public ArrayList<Node> all_nodes=new ArrayList<>();

    public void create_nodes(){
        int i=0;
        for (i=0;i<15;i++)
        {
            String id=String.valueOf(i);
            String type=types[ran.nextInt(4)];
            Node node=new Node(id,type);
            all_nodes.add(node);
            System.out.println(node.getNode_id());
        }

    }

    public void create_txs_blocks(ArrayList<Node> nodes){
        int block_id=1;
        String[] types={"Electronic","Food","Furniture"};
        for (int i=0;i<5;i++)
        {
            String blockid=String.valueOf(block_id);
            Block b=new Block(blockid);
            for(int j=0;j<20;j++)
            {
                String tx_id_string=String.valueOf(j);
                String tx_id=tx_id_string;
                String tx_time_cost=String.valueOf(ran.nextDouble()*150);
                String tx_repu=String.valueOf(ran.nextDouble()*100);
                int firstNumber = ran.nextInt(15); // 生成0到3之间的随机数
                int secondNumber;
                do {
                    secondNumber = ran.nextInt(15);
                } while (secondNumber == firstNumber);
                Node node1=nodes.get(firstNumber);
                Node node2=nodes.get(secondNumber);
                String tx_timestamp=String.valueOf(block_id);
                int type=ran.nextInt(3);
                Transaction tx=new Transaction(tx_id,tx_timestamp,tx_time_cost,tx_repu,node1,node2,types[type]);
                tx.setBlock(b);
                txs.add(tx);
                System.out.println("TX:"+tx.getType());
                b.getTxs().add(tx);
            }
            block_id+=1;
            blocks.add(b);
            System.out.println("Block:"+b.getId());
            System.out.println("Amounts:"+b.getTxs().size());
        }
    }

    public Block query_block(String id, ArrayList<Block> blocks)
    {
        for (Block block:blocks)
        {
            if (block.getId().equals(id))
            {
                return block;
            }
        }
        return null;
    }

    public boolean exist_block(String id, ArrayList<Block> blocks)
    {
        for (Block block:blocks)
        {
            if (block.getId().equals(id))
            {
                return true;
            }
        }
        return false;
    }

    public Node query_node(String id, ArrayList<Node> nodes)
    {
        for (Node node:nodes)
        {
            if (node.getNode_id().equals(id))
            {
                return node;
            }
        }
        return null;
    }




    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {
        String file="./Dataset/100000.csv";
        HashMap<Node,ArrayList<Transaction>> node_txs=new HashMap<>();
        JDBCUtils jdbcUtils=new JDBCUtils();
        DataProcessing dp=new DataProcessing();
        CreateMeaT cmt=new CreateMeaT();
        //将交易、节点、区块写入数据库并暂存至内存
        node_txs=dp.test_dataset_to_txs(file);
        for(Node node:node_txs.keySet())
        {
            dp.insert_transaction_database(node_txs.get(node));
            cmt.all_nodes.add(node);
            cmt.txs.addAll(node_txs.get(node));
        }
//        //miniminitest的操作，一个区块，将所有交易加到这个区块中
//        cmt.blocks.add(cmt.txs.get(0).getBlock());
//        for (Transaction tx: cmt.txs){
//            cmt.blocks.get(0).getTxs().add(tx);
//        }
        //交易、节点存完了，直接读就可以
        //将数据库中的交易和节点读出来
        ResultSet rs = dp.select_transaction();
        ResultSet rs2=dp.select_node();
        ResultSet rs3=dp.select_block();
        while(rs2.next())
        {
            Node node=new Node(rs2.getString("node_id"));
            cmt.all_nodes.add(node);
        }
        while(rs3.next())
        {
            Block block=new Block(rs3.getString("block_id"));
            cmt.blocks.add(block);
        }
        System.out.println("All blocks: "+cmt.blocks.size());
        System.out.println("All nodes: "+cmt.all_nodes.size());
        Node start_node=null;
        Node end_node = new Node(""); // 创建一个空的Node对象，用于重复使用
        while (rs.next()) {
            Transaction tx=null;

            start_node = cmt.query_node(rs.getString("start_node"), cmt.all_nodes);
            // 重复使用end_node对象，而不是在每次迭代中创建新对象
            end_node.setNode_id(rs.getString("end_node"));
            Block b=cmt.query_block(rs.getString("block"),cmt.blocks);
            // 重复使用tx对象，而不是在每次迭代中创建新对象
            if (tx == null) {
                tx = new Transaction("", "", "", "", null, null, "");
            }
            // 更新tx对象的属性
            tx.setId(rs.getString("id"));
            tx.setTimestamp(rs.getString("timestamp"));
            tx.setTime_cost(rs.getString("time_cost"));
            tx.setReputation(rs.getString("reputation"));
            tx.setStart_node(start_node);
            tx.setEnd_node(end_node);
            tx.setType(rs.getString("type"));
            tx.setBlock(b);
            cmt.txs.add(tx);
            System.out.println("Pre tx: " + rs.getString("id"));
            b.getTxs().add(tx);
        }
        System.out.println("Total blocks: "+cmt.blocks.size());

        //Merkle树的
        int leafcount=0;
        for (Block block: cmt.blocks)
        {
            ArrayList<Transaction> transactions=block.getTxs();
            ArrayList<Leaf> leaves=new ArrayList<>();
            for(Transaction tx: transactions)
            {
                Leaf leaf=Leaf.tx_to_leaf(tx);
                leaf.setId(String.valueOf(leafcount));
                leaves.add(leaf);
                leafcount+=1;
            }
            System.out.println("Block "+block.getId()+" has "+leaves.size()+" leaves");
            MerkleTree mt=MerkleTree.create_Merkletree(leaves);
        }

        //开始创建整体的graphmerkle树架构
        System.out.println("------------------------------ Stage 1----------------------------");
        System.out.println("------------------------------Lower MGT----------------------------");
        //首先，按照起始节点的不同，将交易过滤为edge并分类（在MeaTUtils类中）
        int gnlItemcount=0;
        //MerkleGraphTree的
        for (Block block: cmt.blocks)
        {
            HashMap<Node,ArrayList<Edge>> all_transactions=new HashMap<>();
            all_transactions=MeaTUtils.start_node_filter(block.getTxs());
            System.out.println("This block has "+block.getTxs().size()+" transactions");
            //之后，对不同的节点构建多个下层MGT树
            GraphNodeLink gnl=new GraphNodeLink();
            int mgtleafcount=0;
            for(Node node: all_transactions.keySet())
            {
                System.out.println("Pre Node id: "+node.getNode_id());
                GraphNodeLinkItem gnlItem=new GraphNodeLinkItem();
                ArrayList<Edge> edges=all_transactions.get(node);
                ArrayList<GraphLeaf> leaves=new ArrayList<>();
                for(Edge e: edges){
                    GraphLeaf graphLeaf=GraphLeaf.edge_to_leaf(e);
                    graphLeaf.setId(String.valueOf(mgtleafcount));
                    leaves.add(graphLeaf);
                    mgtleafcount+=1;
                }
                MerkleGraphTree lower_mgt=MerkleGraphTree.create_Merkletree(leaves);
                gnlItem.addMGTs(lower_mgt);
                gnlItem.setPre_node(node);
                gnlItem.setId(String.valueOf(gnlItemcount));
                gnlItemcount+=1;
                //将gnlItem写入数据库
                Connection gnlCoon= jdbcUtils.connect_database();
                String gnlSql="insert into graphnodelink (start_node,mgtroot_hashvalue,gnlitem_id) value (?,?,?)";
                PreparedStatement ps=gnlCoon.prepareStatement(gnlSql);
                ps.setString(1,gnlItem.getPre_node().getNode_id());
                ps.setString(2,lower_mgt.getHash_value().toString());
                ps.setString(3,String.valueOf(gnlItemcount));
                ps.executeUpdate();
                try {
                    if (null != ps) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (null != gnlCoon) {
                        gnlCoon.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //GNL中加入item
                gnl.addLink(lower_mgt);
            }
            System.out.println("GNL also has been created. It has "+gnl.getItems().size()+" items now.");
            System.out.println("------------------------------ Stage 2-   -----------------------");
            System.out.println("------------------------------Upper MGT--------------------------");
            //接着，构建上层的MGT树
            MerkleGraphTree upper_mgt=gnl.create_upper_MGT(gnl,block);
            System.out.println("Now the upper MGT has been created, the root is "+block.getRoot()+", and the hash is "+block.getHashroot());
            System.out.println("-------------------------------Stage 3---------------------------");
            System.out.println("------------------------------PST Tree---------------------------");
            //最后，构建PST树
            PropertySemanticTrie pst=new PropertySemanticTrie();
            String[] filter={"type","time_cost","reputation"};
            pst.create_PST(block.getRoot(),cmt.txs, filter, 3);
        }
    }
}
