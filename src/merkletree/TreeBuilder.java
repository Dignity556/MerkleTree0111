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
import merkletree.MerkleTree;

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
            Block b=new Block(blockid.getBytes(StandardCharsets.UTF_8));
            for(int j=0;j<10;j++)
            {
                String tx_id_string=String.valueOf(j);
                byte[] tx_id=tx_id_string.getBytes(StandardCharsets.UTF_8);
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
//        private String id;
//        private String timestamp;
//        private String time_cost;
//        private String reputation; //相当于权重
//        private Node start_node;
//        private Node end_node;



//
//
//        // Create some data blocks to be assigned to leaf nodes
//        final byte[] block1 = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
//        final byte[] block2 = {(byte) 0xae, (byte) 0x45, (byte) 0x98, (byte) 0xff};
//        final byte[] block3 = {(byte) 0x5f, (byte) 0xd3, (byte) 0xcc, (byte) 0xe1};
//        final byte[] block4 = {(byte) 0xcb, (byte) 0xbc, (byte) 0xc4, (byte) 0xe2};
//        final byte[] block5 = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
//        final byte[] block6 = {(byte) 0xae, (byte) 0x45, (byte) 0x98, (byte) 0xff};
//        final byte[] block7 = {(byte) 0x5f, (byte) 0xd3, (byte) 0xcc, (byte) 0xe1};
//        final byte[] block8 = {(byte) 0xcb, (byte) 0xbc, (byte) 0xc4, (byte) 0xe2};
//
//        // Create leaf nodes containing these blocks
//        final List<byte[]> blocks1and2 = new ArrayList<byte[]>();
//        blocks1and2.add(block1);
//        blocks1and2.add(block2);
//
//        final List<byte[]> blocks3and4 = new ArrayList<byte[]>();
//        blocks3and4.add(block3);
//        blocks3and4.add(block4);
//
//        final List<byte[]> blocks5and6 = new ArrayList<byte[]>();
//        blocks5and6.add(block5);
//        blocks5and6.add(block6);
//
//        final List<byte[]> blocks7and8 = new ArrayList<byte[]>();
//        blocks7and8.add(block7);
//        blocks7and8.add(block8);
//

//
//        // Build up the Merkle Tree from the leaves
//        final MerkleTree branch1 = new MerkleTree(md);
//        branch1.add(leaf1, leaf2);
//
//        final MerkleTree branch2 = new MerkleTree(md);
//        branch2.add(leaf3, leaf4);
//
//        final MerkleTree merkleTree = new MerkleTree(md);
//        merkleTree.add(branch1, branch2);
//
//        // Return the digest for the entire tree
//        merkleTree.prettyPrint();
        TreeBuilder tb=new TreeBuilder();
        tb.create_nodes();
        tb.create_txs_blocks(tb.all_nodes);
//        List<Block> blocks1=new ArrayList<>();
//        blocks1.add(tb.blocks.get(0));
//        List<Block> blocks2=new ArrayList<>();
//        blocks2.add(tb.blocks.get(1));
//        List<Block> blocks3=new ArrayList<>();
//        blocks3.add(tb.blocks.get(2));
//        List<Block> blocks4=new ArrayList<>();
//        blocks4.add(tb.blocks.get(3));
//        blocks4.add(tb.blocks.get(4));
        //Each leaf contains a block, and each block contains 10 transactions
        final Leaf leaf1 = new Leaf(tb.blocks.get(0).getTxs());
        final Leaf leaf2 = new Leaf(tb.blocks.get(1).getTxs());
        final Leaf leaf3 = new Leaf(tb.blocks.get(3).getTxs());
        final Leaf leaf4 = new Leaf(tb.blocks.get(4).getTxs());
        final MerkleTree branch1 = new MerkleTree(md);
        branch1.add(leaf1, leaf2);
        final MerkleTree branch2 = new MerkleTree(md);
        branch2.add(leaf3, leaf4);
        final MerkleTree merkleTree = new MerkleTree(md);
        merkleTree.add(branch1,branch2);
        merkleTree.prettyPrint();
    }

}
