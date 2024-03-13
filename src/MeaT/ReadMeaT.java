package MeaT;

import JDBC.JDBCUtils;
import blockchain.Block;
import blockchain.Transaction;
import functions.TxUtils;
import graph.Node;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class ReadMeaT {
    Random ran=new Random();
    ArrayList<Transaction> txs=new ArrayList<>();
    ArrayList<Block> blocks=new ArrayList<>();
    String[] types={"food","electronic","face","furniture"};
    ArrayList<Node> all_nodes=new ArrayList<>();
    JDBCUtils jdbcUtils=new JDBCUtils();
    Connection conn= jdbcUtils.connect_database();
    public ReadMeaT(){

    }
    public ArrayList<Block> read_blocks() throws SQLException {
        String sql = "SELECT * FROM block;";
        //获取sql语句执行者对象
        PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
        //调用查询方法获得结果集
        ResultSet rs = pst.executeQuery();
        //创建集合对象
        ArrayList<Block> blocks = new ArrayList<>();
        while (rs.next()) {
            //获取每个列的数据，封装到Product对象中
            Block block=new Block(rs.getString("block_id"));
            block.setHashroot(rs.getString("hash_value").getBytes(StandardCharsets.UTF_8));
            //把封装好的Product对象存储到list中
            blocks.add(block);
        }
        return blocks;
    }

    public ArrayList<Node> read_nodes() throws SQLException {
        String sql = "SELECT * FROM node;";
        //获取sql语句执行者对象
        PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
        //调用查询方法获得结果集
        ResultSet rs = pst.executeQuery();
        //创建集合对象
        ArrayList<Node> nodes = new ArrayList<>();
        while(rs.next())
        {
            Node node=new Node(rs.getString("node_id"));
            nodes.add(node);
        }
        return nodes;
    }

    public ArrayList<Transaction> read_Txs(ArrayList<Node> nodes, ArrayList<Block> blocks) throws SQLException {
        TxUtils txUtils=new TxUtils();
        String sql = "SELECT * FROM transaction;";
        //获取sql语句执行者对象
        PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
        //调用查询方法获得结果集
        ResultSet rs = pst.executeQuery();
        //创建集合对象
        ArrayList<Transaction> txs = new ArrayList<>();
        while(rs.next())
        {
            Transaction tx=new Transaction(rs.getString("id"),rs.getString("timestamp"),
                    rs.getString("time_cost"),rs.getString("reputation"),
                    txUtils.query_node(nodes,rs.getString("start_node")),
                    txUtils.query_node(nodes,rs.getString("end_node")),
                    rs.getString("type"));
            tx.setBlock(txUtils.query_block(blocks, rs.getString("block")));
            txs.add(tx);
        }
        return txs;
    }

}
