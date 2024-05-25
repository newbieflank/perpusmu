/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Laporan;

import java.util.Date;
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
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ptransaksi extends javax.swing.JPanel {

    private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs;
    String FolderPath;

    public ptransaksi() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        load_table();
        jTable1.getTableHeader().setBackground(new Color(63, 148, 105));
txtcari.putClientProperty("JComponent.roundRect", true);
        jTable1.getTableHeader().setForeground(Color.white);
        txtcari.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "search");
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
            pst = con.prepareStatement("SELECT * FROM pengembalian_view;");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)});
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
            pst = con.prepareStatement("SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status , buku.judul_buku , buku.kategori , detail_pengembalian.tanggal , detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku where nama Like '%" + txtcari.getText() + "%' or status Like '%" + txtcari.getText() + "%' "
                    + "or judul_buku Like '%" + txtcari.getText() + "%'");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)});
            }
            jTable1.setModel(model);
        } catch (Exception e) {
            System.out.println("searchTable" + e);
        }

    }
    
    private void FolderExp() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            FolderPath = selectedFolder.getAbsolutePath();
            try {
                XLSX();
            } catch (Exception e) {
                System.out.println("Import " + e);
            }
        }
    }

    private void XLSX() {
        String tampilan1 = "yyyy-MM-dd";
        SimpleDateFormat tgl1 = new SimpleDateFormat(tampilan1);
        String tanggalawal = String.valueOf(tgl1.format(jDate.getDate()));

        String tampilan2 = "yyyy-MM-dd";
        SimpleDateFormat tgl2 = new SimpleDateFormat(tampilan2); // Fix: Use tampilan2 for tgl2
        String tanggalakhir = String.valueOf(tgl2.format(jDate1.getDate()));
        String exelpath = FolderPath + File.separator + generateUniqueFileName();
        String sql;
        if (jDate == null && jDate1 == null) {
            sql = "SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status ,"
                    + " buku.judul_buku , buku.kategori , detail_pengembalian.tanggal ,"
                    + " detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,"
                    + "detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON"
                    + " pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN"
                    + " anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku "
                    + "where detail_pengembalian.tanggal = current_date";
        } else if (jDate == null && jDate1 != null) {
            sql = "SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status ,"
                    + " buku.judul_buku , buku.kategori , detail_pengembalian.tanggal ,"
                    + " detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,"
                    + "detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON"
                    + " pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN"
                    + " anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku "
                    + "where detail_pengembalian.tanggal = '" + tanggalawal + "'";
        } else if (jDate != null && jDate1 == null) {
            sql = "SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status ,"
                    + " buku.judul_buku , buku.kategori , detail_pengembalian.tanggal ,"
                    + " detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,"
                    + "detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON"
                    + " pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN"
                    + " anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku "
                    + "where detail_pengembalian.tanggal = '" + tanggalakhir + "'";
        } else {
            sql = "SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status ,"
                    + " buku.judul_buku , buku.kategori , detail_pengembalian.tanggal ,"
                    + " detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,"
                    + "detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON"
                    + " pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN"
                    + " anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku "
                    + "where detail_pengembalian.tanggal BETWEEN '" + tanggalawal + "' AND '" + tanggalakhir + "'";
        }

        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            System.out.println(pst);
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("report");

                writeHeaderLine(sheet);

                writeDataLines(rs, workbook, sheet);

                FileOutputStream outputStream = new FileOutputStream(exelpath);
                workbook.write(outputStream);

                workbook.close();

                pst.close();
            }

        } catch (Exception e) {
            System.out.println("koneksi gagal " + e);
        }
    }

    private void writeHeaderLine(XSSFSheet sheet) {
        Row headerRow = sheet.createRow(0);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Kode Pengembalian");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Nama");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("Angkatan");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("Status");
        
        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("Judul Buku");
        
        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("Kategori");
        
        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("Tanggal");
        
        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("Status Pengembalian");
        
        headerCell = headerRow.createCell(8);
        headerCell.setCellValue("Kondisi Buku");
        
        headerCell = headerRow.createCell(9);
        headerCell.setCellValue("Jumlah Pengembalian");
    }

    private void writeDataLines(ResultSet rs, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        int rowCount = 1;

        while (rs.next()) {
            String NISN = rs.getString("kode_Pengembalian");
            String nama = rs.getString("nama");
            String angkatan = rs.getString("angkatan");
            String status = rs.getString("status");
            String judul_buku = rs.getString("judul_buku");
            String kategori = rs.getString("kategori");
            String tanggal = rs.getString("tanggal");
            String status_pengembalian = rs.getString("status_pengembalian");
            String kondisi_buku = rs.getString("kondisi_buku");
            String jumlah_pengembalian = rs.getString("jumlah_pengembalian");

            Row row = sheet.createRow(rowCount++);

            int ColumnCount = 0;
            Cell cell = row.createCell(ColumnCount++);
            cell.setCellValue(NISN);

            cell = row.createCell(ColumnCount++);
            cell.setCellValue(nama);

            cell = row.createCell(ColumnCount++);
            cell.setCellValue(angkatan);

            cell = row.createCell(ColumnCount++);
            cell.setCellValue(status);
            
            cell = row.createCell(ColumnCount++);
            cell.setCellValue(judul_buku);
            
            cell = row.createCell(ColumnCount++);
            cell.setCellValue(kategori);
            
            cell = row.createCell(ColumnCount++);
            cell.setCellValue(tanggal);
            
            cell = row.createCell(ColumnCount++);
            cell.setCellValue(status_pengembalian);
            
            cell = row.createCell(ColumnCount++);
            cell.setCellValue(kondisi_buku);
            
            cell = row.createCell(ColumnCount++);
            cell.setCellValue(jumlah_pengembalian);
        }
    }

    private String generateUniqueFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "report_" + timestamp + ".xlsx";
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
        Export = new javax.swing.JToggleButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jPanel1AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
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

        jDate.setDate(new Date());

        jDate1.setDate(new Date());

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

        Export.setBackground(new java.awt.Color(255, 153, 51));
        Export.setForeground(new java.awt.Color(153, 204, 0));
        Export.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_10/file-download 1.png"))); // NOI18N
        Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(25, 25, 25)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDate1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(49, 49, 49)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Export, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1628, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtcari, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Export, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
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
        int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah Anda akan mencetak ", "Konfirmasi", JOptionPane.YES_NO_OPTION);

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
            String sql = "SELECT pengembalian.kode_pengembalian , anggota.nama , anggota.angkatan , anggota.status , buku.judul_buku , buku.kategori , detail_pengembalian.tanggal , detail_pengembalian.status_pengembalian,detail_pengembalian.kondisi_buku,detail_pengembalian.jumlah_pengembalian FROM detail_pengembalian JOIN pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN anggota ON anggota.NISN = detail_pengembalian.NISN JOIN buku ON buku.No_buku = detail_pengembalian.No_buku WHERE  tanggal BETWEEN '" + tanggalawal + "' AND '" + tanggalakhir + "';";
            java.sql.Connection conn = (Connection) koneksi.Koneksi();
            // Create a Statement
            java.sql.Statement stm = conn.createStatement();

            java.sql.ResultSet res = stm.executeQuery(sql);// Fix: Add WHERE clause
            DefaultTableModel table = (DefaultTableModel) jTable1.getModel();
            table.setRowCount(0);
            while (res.next()) {

                table.addRow(new Object[]{res.getString(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5), res.getString(6),
                    res.getString(7), res.getString(8), res.getString(9), res.getString(10)});
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

    private void ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportActionPerformed
        // TODO add your handling code here:
        FolderExp();
    }//GEN-LAST:event_ExportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton Export;
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
