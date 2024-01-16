package MeaT;

import blockchain.Block;
import blockchain.Transaction;
import graph.Node;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            for(int j=0;j<10;j++)
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



    public static void main(String[] args){
        CreateMeaT cmt=new CreateMeaT();
        cmt.create_nodes();
        cmt.create_txs_blocks(cmt.all_nodes);
        PropertySemanticTrie pst=new PropertySemanticTrie();
        String[] filter={"type","time_cost","reputation"};
        pst.create_PST(cmt.txs, filter, 3);
    }
}
