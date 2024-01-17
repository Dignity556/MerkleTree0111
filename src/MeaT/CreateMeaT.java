package MeaT;

import blockchain.Block;
import blockchain.Transaction;
import functions.MeaTUtils;
import graph.Edge;
import graph.Node;

import javax.xml.crypto.dsig.TransformService;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CreateMeaT {
    Random ran=new Random();
    ArrayList<Transaction> txs=new ArrayList<>();
    ArrayList<Block> blocks=new ArrayList<>();
    String[] types={"food","electronic","face","furniture"};
    ArrayList<Node> all_nodes=new ArrayList<>();

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
            Block b=new Block(blockid.getBytes(StandardCharsets.UTF_8));
            for(int j=0;j<20;j++)
            {
                String tx_id_string=String.valueOf(j);
                byte[] tx_id=tx_id_string.getBytes(StandardCharsets.UTF_8);
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



    public static void main(String[] args) throws NoSuchAlgorithmException {
        CreateMeaT cmt=new CreateMeaT();
        cmt.create_nodes();
        cmt.create_txs_blocks(cmt.all_nodes);
        //开始创建整体的merkle树架构
        //以block1为例,获取block1的所有交易
        Block block=cmt.blocks.get(0);
        ArrayList<Transaction> block1_txs=cmt.blocks.get(0).getTxs();
        System.out.println("------------------------------ Stage 1----------------------------");
        System.out.println("------------------------------Lower MGT----------------------------");
        //首先，按照起始节点的不同，将交易过滤为edge并分类（在MeaTUtils类中）
        HashMap<Node,ArrayList<Edge>> all_transactions=new HashMap<>();
        all_transactions=MeaTUtils.start_node_filter(block1_txs);
        //之后，对不同的节点构建多个下层MGT树
        GraphNodeLink gnl=new GraphNodeLink();
        for(Node node: all_transactions.keySet())
        {
            System.out.println("Pre Node id: "+node.getNode_id());
            GraphNodeLinkItem gnlItem=new GraphNodeLinkItem();
            ArrayList<Edge> edges=all_transactions.get(node);
            ArrayList<GraphLeaf> leaves=new ArrayList<>();
            for(Edge e: edges){
                GraphLeaf graphLeaf=GraphLeaf.edge_to_leaf(e);
                leaves.add(graphLeaf);
            }
            MerkleGraphTree lower_mgt=MerkleGraphTree.create_Merkletree(leaves);
            gnlItem.addMGTs(lower_mgt);
            gnlItem.setPre_node(node);
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
