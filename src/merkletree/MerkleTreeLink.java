package merkletree;

import blockchain.Block;

import java.util.ArrayList;
import java.util.HashMap;

public class MerkleTreeLink {
    private static HashMap<String,MerkleTree> trees=new HashMap<>();

    public static HashMap<String, MerkleTree> getTrees() {
        return trees;
    }

    public static void setTrees(HashMap<String, MerkleTree> trees) {
        MerkleTreeLink.trees = trees;
    }

    public void put_merkletree(String block,MerkleTree mt)
    {
        if (!trees.containsKey(block))
        {
            trees.put(block,mt);
        }
    }

}
