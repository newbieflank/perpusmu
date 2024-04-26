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

public class pdenda extends javax.swing.JPanel {
   private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs;
    public pdenda() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        load_table();
        jTable2.getTableHeader().setBackground(new Color(63, 148, 105));
        jTable2.getTableHeader().setForeground(Color.white);

    }

    private void load_table() throws SQLException {
           jTable2.clearSelection();
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.getTableHeader().setResizingAllowed(false);
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        model.addColumn("Nama");
        model.addColumn("Jumlah Denda");
        model.addColumn("Status Denda");
        model.addColumn("Total Dembayaran");

        try {
             pst = con.prepareStatement ( "SELECT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran, detail_pengembalian.tanggal FROM denda JOIN anggota ON anggota.NISN = denda.NISN JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian;");
           rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            } 
            jTable2.setModel(model);
        } catch (Exception e) {
        }
   }

        private void searchList() throws SQLException {    
            jTable2.clearSelection();
         DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
          model.addColumn("Nama");
        model.addColumn("Jumlah Denda");
        model.addColumn("Status Denda");
        model.addColumn("Total Dembayaran");
        try {
             pst = con.prepareStatement ( "SELECT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran, detail_pengembalian.tanggal FROM denda JOIN anggota ON anggota.NISN = denda.NISN JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian where nama Like '%" + txtcari.getText() + "%' or status_denda Like '%" + txtcari.getText() + "%'");
           rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            } 
            jTable2.setModel(model);
        } catch (Exception e) {
              System.out.println("searchTable" + e);
        }
        }
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        txtcari = new javax.swing.JTextField();
        jToggleButton1 = new javax.swing.JToggleButton();
        jDate = new com.toedter.calendar.JDateChooser();
        jDate1 = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
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

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nama", "Jumlah Denda", "Status Denda", "Jumlah pembayaran"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable2);

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
        jToggleButton1.setForeground(new java.awt.Color(255, 255, 102));
        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/printer-88 2.png"))); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Cari");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("LAPORAN DENDA");

        jLabel2.setText("Tanggal Awal");

        jLabel3.setText("Tanggal Awal");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jDate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addGap(18, 18, 18)
                .addComponent(jToggleButton1)
                .addGap(15, 15, 15))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1392, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(61, 61, 61)
                .addComponent(jLabel3)
                .addGap(208, 208, 208))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addGap(207, 207, 207))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtcariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcariActionPerformed

    private void txtcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcariKeyReleased
//      
         String key = txtcari.getText().trim();
        try {
            searchList();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_txtcariKeyReleased

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
           int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah Anda sudah atur tanggal?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

if (dialogResult == JOptionPane.YES_OPTION) {
   
    try {
        this.disable();
        
        String reportPath = "src/Laporan/report2.jasper";
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
            String sql = "SELECT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran, detail_pengembalian.tanggal FROM denda JOIN anggota ON anggota.NISN = denda.NISN JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian WHERE detail_pengembalian.tanggal BETWEEN '" + tanggalawal + "' AND '" + tanggalakhir + "';";
            java.sql.Connection conn = (Connection) koneksi.Koneksi();
            // Create a Statement
            java.sql.Statement stm = conn.createStatement();

            java.sql.ResultSet res = stm.executeQuery(sql);// Fix: Add WHERE clause
            DefaultTableModel table = (DefaultTableModel) jTable2.getModel();
            table.setRowCount(0);
            while (res.next()) {

                table.addRow(new Object[]{res.getString(1), res.getString(2), res.getString(3), res.getString(4)});
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
            Logger.getLogger(pdenda.class.getName()).log(Level.SEVERE, null, ex);
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
    private javax.swing.JTable jTable2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JTextField txtcari;
    // End of variables declaration//GEN-END:variables
}
