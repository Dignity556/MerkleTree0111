package MeaT;

import JDBC.JDBCUtils;
import blockchain.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PropertySemanticTrie {



    //When the transactions coming, firstly create the property semantic trie
    //filter_order: 按照什么顺序过滤属性; amount: branchnode分成几类
    public MerkleGraphTree create_PST(MerkleGraphTree mgt, ArrayList<Transaction> txs, String[] filter_order, int amount) throws SQLException {
        //本案例中三个属性，timecost，reputation和type
        //创建第一层extensionnode和它下一层的，第一层extension node需要连接merklegraphtree的根
        PSTExtensionNode ex1=new PSTExtensionNode();
        //root与第一层的extension_node相关联
        ex1.setRoot_item(mgt);
        mgt.getRoot().setPstExtensionNode(ex1);
        ex1.setProperty(filter_order[0]);
        ex1.setPre_item(null);//没有previous的extension_node就是第一个
        PSTBranchNode branch1=new PSTBranchNode();
        branch1.setPrevous(ex1);
        ex1.setId("root:"+filter_order[0]);
        branch1.setBranch_id("branch:"+ex1.getProperty()+",pre_extension:"+ex1.getId());
        ex1.set_next_branch(branch1);

        //进入第一层branch_node,开始筛选
        HashMap<String,PSTBranchNodeItem> branchitems1=new HashMap<>();
        if(filter_order[0].equals("type"))
        {
            branchitems1 = branch1.category_by_type(txs);
            System.out.println("First layer: type");
        }else if(filter_order[0].equals("time_cost"))
        {
            branchitems1 = branch1.category_by_timecost(txs,amount);
            System.out.println("First layer: time");
        }else{
            branchitems1 = branch1.category_by_reputation(txs,amount);
            System.out.println("First layer: repu");
        }
        //branchnode写入
        Connection conn=new JDBCUtils().connect_database();
        String sql2 = "insert into pstbranchnode (branch_node_id,property,pre_extension) value (?,?,?)";
        PreparedStatement ps2=conn.prepareStatement(sql2);
        ps2.setString(1,branch1.getBranch_id());
        ps2.setString(2,branch1.getPrevous().getProperty());
        ps2.setString(3,branch1.getPrevous().getId());
        ps2.executeUpdate();
        //ex1写入
        String sql = "insert into pstextensionnode (property,pre_block,next_branch,extension_id) value (?,?,?,?)";
        PreparedStatement ps=conn.prepareStatement(sql);
        ps.setString(1,ex1.getProperty());
        ps.setString(2,ex1.getRoot_item().getBlock().getId());
        ps.setString(3,ex1.getNext_item().getBranch_id());
        ps.setString(4,ex1.getId());
        ps.executeUpdate();
        branch1.setPrevous(ex1);
        branch1.setItems(branchitems1);
        branch1.leaf_or_branch(branchitems1,branch1);
        //开始递归创建
        iterate_filter(branchitems1, 1, filter_order, amount,branch1);
        return mgt;
    }

    public MerkleGraphTree java_create_PST(MerkleGraphTree mgt, ArrayList<Transaction> txs, String[] filter_order, int amount) throws SQLException {
        //本案例中三个属性，timecost，reputation和type
        //创建第一层extensionnode和它下一层的，第一层extension node需要连接merklegraphtree的根
        PSTExtensionNode ex1=new PSTExtensionNode();
        //root与第一层的extension_node相关联
        ex1.setRoot_item(mgt);
        mgt.getRoot().setPstExtensionNode(ex1);
        ex1.setProperty(filter_order[0]);
        ex1.setPre_item(null);//没有previous的extension_node就是第一个
        PSTBranchNode branch1=new PSTBranchNode();
        branch1.setPrevous(ex1);
        ex1.setId("root:"+filter_order[0]);
        branch1.setBranch_id("branch:"+ex1.getProperty()+",pre_extension:"+ex1.getId());
        ex1.set_next_branch(branch1);

        //进入第一层branch_node,开始筛选
        HashMap<String,PSTBranchNodeItem> branchitems1=new HashMap<>();
        if(filter_order[0].equals("type"))
        {
            branchitems1 = branch1.category_by_type(txs);
            System.out.println("First layer: type");
        }else if(filter_order[0].equals("time_cost"))
        {
            branchitems1 = branch1.category_by_timecost(txs,amount);
            System.out.println("First layer: time");
        }else{
            branchitems1 = branch1.category_by_reputation(txs,amount);
            System.out.println("First layer: repu");
        }
        branch1.setPrevous(ex1);
        branch1.setItems(branchitems1);
        branch1.leaf_or_branch(branchitems1,branch1);
        //开始递归创建
        iterate_filter(branchitems1, 1, filter_order, amount,branch1);
        return mgt;
    }

    //递归创建时，需要带着每层筛选的属性集types和目前筛选到哪层的pre_type参数去递归
    public void iterate_filter(HashMap<String,PSTBranchNodeItem> branchitems, int pre_type, String[] types, int amount, PSTBranchNode branchNode) throws SQLException {
        for(String key: branchitems.keySet())
        {//判断是否达到叶子节点 是叶子节点就变成叶子节点
            if(branchitems.get(key).getNext_leaf()!=null || pre_type==3)
            {
                branchitems.get(key).setNext_extension(null);
                PSTLeafNode leafNode=new PSTLeafNode();
                for (Transaction tx:branchitems.get(key).getPre_txs()){
                    leafNode.addTx(tx);
                }
                branchitems.get(key).setNext_leaf(leafNode);
                leafNode.setPreBranch(branchitems.get(key));
                leafNode.setId("prebranch"+leafNode.getPreBranch().getId()+"leafnode");
//              //item更新
                Connection conn=new JDBCUtils().connect_database();
//                String sql = "update psfbranchnodeitem set balance = 1500 where id = 3\"";
//                PreparedStatement ps=conn.prepareStatement(sql);
//                ps.setString(1,branchitems.get(key).getId());
//                ps.setString(2,branchNode.getBranch_id());
//                ps.setString(3,key);
//                ps.executeUpdate();
                //leaf写入
                String sql2 = "insert into pstleafnode (id,branch_item_id) value (?,?)";
                PreparedStatement ps2=conn.prepareStatement(sql2);
                ps2.setString(1,leafNode.getId());
                ps2.setString(2,leafNode.getPreBranch().getId());
                ps2.executeUpdate();
                System.out.println("Leaf");
                try {
                    if (null != ps2) {
                        ps2.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (null != conn) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }else
            {
                PSTExtensionNode extensionNode=branchitems.get(key).getNext_extension();
                if(extensionNode!=null)
                {
                    extensionNode.setProperty(types[pre_type]);
//                    extensionNode.setPre_item(branchitems.get(key));//没有previous的extension_node就是第一个
                    PSTBranchNode branch1=new PSTBranchNode();
                    extensionNode.set_next_branch(branch1);
                    branch1.setBranch_id("branch:"+extensionNode.getProperty()+",pre_extension:"+extensionNode.getId());
                    extensionNode.setId("preitem_"+branchitems.get(key).getId()+"_nextbranch_"+branch1);
                    //ex1写入
                    Connection conn=new JDBCUtils().connect_database();
                    String sql2 = "insert into pstextensionnode (property,pre_item,next_branch,extension_id) value (?,?,?,?)";
                    PreparedStatement ps2=conn.prepareStatement(sql2);
                    ps2.setString(1,extensionNode.getProperty());
                    ps2.setString(2,extensionNode.getPre_item().getId());
                    ps2.setString(3,extensionNode.getNext_item().getBranch_id());
                    ps2.setString(4, extensionNode.getId());
                    ps2.executeUpdate();
                    branch1.setPrevous(extensionNode);
                    HashMap<String,PSTBranchNodeItem> branchitems1=new HashMap<>();
                    if(types[pre_type].equals("type"))
                    {
                        branchitems1 = branch1.category_by_type(branchitems.get(key).getPre_txs());
                        System.out.println("This layer: type");
                    }else if(types[pre_type].equals("time_cost"))
                    {
                        branchitems1 = branch1.category_by_timecost(branchitems.get(key).getPre_txs(),amount);
                        System.out.println("This layer: time");
                    }else{
                        branchitems1 = branch1.category_by_reputation(branchitems.get(key).getPre_txs(),amount);
                        System.out.println("This layer: repu");
                    }
//                    branch1.setBranch_id("branch:"+extensionNode.getProperty()+",pre_extension:"+extensionNode.getId());
                    //branchnode写入
                    String sql3 = "insert into pstbranchnode (branch_node_id,property,pre_extension) value (?,?,?)";
                    PreparedStatement ps3=conn.prepareStatement(sql3);
                    ps3.setString(1,branch1.getBranch_id());
                    ps3.setString(2,branch1.getPrevous().getProperty());
                    ps3.setString(3,branch1.getPrevous().getId());
                    ps3.executeUpdate();
                    HashMap<String,PSTBranchNodeItem> new_branch_items=branch1.leaf_or_branch(branchitems1,branch1);
                    try {
                        if (null != ps2) {
                            ps2.close();
                        }
                        if (null != ps3) {
                            ps3.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (null != conn) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
//                    System.out.println(branchitems1.get(key));
                    iterate_filter(new_branch_items,pre_type+1,types,amount,branch1);
                }
            }
        }
    }

    public void java_iterate_filter(HashMap<String,PSTBranchNodeItem> branchitems, int pre_type, String[] types, int amount, PSTBranchNode branchNode) throws SQLException {
        for(String key: branchitems.keySet())
        {//判断是否达到叶子节点 是叶子节点就变成叶子节点
            if(branchitems.get(key).getNext_leaf()!=null || pre_type==3)
            {
                branchitems.get(key).setNext_extension(null);
                PSTLeafNode leafNode=new PSTLeafNode();
                for (Transaction tx:branchitems.get(key).getPre_txs()){
                    leafNode.addTx(tx);
                }
                branchitems.get(key).setNext_leaf(leafNode);
                leafNode.setPreBranch(branchitems.get(key));
                leafNode.setId("prebranch"+leafNode.getPreBranch().getId()+"leafnode");
                System.out.println("Leaf");

            }else
            {
                PSTExtensionNode extensionNode=branchitems.get(key).getNext_extension();
                if(extensionNode!=null)
                {
                    extensionNode.setProperty(types[pre_type]);
                    PSTBranchNode branch1=new PSTBranchNode();
                    extensionNode.set_next_branch(branch1);
                    branch1.setBranch_id("branch:"+extensionNode.getProperty()+",pre_extension:"+extensionNode.getId());
                    extensionNode.setId("preitem_"+branchitems.get(key).getId()+"_nextbranch_"+branch1);
                    branch1.setPrevous(extensionNode);
                    HashMap<String,PSTBranchNodeItem> branchitems1=new HashMap<>();
                    if(types[pre_type].equals("type"))
                    {
                        branchitems1 = branch1.category_by_type(branchitems.get(key).getPre_txs());
                        System.out.println("This layer: type");
                    }else if(types[pre_type].equals("time_cost"))
                    {
                        branchitems1 = branch1.category_by_timecost(branchitems.get(key).getPre_txs(),amount);
                        System.out.println("This layer: time");
                    }else{
                        branchitems1 = branch1.category_by_reputation(branchitems.get(key).getPre_txs(),amount);
                        System.out.println("This layer: repu");
                    }
                    HashMap<String,PSTBranchNodeItem> new_branch_items=branch1.leaf_or_branch(branchitems1,branch1);
                    java_iterate_filter(new_branch_items,pre_type+1,types,amount,branch1);
                }
            }
        }
    }

    //按照属性查询
    public ArrayList<Transaction> query_Property(HashMap<String,String> queries, MerkleGraphTree mgt){
        ArrayList<Transaction> transactions=new ArrayList<>();
        Queue<PSTExtensionNode> extension_queue=new LinkedList<>();
        //第一层
        PSTExtensionNode extensionNode1=mgt.getRoot().getPstExtensionNode();
        PSTBranchNode branchNode1= extensionNode1.getNext_item();
        HashMap<String,PSTBranchNodeItem> branchNodeItems=branchNode1.getItems();
        //一层一层来，第一层是属性，第二层是timecost，第三层是reputation
        //如果需要查询属性类型，就加入指定的extensionnode，否则就全加入
        if(queries.containsKey("type"))
        {
            extension_queue.add(type_extension_filter(queries.get("type"), branchNodeItems));
            transactions.addAll(type_leaf_filter(queries.get("type"), branchNodeItems));
            queries.remove("type");
        }else{
            for (String key:branchNodeItems.keySet()){
                if(branchNodeItems.get(key).getNext_extension()!=null)
                {
                    extension_queue.add(branchNodeItems.get(key).getNext_extension());
                }else if(branchNodeItems.get(key).getNext_leaf()!=null)
                {
                    transactions.addAll(branchNodeItems.get(key).getNext_leaf().getTxs());
                }
            }
        }
        //第二层之后，用队列代替递归
        if(queries.size()!=0)
        {
            transactions.addAll(iterate_query_pst(extension_queue,queries));
        }
        return transactions;
    }

    public ArrayList<Transaction> iterate_query_pst(Queue<PSTExtensionNode> extension_queue, HashMap<String,String> queries){
        ArrayList<Transaction> txs=new ArrayList<>();
        if(queries.containsKey("time_cost") && !queries.containsKey("reputation"))
        {
            while (extension_queue.size()!=0)
            {
                if(extension_queue.peek().getProperty().equals("time_cost"))
                {
                    PSTExtensionNode pre=extension_queue.peek();
                    HashMap<String,PSTBranchNodeItem> items=pre.getNext_item().getItems();
                    txs.addAll(value_leaf_filter("time_cost",queries.get("time_cost"),items));
                    extension_queue.addAll(value_extension_filter("time_cost",queries.get("time_cost"),items));
                    extension_queue.poll();
                }else{
                    PSTExtensionNode pre=extension_queue.peek();
                    HashMap<String,PSTBranchNodeItem> items=pre.getNext_item().getItems();
                    txs.addAll(value_leaf_filter("time_cost",queries.get("time_cost"),items));
                    extension_queue.poll();
                }
            }
        }else if(!queries.containsKey("time_cost") && queries.containsKey("reputation"))
        {
            for (PSTExtensionNode node:extension_queue)
            {
                PSTBranchNode pre=node.getNext_item();
                for (String key:pre.getItems().keySet()){
                    extension_queue.add(pre.getItems().get(key).getNext_extension());
                }
            }
            while (extension_queue.size()!=0)
            {
                PSTExtensionNode pre=extension_queue.peek();
                HashMap<String,PSTBranchNodeItem> items=pre.getNext_item().getItems();
                txs.addAll(value_leaf_filter("time_cost",queries.get("time_cost"),items));
                extension_queue.poll();
            }
        }else
        {
            while (extension_queue.size()!=0)
            {
                PSTExtensionNode pre=extension_queue.peek();
                HashMap<String,PSTBranchNodeItem> items=pre.getNext_item().getItems();
                txs.addAll(value_leaf_filter("time_cost",queries.get("time_cost"),items));
                extension_queue.addAll(value_extension_filter("time_cost",queries.get("time_cost"),items));
                extension_queue.poll();
            }
        }
        return txs;
    }


    //确认属性在哪个item中
    public PSTExtensionNode type_extension_filter(String type, HashMap<String,PSTBranchNodeItem> branchNodeItems){
        PSTBranchNodeItem nodeitems=branchNodeItems.get(type);
        PSTExtensionNode extensionNode=nodeitems.getNext_extension();
        return extensionNode;
    }

    //确认属性在哪个item中
    public ArrayList<Transaction> type_leaf_filter(String type, HashMap<String,PSTBranchNodeItem> branchNodeItems){
        ArrayList<Transaction> txs=new ArrayList<>();
        PSTBranchNodeItem nodeitems=branchNodeItems.get(type);
        if (nodeitems.getNext_leaf()!=null)
        {
            txs=nodeitems.getNext_leaf().getTxs();
        }
        return txs;
    }

    //返回所有item的再下一层extension_node
    public LinkedList<PSTExtensionNode> value_extension_filter(String cost_or_repu, String property_value, HashMap<String,PSTBranchNodeItem> branchNodeItems)
    {
        //先比较值在哪些key中
        LinkedList<PSTExtensionNode> nodes=new LinkedList<>();
        ArrayList<String> keys=new ArrayList<>();
        for (String key: branchNodeItems.keySet())
        {
            String[] min_max=key.split(",");
            double min=Double.valueOf(min_max[0]);
            double max=Double.valueOf(min_max[1]);
            double value=Double.valueOf(property_value);
            if(value>max || (value<=max && value>=min))
            {
                keys.add(key);
            }
        }
        //根据对应的key值筛选extensionnode
        for (int i=0; i<keys.size();i++)
        {
            if(branchNodeItems.get(keys.get(i)).getNext_extension()!=null)
            {
                nodes.add(branchNodeItems.get(keys.get(i)).getNext_extension());
            }
        }
        return nodes;
    }

    //返回所有leaf的transactions
    public ArrayList<Transaction> value_leaf_filter(String cost_or_repu, String property_value, HashMap<String,PSTBranchNodeItem> branchNodeItems)
    {
        //先比较值在哪些key中
        ArrayList<Transaction> txs=new ArrayList<>();
        ArrayList<String> keys=new ArrayList<>();
        for (String key: branchNodeItems.keySet())
        {
            //cost/repu的值用
            String[] min_max=key.split(",");
            double min=Double.valueOf(min_max[0]);
            double max=Double.valueOf(min_max[1]);
            double value=Double.valueOf(property_value);
            if(value>max || (value<=max && value>=min))
            {
                keys.add(key);
            }
        }
        //根据对应的key值筛选extensionnode
        for (int i=0; i<keys.size();i++)
        {
            if(branchNodeItems.get(keys.get(i)).getNext_leaf()!=null)
            {
                txs.addAll(branchNodeItems.get(keys.get(i)).getNext_leaf().getTxs());
            }
        }
        return txs;
    }

}
