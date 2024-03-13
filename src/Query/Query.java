package Query;

import JDBC.JDBCUtils;
import MeaT.*;
import blockchain.Block;
import blockchain.Transaction;
import dataset.DataProcessing;
import functions.MeaTUtils;
import graph.Edge;
import graph.Node;
import merkletree.Leaf;
import merkletree.MerkleTree;
import merkletree.MerkleTreeLink;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Query {

    /*
    对比实验：
    1.merkle树结构对比：
    （1）普通merkle树：无图，无属性；
    （2）MST：只做属性是否存在的查询，不支持范围查询；
    （3）我们的MGT：支持属性范围查询，支持单一节点查询；
    2.查询方法：满足skyline查询的基本需求
    （1）单一准则：节点查询：单节点的多个交易、多节点的多个交易；
    （2）多准则：属性值+属性范围查询：
    （3）范围准则：节点+属性值+属性范围查询：
    3.已有skyline查询方法实现：证明现有skyline算法可用在本结构上
    blockchain top-k
     */


    /*
    查询方法
     */
    //1.普通merkle树&MST的单个节点查询
    public void mt_single_node_query(String tx_id) throws SQLException {
        long start_time=System.nanoTime();
        for (String string: MerkleTreeLink.getTrees().keySet())
        {
            Transaction tx=MerkleTreeLink.getTrees().get(string).containsTx_by_id(tx_id,MerkleTreeLink.getTrees().get(string).getRoot());
            if(tx!=null)
            {
                break;
            }
        }
        long end_time=System.nanoTime();
        System.out.println("MT/MST single node query time: "+(-start_time+end_time));
    }
    //1.merklegraphtree的单个节点查询
    public void mgt_single_node_query(String node, String tx_id, GraphNodeLink gnl) throws SQLException {
        //在grapgnodelink中查询所有的node对应的mgtroot
        //不在数据库里做，在内存做
        long startTime = System.nanoTime();
        GraphNodeLinkItem gnlitem=gnl.query_by_string(node);
        //遍历每个mgt
        for (MerkleGraphTree mgt:gnlitem.getMgts().values())
        {
            if(mgt.containsTx_by_id(tx_id,mgt.getRoot())!=null)
            {
                break;
            }
        }
        long endtime=System.nanoTime();
        System.out.println("MGT single node query time: "+(-startTime+endtime));
    }
    //2.普通merkle树&MST的节点遍历查询:查询某一节点出发的所有交易
    public void mt_multi_node_query(String node_id) {
        long start_time=System.nanoTime();
        ArrayList<Transaction> total_txs=new ArrayList<>();
        for (String string: MerkleTreeLink.getTrees().keySet())
        {
            total_txs=MerkleTreeLink.getTrees().get(string).iterateTx_by_node(node_id,MerkleTreeLink.getTrees().get(string).getRoot(),total_txs);
        }
        long end_time=System.nanoTime();
        System.out.println("MT/MST multi-nodes query time: "+(-start_time+end_time));
    }
    //2.merklegraphtree的节点遍历查询
    public void mgt_multi_node_query(String node, GraphNodeLink gnl){
        long startTime = System.nanoTime();
        GraphNodeLinkItem gnlitem=gnl.query_by_string(node);
        //遍历每个mgt
        ArrayList<Transaction> txs=new ArrayList<>();
        for (MerkleGraphTree mgt:gnlitem.getMgts().values())
        {
            txs=mgt.iterateTxs_by_id(node,mgt.getRoot(),txs);
        }
        long endtime=System.nanoTime();
        System.out.println("MGT multi nodes query time: "+(-startTime+endtime));
    }
    //3.Merkle树的属性值和范围查询
    public void mt_property_query(String type, String[] time_cost, String[] repu){
        long start_time=System.nanoTime();
        for (String string: MerkleTreeLink.getTrees().keySet())
        {
            ArrayList<Transaction> total_txs=MerkleTreeLink.getTrees().get(string).queryTx_by_properties(type,time_cost,repu,MerkleTreeLink.getTrees().get(string).getRoot());
        }
        long end_time=System.nanoTime();
        System.out.println("MT property query time: "+(-start_time+end_time));
    }
    //3.MST的属性值与范围查询
    public void mst_property_query(HashMap<String,String> queries, String[] time_cost, String[] repu, Block block){
        long start_time=System.currentTimeMillis();
        MerkleGraphTree mgt=block.getRoot();
        PropertySemanticTrie pst=new PropertySemanticTrie();
        ArrayList<Transaction> txs=pst.query_Property(queries,mgt);
        ArrayList<Transaction> final_txs=new ArrayList<>();
        double time_cost_max=Double.valueOf(time_cost[1]);
        double time_cost_min=Double.valueOf(time_cost[0]);
        double repu_max=Double.valueOf(repu[1]);
        double repu_min=Double.valueOf(repu[0]);
        for (Transaction tx: txs)
        {
            if(Double.valueOf(tx.getReputation())<repu_max && Double.valueOf(tx.getReputation())>repu_min
            && Double.valueOf(tx.getTime_cost())<time_cost_max && Double.valueOf(tx.getTime_cost())>time_cost_min){
                final_txs.add(tx);
            }
        }
        long end_time=System.currentTimeMillis();
        System.out.println("MGT property query time: "+(end_time-start_time));
    }
    //3.Merklegraphtree的属性值和范围查询
    public void mgt_property_query(HashMap<String,String> queries, ArrayList<Block> blocks){
        long start_time=System.nanoTime();
        for (Block block: blocks)
        {
            MerkleGraphTree mgt=block.getRoot();
            PropertySemanticTrie pst=new PropertySemanticTrie();
            ArrayList<Transaction> txs=pst.query_Property(queries,mgt);
        }
        long end_time=System.nanoTime();
        System.out.println("MGT property query time: "+(end_time-start_time));
    }
    //4.Merkle树的top-k混合查询
    //某个节点出发的、信誉值与time_cost都在前三名的
    public void mt_mix_query(String node_id,String type, String[] time_cost, String[] repu) {
        long start_time=System.nanoTime();
        ArrayList<Transaction> total_txs=new ArrayList<>();
        for (String string: MerkleTreeLink.getTrees().keySet())
        {
            total_txs=MerkleTreeLink.getTrees().get(string).queryTx_by_properties(type,time_cost,repu,MerkleTreeLink.getTrees().get(string).getRoot());
        }
        for (Transaction tx: total_txs){
            if (!tx.getStart_node().equals(node_id))
            {
                total_txs.remove(tx);
            }
        }
        long end_time=System.nanoTime();
        System.out.println("MT mix query size: "+total_txs.size());
        System.out.println("MT property query time: "+(-start_time+end_time));
    }
    //4.MGT的混合查询
    public void mgt_mix_query(String node_id,HashMap<String,String> queries, Block block){
        long start_time=System.nanoTime();
        MerkleGraphTree mgt=block.getRoot();
        PropertySemanticTrie pst=new PropertySemanticTrie();
        ArrayList<Transaction> txs=pst.query_Property(queries,mgt);
        for (Transaction tx:txs)
        {
            if (!tx.getStart_node().equals(node_id))
            {
                txs.remove(tx);
            }
        }
        long end_time=System.nanoTime();
        System.out.println("MGT mix query size: "+txs.size());
        System.out.println("MGT mix query time: "+(end_time-start_time));
    }
    public void read_db() throws SQLException, NoSuchAlgorithmException {
        String file="./Dataset/miniminitrain.csv";
        HashMap<Node,ArrayList<Transaction>> node_txs=new HashMap<>();
        JDBCUtils jdbcUtils=new JDBCUtils();
        DataProcessing dp=new DataProcessing();
        CreateMeaT cmt=new CreateMeaT();
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
            MerkleTree mt=MerkleTree.create_java_Merkletree(leaves);
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
            all_transactions= MeaTUtils.start_node_filter(block.getTxs());
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
                MerkleGraphTree lower_mgt=MerkleGraphTree.java_create_Merkletree(leaves);
                gnlItem.addMGTs(lower_mgt);
                gnlItem.setPre_node(node);
                gnlItem.setId(String.valueOf(gnlItemcount));
                gnlItemcount+=1;
                //GNL中加入item
                gnl.addLink(lower_mgt);
            }
            System.out.println("GNL also has been created. It has "+gnl.getItems().size()+" items now.");
            System.out.println("------------------------------ Stage 2-   -----------------------");
            System.out.println("------------------------------Upper MGT--------------------------");
            //接着，构建上层的MGT树
            MerkleGraphTree upper_mgt=gnl.java_create_upper_MGT(gnl,block);
            System.out.println("Now the upper MGT has been created, the root is "+block.getRoot()+", and the hash is "+block.getHashroot());
            System.out.println("-------------------------------Stage 3---------------------------");
            System.out.println("------------------------------PST Tree---------------------------");
            //最后，构建PST树
            PropertySemanticTrie pst=new PropertySemanticTrie();
            String[] filter={"type","time_cost","reputation"};
            pst.create_PST(block.getRoot(),cmt.txs, filter, 3);
        }

    }
    public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
        String file="./Dataset/miniminitrain.csv";
        HashMap<Node,ArrayList<Transaction>> node_txs=new HashMap<>();
        JDBCUtils jdbcUtils=new JDBCUtils();
        DataProcessing dp=new DataProcessing();
        CreateMeaT cmt=new CreateMeaT();
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
            MerkleTree mt=MerkleTree.create_java_Merkletree(leaves);
            MerkleTreeLink.getTrees().put(block.getId(),mt);
        }
        //开始创建整体的graphmerkle树架构
        System.out.println("------------------------------ Stage 1----------------------------");
        System.out.println("------------------------------Lower MGT----------------------------");
        //首先，按照起始节点的不同，将交易过滤为edge并分类（在MeaTUtils类中）
        int gnlItemcount=0;
        GraphNodeLink gnl=new GraphNodeLink();
        //MerkleGraphTree的
        for (Block block: cmt.blocks)
        {
            HashMap<Node,ArrayList<Edge>> all_transactions=new HashMap<>();
            all_transactions= MeaTUtils.start_node_filter(block.getTxs());
            System.out.println("This block has "+block.getTxs().size()+" transactions");
            //之后，对不同的节点构建多个下层MGT树
            gnl=new GraphNodeLink();
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
                MerkleGraphTree lower_mgt=MerkleGraphTree.java_create_Merkletree(leaves);
                gnlItem.addMGTs(lower_mgt);
                gnlItem.setPre_node(node);
                gnlItem.setId(String.valueOf(gnlItemcount));
                gnlItemcount+=1;
                //GNL中加入item
                gnl.addLink(lower_mgt);
            }
            System.out.println("GNL also has been created. It has "+gnl.getItems().size()+" items now.");
            System.out.println("------------------------------ Stage 2-   -----------------------");
            System.out.println("------------------------------Upper MGT--------------------------");
            //接着，构建上层的MGT树
            MerkleGraphTree upper_mgt=gnl.java_create_upper_MGT(gnl,block);
            block.setRoot(upper_mgt);
            System.out.println("Now the upper MGT has been created, the root is "+block.getRoot()+", and the hash is "+block.getHashroot());
            System.out.println("-------------------------------Stage 3---------------------------");
            System.out.println("------------------------------PST Tree---------------------------");
            //最后，构建PST树
            PropertySemanticTrie pst=new PropertySemanticTrie();
            String[] filter={"type","time_cost","reputation"};
            block.setRoot(pst.java_create_PST(block.getRoot(),cmt.txs, filter, 3));
        }

        //执行查询，设置循环查询
        //查询1-merkle tree
        String[] targets={"1","5","9","13","17","21","25"};
        String[] target_nodes={"1000001","1000002","1000004","1000005","1000006","1000008","1000008"};
        Query query=new Query();
        long start_time=System.nanoTime();
        for (int i=0;i<targets.length;i++)
        {
            query.mt_single_node_query(targets[i]);
        }
        long end_time=System.nanoTime();
        System.out.println("Total: "+(end_time-start_time));
        System.out.println("Average: "+((end_time-start_time)/targets.length));
        //查询1-MGT
        long MGT_start=System.nanoTime();
        for (int i=0;i<targets.length;i++)
        {
            query.mgt_single_node_query(target_nodes[i],targets[i],gnl);
        }
        long MGT_end=System.nanoTime();
        System.out.println("Total: "+(MGT_end-MGT_start));
        System.out.println("Average: "+((MGT_end-MGT_start)/ targets.length));
        //查询2-merkle tree
        long MT_start_2 =System.nanoTime();
        for (int i=0;i<target_nodes.length;i++)
        {
            query.mt_multi_node_query(target_nodes[i]);
        }
        long MT_end_2=System.nanoTime();
        System.out.println("Total: "+(MT_end_2-MT_start_2));
        System.out.println("Average: "+((MT_end_2-MT_start_2)/target_nodes.length));
        //查询2-MGT
        long MGT_start_2=System.nanoTime();
        for (int i=0;i<target_nodes.length;i++)
        {
            query.mgt_multi_node_query(target_nodes[i],gnl);
        }
        long MGT_end_2=System.nanoTime();
        System.out.println("Total: "+(MGT_end_2-MGT_start_2));
        System.out.println("Average: "+((MGT_end_2-MGT_start_2)/target_nodes.length));
        //查询3-MT
        long MT_start_3=System.nanoTime();
        String[] cost= {"10000","15000"};
        String[] repu={"1","5"};
        query.mt_property_query("1",cost,repu);
        long MT_end_3=System.nanoTime();
        System.out.println("MT Total3: "+(MT_end_3-MT_start_3));
        //查询3-MGT
        long MGT_start_3=System.nanoTime();
        HashMap<String,String> query_input=new HashMap<>();
        query_input.put("type","1");
        query_input.put("reputation","1,5");
        query_input.put("time_cost","10000,15000");
        query.mgt_property_query(query_input,cmt.blocks);
        long MGT_end_3=System.nanoTime();
        System.out.println("MGT Total3: "+(MGT_end_3-MGT_start_3));
        //查询4-mt
        long MT_start_4=System.nanoTime();
        String[] cost_4= {"10000","15000"};
        String[] repu_4={"1","5"};
        String node_id="1000001";
        query.mt_mix_query(node_id,"1",cost_4,repu_4);
        long MT_end_4=System.nanoTime();
        System.out.println("MT Total4: "+(MT_end_4-MT_start_4));
        //查询4-MGT
        long MGT_start_4=System.nanoTime();
        HashMap<String,String> query_input_4=new HashMap<>();
        query_input_4.put("type","1");
        query_input_4.put("reputation","1,5");
        query_input_4.put("time_cost","10000,16000");
        query.mgt_mix_query(node_id,query_input,cmt.blocks.get(0));
        long MGT_end_4=System.nanoTime();
        System.out.println("MGT Total4: "+(MGT_end_4-MGT_start_4));
    }
}
