/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author User
 */
public class conek {
    
    private static Connection mysqlconfig;
    public static Connection configDB() throws SQLException {
    try{
    String url = "jdbc:mysql://localhost:3306/perpusmu";
    String user = "root";
    String pass = "";
    DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
    mysqlconfig = DriverManager.getConnection(url, user, pass);
    }catch (Exception e){
    System.err.println("koneksi gagal" + e.getMessage());
    }
    return mysqlconfig;
    }

    static PreparedStatement prepareStatement(String sql) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    
}

