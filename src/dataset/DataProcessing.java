package dataset;

import blockchain.Transaction;
import graph.Node;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

//数据集 kalgle黑五数据集，55万条交易数据
public class DataProcessing {
    //数据集转化为交易
    public HashMap<Node,ArrayList<Transaction>> dataset_to_txs(String address){
        HashMap<Node,ArrayList<Transaction>> node_txs=new HashMap<>();
        HashMap<String,Node> nodes=new HashMap<>();
        try(BufferedReader br=new BufferedReader(new FileReader(address))){
            int count=0;
            int block_id=0;
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
                //人为设定id和时间
                if(count/550==0)
                {
                    block_id+=1;
                }
                tx.setId(String.valueOf(count).getBytes(StandardCharsets.UTF_8));
                tx.setTimestamp(String.valueOf(block_id));
                //price设为time_cost
                tx.setTime_cost(row_values[11]);
                tx.setType(row_values[8]);
                if(row_values[5].equals("A"))
                {
                    tx.setReputation("1");
                }else if(row_values[5].equals("B"))
                {
                    tx.setReputation("2");
                }else{
                    tx.setReputation("3");
                }
                //给每个交易设置start_node和end_node,其中由于mgt的构建只与start_node有关，因此不用在意end_node;
                if(nodes.containsKey(row_values[0]))
                {
                    tx.setStart_node(nodes.get(row_values[0]));
                    Node start_node=nodes.get(row_values[0]);
                    tx.setEnd_node(nodes.get(row_values[1]));
                    node_txs.get(start_node).add(tx);
                }else{
                    Node new_node=new Node(row_values[0]);
                    nodes.put(row_values[0],new_node);
                    tx.setStart_node(new_node);
                    tx.setEnd_node(nodes.get(row_values[1]));
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
        return node_txs;
    }
    public static void main(String[] args){
        String file="./Dataset/goods.csv";
        HashMap<Node,ArrayList<Transaction>> node_txs=new HashMap<>();
        DataProcessing dp=new DataProcessing();
        node_txs=dp.dataset_to_txs(file);
        System.out.println(node_txs.size());
    }
}
