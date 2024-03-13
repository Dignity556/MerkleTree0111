package dataset;

import JDBC.JDBCUtils;
import blockchain.Block;
import blockchain.Transaction;
import graph.Node;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

//数据集 kalgle黑五数据集，55万条交易数据
public class DataProcessing {
    //数据集转化为交易
    public HashMap<Node,ArrayList<Transaction>> dataset_to_txs(String address) throws SQLException {
        HashMap<Node,ArrayList<Transaction>> node_txs=new HashMap<>();
        HashMap<String,Node> nodes=new HashMap<>();
        HashMap<String,Block> blocks=new HashMap<>();
        //连接数据库
        JDBCUtils jdbcUtils=new JDBCUtils();
        //读文件
        try(BufferedReader br=new BufferedReader(new FileReader(address))){
            int count=0;
            int block_id=1;
            String line;
            while((line = br.readLine()) != null)
            {
                if(count==0)
                {
                    count+=1;
                    continue;
                }
                //按行读取csv
                String[] row_values=line.split(",");
                Transaction tx=new Transaction();

                //人为设定id、区块id与时间戳
                if(count%550==0)
                {
                    block_id+=1;
                }
                //写进数据库
                if (!blocks.containsKey(String.valueOf(block_id)))
                {
                    Block block=new Block(String.valueOf(block_id));
                    blocks.put(String.valueOf(block_id),block);
                    System.out.println("Current block:"+block_id);
                    Connection conn=jdbcUtils.connect_database();
                    String sql2= "insert into block (block_id) value (?)";
                    PreparedStatement ps=conn.prepareStatement(sql2);
                    ps.setString(1,String.valueOf(block_id));
                    ps.executeUpdate();
                    try {
                        if (null != ps) {
                            ps.close();
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
                }
                tx.setBlock(blocks.get(String.valueOf(block_id)));
                tx.setId(String.valueOf(count));
                tx.setTimestamp(String.valueOf(block_id));
                //price设为time_cost
                tx.setTime_cost(row_values[11]);
                tx.setType(row_values[8]);
                if(row_values[5].equals("A"))
                {
                    tx.setReputation(String.valueOf(Math.random()*5));
                }else if(row_values[5].equals("B"))
                {
                    tx.setReputation(String.valueOf(Math.random()*10));
                }else{
                    tx.setReputation(String.valueOf(Math.random()*15));
                }
                //给每个交易设置start_node和end_node,其中由于mgt的构建只与start_node有关，因此不用在意end_node;
                if(nodes.containsKey(row_values[0]))
                {
                    tx.setStart_node(nodes.get(row_values[0]));
                    Node start_node=nodes.get(row_values[0]);
                    tx.setEnd_node(new Node(row_values[1]));
                    node_txs.get(start_node).add(tx);
                }else{
                    Node new_node=new Node(row_values[0]);
                    nodes.put(row_values[0],new_node);
                    tx.setStart_node(new_node);
                    tx.setEnd_node(new Node(row_values[1]));
                    ArrayList<Transaction> txs=new ArrayList<>();
                    txs.add(tx);
                    node_txs.put(new_node,txs);
                    System.out.println(new_node.getNode_id());
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String str: nodes.keySet())
        {
            Connection conn=jdbcUtils.connect_database();
            String sql = "insert into node (node_id) value (?)";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1,str);
            ps.executeUpdate();
            try {
                if (null != ps) {
                    ps.close();
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
        }
        return node_txs;
    }

    public HashMap<Node,ArrayList<Transaction>> test_dataset_to_txs(String address) throws SQLException {
        HashMap<Node,ArrayList<Transaction>> node_txs=new HashMap<>();
        HashMap<String,Node> nodes=new HashMap<>();
        HashMap<String,Block> blocks=new HashMap<>();
        //连接数据库
        JDBCUtils jdbcUtils=new JDBCUtils();
        //读文件
        try(BufferedReader br=new BufferedReader(new FileReader(address))){
            int count=0;
            int block_id=1;
            String line;
            while((line = br.readLine()) != null)
            {
                if(count==0)
                {
                    count+=1;
                    continue;
                }
                //按行读取csv
                String[] row_values=line.split(",");
                Transaction tx=new Transaction();

                //人为设定id、区块id与时间戳
                if(count%550==0)
                {
                    block_id+=1;
                }
                //写进数据库
                if (!blocks.containsKey(String.valueOf(block_id)))
                {
                    Block block=new Block(String.valueOf(block_id));
                    blocks.put(String.valueOf(block_id),block);
                    System.out.println("Current block:"+block_id);
                    Connection conn=jdbcUtils.connect_database();
                    String sql2= "insert into block (block_id) value (?)";
                    PreparedStatement ps=conn.prepareStatement(sql2);
                    ps.setString(1,String.valueOf(block_id));
                    ps.executeUpdate();
                    try {
                        if (null != ps) {
                            ps.close();
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
                }
                tx.setBlock(blocks.get(String.valueOf(block_id)));
                tx.setId(String.valueOf(count));
                tx.setTimestamp(String.valueOf(block_id));
                //price设为time_cost
                tx.setTime_cost(row_values[11]);
                tx.setType(row_values[8]);
                if(row_values[5].equals("A"))
                {
                    tx.setReputation(String.valueOf(Math.random()*5));
                }else if(row_values[5].equals("B"))
                {
                    tx.setReputation(String.valueOf(Math.random()*10));
                }else{
                    tx.setReputation(String.valueOf(Math.random()*15));
                }
                //给每个交易设置start_node和end_node,其中由于mgt的构建只与start_node有关，因此不用在意end_node;
                if(nodes.containsKey(row_values[0]))
                {
                    tx.setStart_node(nodes.get(row_values[0]));
                    Node start_node=nodes.get(row_values[0]);
                    tx.setEnd_node(new Node(row_values[1]));
                    node_txs.get(start_node).add(tx);
                }else{
                    Node new_node=new Node(row_values[0]);
                    nodes.put(row_values[0],new_node);
                    tx.setStart_node(new_node);
                    tx.setEnd_node(new Node(row_values[1]));
                    ArrayList<Transaction> txs=new ArrayList<>();
                    txs.add(tx);
                    node_txs.put(new_node,txs);
                    System.out.println(new_node.getNode_id());
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String str: nodes.keySet())
        {
            Connection conn=jdbcUtils.connect_database();
            String sql = "insert into node (node_id) value (?)";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1,str);
            ps.executeUpdate();
            try {
                if (null != ps) {
                    ps.close();
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
        }
        return node_txs;
    }

    public void insert_transaction_database(ArrayList<Transaction> txs) throws SQLException {
        //连接数据库
        JDBCUtils jdbcUtils=new JDBCUtils();
        Connection conn=jdbcUtils.connect_database();
        for (Transaction tx:txs)
        {

            String sql = "insert into transaction (id,timestamp,time_cost,reputation,start_node,end_node,type,block) value (?,?,?,?,?,?,?,?)";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1,String.valueOf(tx.getId()));
            ps.setString(2,tx.getTimestamp());
            ps.setString(3,tx.getTime_cost());
            ps.setString(4,tx.getReputation());
            ps.setString(5,tx.getStart_node().getNode_id());
            ps.setString(6,tx.getEnd_node().getNode_id());
            ps.setString(7,tx.getType());
            ps.setString(8,String.valueOf(tx.getBlock().getId()));
            ps.executeUpdate();
            try {
                if (null != ps) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert_mini_transaction_database(ArrayList<Transaction> txs) throws SQLException {
        //连接数据库
        JDBCUtils jdbcUtils=new JDBCUtils();
        Connection conn=jdbcUtils.connect_database();
        for (Transaction tx:txs)
        {

            String sql = "insert into transaction (id,timestamp,time_cost,reputation,start_node,end_node,type,block) value (?,?,?,?,?,?,?,?)";
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1,String.valueOf(tx.getId()));
            ps.setString(2,tx.getTimestamp());
            ps.setString(3,tx.getTime_cost());
            ps.setString(4,tx.getReputation());
            ps.setString(5,tx.getStart_node().getNode_id());
            ps.setString(6,tx.getEnd_node().getNode_id());
            ps.setString(7,tx.getType());
            ps.setString(8,String.valueOf(tx.getBlock().getId()));
            ps.executeUpdate();
            try {
                if (null != ps) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet select_block() throws SQLException {
        JDBCUtils jdbcUtils=new JDBCUtils();
        Connection conn=jdbcUtils.connect_database();
        String sql="SELECT * FROM block";
        PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        return rs;
    }

    public ResultSet select_transaction() throws SQLException {
        JDBCUtils jdbcUtils=new JDBCUtils();
        Connection conn=jdbcUtils.connect_database();
        String sql="SELECT * FROM transaction";
        PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        return rs;
    }

    public ResultSet select_node() throws SQLException {
        JDBCUtils jdbcUtils=new JDBCUtils();
        Connection conn=jdbcUtils.connect_database();
        String sql="SELECT * FROM node";
        PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        return rs;
    }
}
