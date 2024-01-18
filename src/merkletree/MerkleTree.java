package merkletree;

import blockchain.Block;
import blockchain.Transaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    //private Node subtree;//图树中能用到的，判断子树是哪个节点的交易组成的


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

    public MerkleTree create_Merkletree(ArrayList<Leaf> leaves) throws NoSuchAlgorithmException {
        ArrayList<Leaf> new_leaves=new ArrayList<>();
        MerkleTree mt=new MerkleTree();
        if(leaves.size()==1)
        {
            leaves.get(0).setFather(null);
            mt.setRoot(leaves.get(0));
            mt.setHash_value(leaves.get(0).getHash_id());
            return mt;
        }else{
            for(int i=0;i<leaves.size();i+=2)
            {
                Leaf father=new Leaf();
                father.setLeft_son(leaves.get(i));
                father.setRight_son(leaves.get(i+1));
                father.setHash_id(Leaf.calculateSHA256(leaves.get(i).getHash_id().toString()+leaves.get(i+1).getHash_id().toString()));
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                new_leaves.add(father);
            }
            if (leaves.size()/2==1)
            {
                new_leaves.add(leaves.get(leaves.size()-1));
            }
        }
        create_Merkletree(new_leaves);
        return mt;
    }
}
