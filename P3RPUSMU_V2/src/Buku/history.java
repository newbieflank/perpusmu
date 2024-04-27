/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Buku;

import Login.Login;
import Navbar.Navbar;
import com.formdev.flatlaf.FlatClientProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import Navbar.koneksi;
import com.mysql.cj.xdevapi.Statement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.sql.DriverManager;
import java.util.Date;

public class history extends javax.swing.JPanel {

    private JTable table;
    private DefaultTableModel model;
        

    public history() {
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Id");
        columnNames.add("Kode Buku");
        columnNames.add("Judul Buku");
        columnNames.add("Kategori");
        columnNames.add("Kondisi");
        columnNames.add("Harga");
        columnNames.add("Tgl Hilang");
        columnNames.add("Peristiwa");
        columnNames.add("Tgl Masuk");
        columnNames.add("Keterangan");

        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        table1();
    }

    private void table1() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpusmu", "root", "");
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT history.id_history, history.tgl_masuk, history.peristiwa, history.keterangan, "
                     + "buku.kode_buku, buku.judul_buku, buku.kategori, buku.kondisi_buku, buku.harga, detail_pengembalian.tanggal, "
                     + "history.No_buku "
                     + "FROM history "
                     + "JOIN buku ON history.No_buku = buku.No_buku "
                     + "LEFT JOIN detail_pengembalian ON buku.No_buku = detail_pengembalian.No_buku AND buku.kondisi = 'hilang'")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id_history"));
                row.add(rs.getString("kode_buku"));
                row.add(rs.getString("judul_buku"));
                row.add(rs.getString("kategori"));
                row.add(rs.getString("kondisi_buku"));
                row.add(rs.getInt("harga"));
                row.add(rs.getDate("tgl_hilang"));
                row.add(rs.getString("peristiwa"));
                row.add(rs.getDate("tgl_masuk"));
                row.add(rs.getString("keterangan"));

                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(String kodeBuku, String judulBuku, String kategori, String kondisi, int harga) {
        // Menambahkan buku ke tabel buku
        // ...

        // Mendapatkan No_buku yang baru dibuat
        

        // Menambahkan data ke tabel history
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpusmu", "root", "");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO history (No_buku, tgl_masuk, peristiwa, keterangan) VALUES (?, ?, ?, ?)")) {
            int No_buku = stmt.getGeneratedKeys().getInt(1);
            stmt.setDate(2, (java.sql.Date) new Date());
            stmt.setString(3, "Buku Baru Ditambahkan");
            stmt.setString(4, "Buku " + judulBuku + " (" + kodeBuku + ") ditambahkan ke database.");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Menampilkan data di tabel history
        table1();
    
    // ... (lanjutkan dengan method lain yang dibutuhkan)
}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        txtcari = new javax.swing.JTextField();
        btncetak = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        table1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Kode Buku", "Judul Buku", "Kategori", "Kondisi ", "Harga", "Tgl Hilang", "Peristiwa", "Tgl Masuk", "Keterangan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        table1.setGridColor(new java.awt.Color(255, 255, 255));
        table1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table1);

        txtcari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcariActionPerformed(evt);
            }
        });
        txtcari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtcariKeyReleased(evt);
            }
        });

        btncetak.setBackground(new java.awt.Color(51, 153, 0));
        btncetak.setForeground(new java.awt.Color(153, 204, 0));
        btncetak.setIcon(new javax.swing.ImageIcon("D:\\perpusmu\\P3RPUSMU_V2\\src\\img_15\\image_button\\printer-88 2_1.png")); // NOI18N
        btncetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncetakActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setText("HISTORY BUKU");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1093, Short.MAX_VALUE)
                        .addComponent(btncetak)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(12, 12, 12)
                        .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btncetak)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addGap(173, 173, 173))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtcariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcariActionPerformed

    private void txtcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcariKeyReleased



    }//GEN-LAST:event_txtcariKeyReleased

    private void btncetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncetakActionPerformed

    }//GEN-LAST:event_btncetakActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btncetak;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table1;
    private javax.swing.JTextField txtcari;
    // End of variables declaration//GEN-END:variables
}

