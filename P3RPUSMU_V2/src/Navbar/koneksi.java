/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Navbar;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Septian Galoh P
 */
public class koneksi {

    private static Connection con;

    public static Connection Koneksi() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpusmu", "root", "");

        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }
}
