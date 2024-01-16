package MeaT;

import blockchain.Transaction;

import java.util.ArrayList;
import java.util.HashMap;

public class PropertySemanticTrie {



    //When the transactions coming, firstly create the property semantic trie
    //filter_order: 按照什么顺序过滤属性; amount: branchnode分成几类
    public void create_PST(ArrayList<Transaction> txs, String[] filter_order, int amount){
        //本案例中三个属性，timecost，reputation和type
        //创建第一层extensionnode和它下一层的
        PSTExtensionNode ex1=new PSTExtensionNode();
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
        //开始递归遍历

    }

    public void iterate_filter(HashMap<String,PSTBranchNodeItem> branchitems){

    }
}
