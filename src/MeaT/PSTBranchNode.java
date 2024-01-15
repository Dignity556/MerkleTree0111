package MeaT;

import blockchain.Transaction;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

//
public class PSTBranchNode {
    private String branch_id;
    private PSTExtensionNode prevous;
    private HashMap<String,PSTBranchNodeItem> items;//到该层时还剩的交易数量及对应的分类

    public PSTBranchNode(){
        this.items=new HashMap<>();
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public PSTExtensionNode getPrevous() {
        return prevous;
    }

    public void setPrevous(PSTExtensionNode prevous) {
        this.prevous = prevous;
    }

    //按照数值范围划分
    public HashMap<String,PSTBranchNodeItem> category_by_timecost(ArrayList<Transaction> txs,int amount){
        HashMap<String,PSTBranchNodeItem> items=new HashMap<>();
        double max=Double.valueOf(txs.get(0).getTime_cost());
        double min=Double.valueOf(txs.get(0).getTime_cost());
        for (Transaction ts: txs){
            if (Double.valueOf(ts.getTime_cost())>max){
                max=Double.valueOf(ts.getTime_cost());
            }else if(Double.valueOf(ts.getTime_cost())<min)
            {
                min=Double.valueOf(ts.getTime_cost());
            }
        }
        for(int i=0;i<amount-1;i++)
        {
            PSTBranchNodeItem bnitem=new PSTBranchNodeItem();
            String range=String.valueOf((min+(i-1)*(max-min))/(amount-1))+","+String.valueOf((min+i*(max-min))/(amount-1));
            items.put(range,bnitem);
        }
        for (Transaction ts:txs)
        {
             int mole= (int) Math.floor(((amount-1)*(Double.valueOf(ts.getTime_cost())-min))/(max-min));
             String key=String.valueOf((min+(mole-1)*(max-min))/(amount-1))+","+String.valueOf((min+mole*(max-min))/(amount-1));
             items.get(key).getPre_txs().add(ts);
        }
        return items;

    }
    //按照reputation范围划分
    public HashMap<String,PSTBranchNodeItem> category_by_reputation(ArrayList<Transaction> txs,int amount){
        HashMap<String,PSTBranchNodeItem> items=new HashMap<>();
        double max=Double.valueOf(txs.get(0).getReputation());
        double min=Double.valueOf(txs.get(0).getReputation());
        for (Transaction ts: txs){
            if (Double.valueOf(ts.getReputation())>max){
                max=Double.valueOf(ts.getReputation());
            }else if(Double.valueOf(ts.getReputation())<min)
            {
                min=Double.valueOf(ts.getReputation());
            }
        }
        for(int i=0;i<amount-1;i++)
        {
            PSTBranchNodeItem bnitem=new PSTBranchNodeItem();
            String range=String.valueOf((min+(i-1)*(max-min))/(amount-1))+","+String.valueOf((min+i*(max-min))/(amount-1));
            items.put(range,bnitem);
        }
        for (Transaction ts:txs)
        {
            int mole= (int) Math.floor(((amount-1)*(Double.valueOf(ts.getReputation())-min))/(max-min));
            String key=String.valueOf((min+(mole-1)*(max-min))/(amount-1))+","+String.valueOf((min+mole*(max-min))/(amount-1));
            items.get(key).getPre_txs().add(ts);
        }
        return items;

    }

    //按照交易属性划分
    public HashMap<String,PSTBranchNodeItem> category_by_type(ArrayList<Transaction> txs){
        HashMap<String,PSTBranchNodeItem> extension=new HashMap<>();
        for(Transaction tx: txs)
        {
            String type=tx.getType();
            if(!extension.containsKey(type)){
                PSTBranchNodeItem item=new PSTBranchNodeItem();
                item.getPre_txs().add(tx);
                extension.put(type,item);
            }else
            {
                extension.get(type).getPre_txs().add(tx);
            }
        }
        return extension;
    }

    public void leaf_or_branch(HashMap<String,PSTBranchNodeItem> items)//确认下一层是
    {
        for (String key: items.keySet())
        {
            if(items.get(key).getPre_txs().size()==1)
            {
                PSTLeafNode leafnode=new PSTLeafNode();
                leafnode.setTx(items.get(key).getPre_txs().get(0));
                leafnode.setPreBranch(items.get(key));
                items.get(key).setNext_leaf(leafnode);
            }else{
                PSTExtensionNode extensionNode=new PSTExtensionNode();
                extensionNode.setPre_item(items.get(key));
                items.get(key).setNext_extension(extensionNode);
            }
        }
    }

}
