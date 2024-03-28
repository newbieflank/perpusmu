
package Login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Config {
    private static Connection mysqlconfig;
    public static Connection configDB() throws SQLException {
    try{
    String url = "jdbc:mysql://localhost:3306/perpusmu";
    String user = "root";
    String pass = "";
    DriverManager.registerDriver(new com.mysql.jdbc.Driver());
    mysqlconfig = DriverManager.getConnection(url, user, pass);
    }catch (Exception e){
    System.err.println("koneksi gagal" + e.getMessage());
    }
    return mysqlconfig;
    }

    public static PreparedStatement prepareStatement(String sql) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
