/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Laporan;

import Login.Login;
import Navbar.Navbar;
import com.formdev.flatlaf.FlatClientProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import Navbar.koneksi;
import java.awt.Color;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
public class ptransaksi extends javax.swing.JPanel {
        private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs;
    public ptransaksi() throws SQLException {
         con = koneksi.Koneksi();
        initComponents();
        load_table();
       jTable1.getTableHeader().setBackground(new Color(63,148,105));
            
        jTable1.getTableHeader().setForeground(Color.white);
    }
   private void load_table() throws SQLException {
       jTable1.clearSelection();
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.getTableHeader().setResizingAllowed(false);
      DefaultTableModel model = new DefaultTableModel() {
          
            @Override
            public boolean isCellEditable(int row, int column) {
            
                return false;
           
        }
};
       
        
        model.addColumn("Kode Pengembalian");
        model.addColumn("Nama");
        model.addColumn("angkatan");
        model.addColumn("status siswa");
        model.addColumn("Judul");
        model.addColumn("Kategori");
        model.addColumn("Tanggal Kembali");
        model.addColumn("Status pengembalian");
        model.addColumn("Kondisi Buku");
        model.addColumn("Jumlah Kembali");
   
        try {
          pst = con.prepareStatement ("SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status , buku.judul_buku , buku.kategori , detail_pengembalian.tanggal , detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku where detail_pengembalian.tanggal = current_date"); 
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10)});
            }
            jTable1.setModel(model);
        } catch (Exception e) {
        }
   }
        private void searchList() throws SQLException {    
            jTable1.clearSelection();
         DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Kode Pengembalian");
        model.addColumn("Nama");
        model.addColumn("angkatan");
        model.addColumn("status siswa");
        model.addColumn("Judul");
        model.addColumn("Kategori");
        model.addColumn("Tanggal Kembali");
        model.addColumn("Status pengembalian");
        model.addColumn("Kondisi Buku");
        model.addColumn("Jumlah Kembali");
        try {
          pst = con.prepareStatement ("SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status , buku.judul_buku , buku.kategori , detail_pengembalian.tanggal , detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku where nama Like '%" + txtcari.getText() + "%' or status Like '%" + txtcari.getText() + "%' "
                    + "or judul_buku Like '%" + txtcari.getText() + "%'"); 
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10)});
            }
            jTable1.setModel(model);
        } catch (Exception e) {
             System.out.println("searchTable" + e);
        }
     

   }
       
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtcari = new javax.swing.JTextField();
        jToggleButton1 = new javax.swing.JToggleButton();
        jDate = new com.toedter.calendar.JDateChooser();
        jDate1 = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jPanel1AncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        jTable1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Kode Pengembalian", "Nama", "angkatan", "Status Siswa", "Judul", "Kategori ", "Tanggal kembali", "Status Pengembalian", "Kondisi Buku", "Jumlah kembali"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setResizable(false);
        }

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

        jToggleButton1.setBackground(new java.awt.Color(51, 153, 0));
        jToggleButton1.setForeground(new java.awt.Color(153, 204, 0));
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/printer-88 2.png"))); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 153, 51));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Cari");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setText("LAPORAN TRANSAKSI");

        jLabel1.setText("Tanggal Awal");

        jLabel3.setText("Tanggal Akhir");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1599, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jDate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(46, 46, 46)
                                .addComponent(jDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(jButton3)
                                .addGap(18, 18, 18)
                                .addComponent(jToggleButton1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(101, 101, 101)
                                .addComponent(jLabel3)
                                .addGap(190, 190, 190)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(12, 12, 12)
                        .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jToggleButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
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

             String key = txtcari.getText().trim();
        try {
            searchList();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_txtcariKeyReleased

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
          // TODO add your handling code here:
     int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah Anda sudah atur tanggal?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

if (dialogResult == JOptionPane.YES_OPTION) {
   
    try {
        this.disable();
        
        String reportPath = "src/Laporan/report1.jasper";
        Connection conn = koneksi.Koneksi();

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tgl1", jDate.getDate());
        parameters.put("tgl2", jDate1.getDate());
        JasperPrint print = JasperFillManager.fillReport(reportPath, parameters, conn);
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setVisible(true);

        conn.close(); // Menutup koneksi setelah selesai menggunakan

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error displaying report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
} else if (dialogResult == JOptionPane.NO_OPTION) {
    JOptionPane.showMessageDialog(null, "Anda harus atur tanggal dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
}

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       String tampilan1 = "yyyy-MM-dd";
SimpleDateFormat tgl1 = new SimpleDateFormat(tampilan1);
String tanggalawal = String.valueOf(tgl1.format(jDate.getDate()));

String tampilan2 = "yyyy-MM-dd";
SimpleDateFormat tgl2 = new SimpleDateFormat(tampilan2); // Fix: Use tampilan2 for tgl2
String tanggalakhir = String.valueOf(tgl2.format(jDate1.getDate()));
System.out.println(tanggalawal + tanggalakhir);
try {
    int No = 1;
   String sql ="SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status , buku.judul_buku , buku.kategori , detail_pengembalian.tanggal , detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku WHERE  tanggal BETWEEN '"+tanggalawal+"' AND '"+tanggalakhir+"';";
   java.sql.Connection conn = (Connection) koneksi.Koneksi();
    // Create a Statement
    java.sql.Statement stm = conn.createStatement();
   
    java.sql.ResultSet res = stm.executeQuery(sql);// Fix: Add WHERE clause
    DefaultTableModel table = (DefaultTableModel) jTable1.getModel();
    table.setRowCount(0);
    while (res.next()) {
       
       table.addRow(new Object[]{res.getString(1), res.getString(2), res.getInt(3), res.getString(4),res.getString(5),res.getString(6),
           res.getString(7),res.getString(8),res.getString(9),res.getString(10)});
}
} catch (Exception e) {
    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
}
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jPanel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel1AncestorAdded
          try {
            // TODO add your handling code here:
            load_table();
        } catch (SQLException ex) {
            Logger.getLogger(ptransaksi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jPanel1AncestorAdded


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private com.toedter.calendar.JDateChooser jDate;
    private com.toedter.calendar.JDateChooser jDate1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JTextField txtcari;
    // End of variables declaration//GEN-END:variables
}
