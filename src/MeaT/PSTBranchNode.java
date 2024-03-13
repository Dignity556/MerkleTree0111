package MeaT;

import JDBC.JDBCUtils;
import blockchain.Transaction;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    public HashMap<String, PSTBranchNodeItem> getItems() {
        return items;
    }

    public void setItems(HashMap<String, PSTBranchNodeItem> items) {
        this.items = items;
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
        for(int i=0;i<amount;i++)
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
        for(int i=0;i<amount;i++)
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
        //检查是否有item中交易数量为0的，如果有则先删除再返回
        HashMap<String,PSTBranchNodeItem> final_items=new HashMap<>();
        for (String str:items.keySet())
        {
            if(items.get(str).getPre_txs().size()!=0)
            {
                final_items.put(str,items.get(str));
            }
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

    public HashMap<String,PSTBranchNodeItem> leaf_or_branch(HashMap<String,PSTBranchNodeItem> items,PSTBranchNode branchNode) throws SQLException//确认下一层是
    {
        HashMap<String,PSTBranchNodeItem> return_items=new HashMap<>();
        for (String key: items.keySet())
        {
            if(items.get(key).getPre_txs().size()==0)
            {
                System.out.println("A zero item is created");
            }else if(items.get(key).getPre_txs().size()==1)
            {
                PSTLeafNode leafnode=new PSTLeafNode();
                leafnode.setTx(items.get(key).getPre_txs().get(0));
                leafnode.setPreBranch(items.get(key));
                leafnode.setId("prebranchitem_"+items.get(key).getId()+"_leafnode");
                items.get(key).setNext_leaf(leafnode);
                items.get(key).setId("branchitem_nextleaf_"+items.get(key).getNext_leaf().getId());
                return_items.put(key,items.get(key));
                //写入branchnodeitem
                Connection conn=new JDBCUtils().connect_database();
                String sql = "insert into pstbranchnodeitem (id,branch_id,filter_key) value (?,?,?)";
                PreparedStatement ps=conn.prepareStatement(sql);
                ps.setString(1,items.get(key).getId());
                ps.setString(2,branchNode.getBranch_id());
                ps.setString(3,key);
                ps.executeUpdate();
                //写入leafnode
                String sql2 = "insert into pstleafnode (id,branch_item_id) value (?,?)";
                PreparedStatement ps2=conn.prepareStatement(sql2);
                ps2.setString(1,leafnode.getId());
                ps2.setString(2,leafnode.getPreBranch().getId());
                ps2.executeUpdate();
            }else{
                PSTExtensionNode extensionNode=new PSTExtensionNode();
                extensionNode.setPre_item(items.get(key));
                extensionNode.setId("prebranchitem:"+items.get(key).toString());
                items.get(key).setNext_extension(extensionNode);
                return_items.put(key,items.get(key));
                items.get(key).setId("branchitem_nextextension_"+items.get(key).getNext_extension().getId());
                //写入item
                Connection conn=new JDBCUtils().connect_database();
                String sql = "insert into pstbranchnodeitem (id,branch_id,filter_key) value (?,?,?)";
                PreparedStatement ps=conn.prepareStatement(sql);
                ps.setString(1,items.get(key).getId());
                ps.setString(2,branchNode.getBranch_id());
                ps.setString(3,key);
                ps.executeUpdate();
            }
        }
        return return_items;
    }

    public HashMap<String,PSTBranchNodeItem> java_leaf_or_branch(HashMap<String,PSTBranchNodeItem> items,PSTBranchNode branchNode) throws SQLException//确认下一层是
    {
        HashMap<String,PSTBranchNodeItem> return_items=new HashMap<>();
        for (String key: items.keySet())
        {
            if(items.get(key).getPre_txs().size()==0)
            {
                System.out.println("A zero item is created");
            }else if(items.get(key).getPre_txs().size()==1)
            {
                PSTLeafNode leafnode=new PSTLeafNode();
                leafnode.setTx(items.get(key).getPre_txs().get(0));
                leafnode.setPreBranch(items.get(key));
                leafnode.setId("prebranchitem_"+items.get(key).getId()+"_leafnode");
                items.get(key).setNext_leaf(leafnode);
                items.get(key).setId("branchitem_nextleaf_"+items.get(key).getNext_leaf().getId());
                return_items.put(key,items.get(key));
            }else{
                PSTExtensionNode extensionNode=new PSTExtensionNode();
                extensionNode.setPre_item(items.get(key));
                extensionNode.setId("prebranchitem:"+items.get(key).toString());
                items.get(key).setNext_extension(extensionNode);
                return_items.put(key,items.get(key));
                items.get(key).setId("branchitem_nextextension_"+items.get(key).getNext_extension().getId());
            }
        }
        return return_items;
    }

}
