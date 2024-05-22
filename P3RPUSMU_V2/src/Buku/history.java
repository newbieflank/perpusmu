/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Buku;

import Navbar.koneksi;
import com.formdev.flatlaf.FlatClientProperties;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Septian Galoh P
 */
public class history extends javax.swing.JPanel {

    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;
    private int tgl, thn, bln;
    String thn_get, bln_get, hari_get, perstwa;

    private ArrayList<String> Tahun = new ArrayList<>();

    public history() {
        initComponents();
        con = koneksi.Koneksi();
        jPanel1.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        txtcari.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        txtcari.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari");
        JcomboTahun();
        loadTabel2();

        tabel.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel.getTableHeader().setForeground(Color.white);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void JcomboTahun() {
        tahun.removeAllItems();
        try {

            pst = con.prepareStatement("SELECT DISTINCT Year(tanggal) as tahun FROM data_history;");
            rs = pst.executeQuery();
            while (rs.next()) {
                String year = rs.getString("tahun");
                tahun.addItem(year); // Add each distinct year to the JComboBox
            }

            int itemCount = tahun.getItemCount();

            if (itemCount > 0) {
                tahun.setSelectedIndex(itemCount - 1); // Select the last item in the JComboBox
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getHari() {
        try {
            String selected = (String) hari.getSelectedItem();
            if (hari.getSelectedIndex() != 0) {
                tgl = Integer.parseInt(selected);
                hari_get = " AND day(tanggal) = " + tgl;
            } else {
                hari_get = ""; // Reset the condition if no day is selected
            }
        } catch (NumberFormatException e) {
            // Handle the case where the selected item cannot be parsed to an integer
            // You can log the error or display a message to the user
            System.out.println("Error parsing day: " + e.getMessage());
        }
    }

    private void getBulan() {
        try {
            if (bulan.getSelectedIndex() != 0) {
                bln = bulan.getSelectedIndex();
                bln_get = " AND month(tanggal) = " + bln;
            } else {
                bln_get = ""; // Reset the condition if no month is selected
            }
        } catch (Exception e) {
            // Handle any other exceptions that might occur
            System.out.println("Error in getBulan: " + e.getMessage());
        }
    }

    private void getTahun() {
        try {
            String selected = (String) tahun.getSelectedItem();
            thn = Integer.parseInt(selected);
            thn_get = "year(tanggal) = " + thn;
        } catch (NumberFormatException e) {
            // Handle the case where the selected item cannot be parsed to an integer
            // You can log the error or display a message to the user
            System.out.println("Error parsing year: " + e.getMessage());
        }
    }

    private void peristiwa() {
        try {
            if (J_peristiwa.getSelectedIndex() != 0) {
                String Pers = (String) J_peristiwa.getSelectedItem();
                perstwa = " AND peristiwa='" + Pers + "'";
            } else {
                perstwa = "";
            }
        } catch (Exception e) {
            System.out.println("error Peristiwa: " + e);
        }
    }

    private void loadTabel2() {
        tabel.clearSelection();
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getTableHeader().setResizingAllowed(false);
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        getTahun();
        getBulan();
        getHari();
        peristiwa();
        String query = "select * from data_history where ";

        try {
            if (bulan.getSelectedIndex() == 0 && hari.getSelectedIndex() == 0 && J_peristiwa.getSelectedIndex() == 0) {
                query = query + thn_get;
            } else {
                query = query + thn_get + bln_get + hari_get + perstwa;
            }

            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Add columns to the DefaultTableModel excluding 'id_history'
            for (int i = 1; i <= columnCount; i++) {
                if (!rsmd.getColumnName(i).equalsIgnoreCase("id history")) {
                    model.addColumn(rsmd.getColumnName(i));
                }
            }

            // Add rows to the DefaultTableModel
            while (rs.next()) {
                Object[] rowData = new Object[columnCount - 1];
                int columnIndex = 0;
                for (int i = 1; i <= columnCount; i++) {
                    if (!rsmd.getColumnName(i).equalsIgnoreCase("id history")) {
                        rowData[columnIndex++] = rs.getObject(i);
                    }
                }
                model.addRow(rowData);
            }
            tabel.setModel(model);
        } catch (Exception e) {
            System.out.println("loadTable" + e);
        }
    }

    private void cari() {
        tabel.clearSelection();
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getTableHeader().setResizingAllowed(false);
        DefaultTableModel model1 = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String key = txtcari.getText();
        String query = "select * from data_history where ";
        if (key.equals("")) {
            loadTabel2();
        } else {
            try {
                if (bulan.getSelectedIndex() == 0 && hari.getSelectedIndex() == 0) {
                    query = query + thn_get + " AND Judul buku Like '%" + key + "%'";
                } else {
                    query = query + thn_get + bln_get + hari_get + " AND Judul buku Like '%" + key + "%'";
                }
                pst = con.prepareStatement(query);
                rs = pst.executeQuery();
                ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    model1.addColumn(rsmd.getColumnName(i));
                }

                // Add rows to the DefaultTableModel
                while (rs.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = rs.getObject(i);
                    }
                    model1.addRow(rowData);
                }
                tabel.setModel(model1);
            } catch (Exception e) {
                System.out.println("data cari" + e);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        txtcari = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tahun = new javax.swing.JComboBox<>();
        hari = new javax.swing.JComboBox<>();
        bulan = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        J_peristiwa = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(204, 204, 204));
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                formAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabel.setModel(new javax.swing.table.DefaultTableModel(
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
        tabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabel.setGridColor(new java.awt.Color(255, 255, 255));
        tabel.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tabel);

        txtcari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtcariKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setText("HISTORY BUKU");

        tahun.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                tahunPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        hari.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", " " }));
        hari.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hariItemStateChanged(evt);
            }
        });

        bulan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" }));
        bulan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                bulanItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Tahun");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Bulan");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Tanggal");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Peristiwa");

        J_peristiwa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Masuk", "Hilang" }));
        J_peristiwa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                J_peristiwaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel3)
                        .addGap(5, 5, 5)
                        .addComponent(bulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(J_peristiwa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(347, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1140, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tahun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(J_peristiwa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(537, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(110, 110, 110)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtcariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcariKeyReleased
        cari();
    }//GEN-LAST:event_txtcariKeyReleased

    private void bulanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_bulanItemStateChanged
        // TODO add your handling code here:
        loadTabel2();
    }//GEN-LAST:event_bulanItemStateChanged

    private void hariItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_hariItemStateChanged
        // TODO add your handling code here:
        loadTabel2();
    }//GEN-LAST:event_hariItemStateChanged

    private void tahunPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_tahunPopupMenuWillBecomeInvisible
        // TODO add your handling code here:
        loadTabel2();
    }//GEN-LAST:event_tahunPopupMenuWillBecomeInvisible

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        // TODO add your handling code here:
        loadTabel2();
        JcomboTahun();
    }//GEN-LAST:event_formAncestorAdded

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        loadTabel2();
    }//GEN-LAST:event_formComponentShown

    private void J_peristiwaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_J_peristiwaItemStateChanged
        // TODO add your handling code here:
        loadTabel2();
    }//GEN-LAST:event_J_peristiwaItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> J_peristiwa;
    private javax.swing.JComboBox<String> bulan;
    private javax.swing.JComboBox<String> hari;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabel;
    private javax.swing.JComboBox<String> tahun;
    private javax.swing.JTextField txtcari;
    // End of variables declaration//GEN-END:variables
}
