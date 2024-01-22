package merkletree;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

import blockchain.Block;
import blockchain.Transaction;
import graph.Node;
import merkletree.Leaf;
//import merkletree.MerkleTree;

/**
 * Test class to build and print a Merkle Tree.
 */
public class TreeBuilder
{
    /**
     * Main method creates a simple Merkle Tree consisting of
     * two subtrees, each with two leaf nodes, each of these consisting
     * of two data blocks. Then pretty prints the tree to show its structure.
     *
     * @param noargs
     */

    String[] types={"food","electronic","face","furniture"};
    ArrayList<Node> all_nodes=new ArrayList<>();
    ArrayList<Transaction> txs=new ArrayList<>();
    ArrayList<Block> blocks=new ArrayList<>();
    Random ran=new Random();
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
            for(int j=0;j<10;j++)
            {
                String tx_id_string=String.valueOf(j);
                String tx_id=tx_id_string;
                String tx_time_cost=String.valueOf(ran.nextDouble()*15);
                String tx_repu=String.valueOf(ran.nextDouble());
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
                System.out.println("TX:"+tx.getId());
                b.getTxs().add(tx);
            }
            block_id+=1;
            blocks.add(b);
            System.out.println("Block:"+b.getId());
            System.out.println("Amounts:"+b.getTxs().size());
        }
    }

    public static void main(String[] noargs)
    {
        // Define the message digest algorithm to use
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA");
        }
        catch (NoSuchAlgorithmException e)
        {
            // Should never happen, we specified SHA, a valid algorithm
            assert false;
        }
        // Create some nodes
        // Create 50 transactions, each block has 10 transactions;
        TreeBuilder tb=new TreeBuilder();
        tb.create_nodes();
        tb.create_txs_blocks(tb.all_nodes);

    }

}
