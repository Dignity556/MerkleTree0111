package JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {


    public Connection connect_database()
    {
        Connection conn;
        conn = null;
        Statement st = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // 建立连接
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/skyline_query", "root", "123456");
            //st = conn.createStatement();
//            String sql = "insert into transaction (id) value ('1')";
//            st.execute(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    public static void main(String[] args) throws SQLException {
        JDBCUtils ju=new JDBCUtils();
        Connection conn= ju.connect_database();
        Statement st= conn.createStatement();
        String sql = "insert into transaction (id) value ('1')";
        st.execute(sql);
        try {
            if (null != st) {
                st.close();
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
}
