package MeaT;

import blockchain.Transaction;

import java.util.ArrayList;
import java.util.HashMap;

public class PropertySemanticTrie {



    //When the transactions coming, firstly create the property semantic trie
    //filter_order: 按照什么顺序过滤属性; amount: branchnode分成几类
    public void create_PST(MerkleGraphTree mgt, ArrayList<Transaction> txs, String[] filter_order, int amount){
        //本案例中三个属性，timecost，reputation和type
        //创建第一层extensionnode和它下一层的，第一层extension node需要连接merklegraphtree的根
        PSTExtensionNode ex1=new PSTExtensionNode();
        ex1.setRoot_item(mgt);
        ex1.setProperty(filter_order[0]);
        ex1.setPre_item(null);//没有previous的extension_node就是第一个
        PSTBranchNode branch1=new PSTBranchNode();
        ex1.set_next_branch(branch1);
        branch1.setPrevous(ex1);
        //进入第一层branch_node,开始筛选
        HashMap<String,PSTBranchNodeItem> branchitems1=new HashMap<>();
        if(filter_order[0].equals("type"))
        {
            branchitems1 = branch1.category_by_type(txs);
        }else if(filter_order[0].equals("time_cost"))
        {
            branchitems1 = branch1.category_by_timecost(txs,amount);
        }else{
            branchitems1 = branch1.category_by_reputation(txs,amount);
        }
        branch1.leaf_or_branch(branchitems1);
        //开始递归创建
        iterate_filter(branchitems1, 1, filter_order, amount);
    }

    //递归创建时，需要带着每层筛选的属性集types和目前筛选到哪层的pre_type参数去递归
    public void iterate_filter(HashMap<String,PSTBranchNodeItem> branchitems, int pre_type, String[] types, int amount){
        for(String key: branchitems.keySet())
        {
            if(branchitems.get(key).getNext_leaf()!=null || pre_type==3)
            {
                branchitems.get(key).setNext_extension(null);
                PSTLeafNode leafNode=new PSTLeafNode();
                for (Transaction tx:branchitems.get(key).getPre_txs()){
                    leafNode.addTx(tx);
                }
                branchitems.get(key).setNext_leaf(leafNode);
                System.out.println("Leaf");
                continue;
            }else
            {
                PSTExtensionNode extensionNode=branchitems.get(key).getNext_extension();
                extensionNode.setProperty(types[pre_type]);
                extensionNode.setPre_item(branchitems.get(key));//没有previous的extension_node就是第一个
                PSTBranchNode branch1=new PSTBranchNode();
                extensionNode.set_next_branch(branch1);
                branch1.setPrevous(extensionNode);
                HashMap<String,PSTBranchNodeItem> branchitems1=new HashMap<>();
                if(types[pre_type].equals("type"))
                {
                    branchitems1 = branch1.category_by_type(branchitems.get(key).getPre_txs());
                }else if(types[pre_type].equals("time_cost"))
                {
                    branchitems1 = branch1.category_by_timecost(branchitems.get(key).getPre_txs(),amount);
                }else{
                    branchitems1 = branch1.category_by_reputation(branchitems.get(key).getPre_txs(),amount);
                }
                branch1.leaf_or_branch(branchitems1);
                System.out.println(branchitems.get(key));
                iterate_filter(branchitems1,pre_type+1,types,amount);
            }
        }
    }
}
