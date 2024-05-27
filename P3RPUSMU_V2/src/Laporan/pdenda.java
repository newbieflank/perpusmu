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
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

public class pdenda extends javax.swing.JPanel {
   private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs;
    String FolderPath;
    public pdenda() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        
        jTable2.getTableHeader().setBackground(new Color(63, 148, 105));
        jTable2.getTableHeader().setForeground(Color.white);
keyword.putClientProperty("JComponent.roundRect", true);
      keyword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "search");
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
            sql = "SELECT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran " +
                 "FROM denda " +
                 "JOIN anggota ON anggota.NISN = denda.NISN " +
                 "JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian " +
                 "WHERE detail_pengembalian.tanggal = current_date";
        } else if (jDate == null && jDate1 != null) {
            sql = "SELECT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran " +
                 "FROM denda " +
                 "JOIN anggota ON anggota.NISN = denda.NISN " +
                 "JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian " 
                    + "where detail_pengembalian.tanggal = '" + tanggalawal + "'";
        } else if (jDate != null && jDate1 == null) {
            sql = "SELECT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran " +
                 "FROM denda " +
                 "JOIN anggota ON anggota.NISN = denda.NISN " +
                 "JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian " 
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
   private void load_table() throws SQLException {
    jTable2.clearSelection();
    jTable2.getTableHeader().setResizingAllowed(false);
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            // All cells false
            return false;
        }
    };
    model.addColumn("Nama");
    model.addColumn("Jumlah Denda");
    model.addColumn("Status Denda");
    model.addColumn("Total Pembayaran");

    String sql = "SELECT * FROM denda_view";

    try (PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        Map<String, String[]> anggotaData = new HashMap<>();
        
        while (rs.next()) {
            String nama = rs.getString("nama");
            String jumlahDenda = rs.getString("jumlah_denda");
            String statusDenda = rs.getString("status_denda");
            String totalPembayaran = rs.getString("total_pembayaran");

            anggotaData.put(nama, new String[]{jumlahDenda, statusDenda, totalPembayaran});
        }
        
        for (Map.Entry<String, String[]> entry : anggotaData.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()[0], entry.getValue()[1], entry.getValue()[2]});
        }
        
        jTable2.setModel(model);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


//        private void searchList() throws SQLException {    
//            jTable2.clearSelection();
//         DefaultTableModel model = new DefaultTableModel() {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//          model.addColumn("Nama");
//        model.addColumn("Jumlah Denda");
//        model.addColumn("Status Denda");
//        model.addColumn("Total Dembayaran");
   private void searchList(String keyword) {
    jTable2.clearSelection();
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            // All cells false
            return false;
        }
    };

    String sql = "SELECT * FROM denda_view WHERE `nama` LIKE ?";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, "%" + keyword + "%");

        try (ResultSet rs = pst.executeQuery()) {
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(rsmd.getColumnName(i));
            }

            Set<String> uniqueNames = new HashSet<>();

            // Add rows to the DefaultTableModel
            while (rs.next()) {
                String name = rs.getString("nama");
                if (!uniqueNames.contains(name)) {
                    uniqueNames.add(name);
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = rs.getObject(i);
                    }
                    model.addRow(rowData);
                }
            }
            jTable2.setModel(model);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        keyword = new javax.swing.JTextField();
        jToggleButton1 = new javax.swing.JToggleButton();
        jDate = new com.toedter.calendar.JDateChooser();
        jDate1 = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jToggleButton2 = new javax.swing.JToggleButton();

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

        keyword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keywordActionPerformed(evt);
            }
        });
        keyword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                keywordKeyReleased(evt);
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("LAPORAN DENDA");

        jLabel2.setText("Tanggal Awal");

        jLabel3.setText("Tanggal Akhir");

        jToggleButton2.setBackground(new java.awt.Color(255, 153, 51));
        jToggleButton2.setForeground(new java.awt.Color(255, 255, 102));
        jToggleButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_10/file-download 1.png"))); // NOI18N
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1392, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jDate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDate1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel2)
                        .addGap(82, 82, 82)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(keyword, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDate1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton3)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
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

    private void keywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keywordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_keywordActionPerformed

    private void keywordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keywordKeyReleased
//      
         String key = keyword.getText().trim();
        try {
            searchList(key);
        } catch (Exception e) {
        }
    }//GEN-LAST:event_keywordKeyReleased

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
           int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah Anda akan mencetak", "Konfirmasi", JOptionPane.YES_NO_OPTION);

if (dialogResult == JOptionPane.YES_OPTION) {
   
    try {
        this.disable();
        
        String reportPath = "src/Laporan/report3.jasper";
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
//        String tampilan1 = "yyyy-MM-dd";
//        SimpleDateFormat tgl1 = new SimpleDateFormat(tampilan1);
//        String tanggalawal = String.valueOf(tgl1.format(jDate.getDate()));
//
//        String tampilan2 = "yyyy-MM-dd";
//        SimpleDateFormat tgl2 = new SimpleDateFormat(tampilan2); // Fix: Use tampilan2 for tgl2
//        String tanggalakhir = String.valueOf(tgl2.format(jDate1.getDate()));
//        System.out.println(tanggalawal + tanggalakhir);
//        try {
//            int No = 1;
//            String sql = "SELECT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran, detail_pengembalian.tanggal FROM denda JOIN anggota ON anggota.NISN = denda.NISN JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian WHERE detail_pengembalian.tanggal BETWEEN '" + tanggalawal + "' AND '" + tanggalakhir + "';";
//            java.sql.Connection conn = (Connection) koneksi.Koneksi();
//            // Create a Statement
//            java.sql.Statement stm = conn.createStatement();
//
//            java.sql.ResultSet res = stm.executeQuery(sql);// Fix: Add WHERE clause
//            DefaultTableModel table = (DefaultTableModel) jTable2.getModel();
//            table.setRowCount(0);
//            while (res.next()) {
//
//                table.addRow(new Object[]{res.getString(1), res.getString(2), res.getString(3), res.getString(4)});
//            }
//        } catch (Exception e) {
//            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
//        }
          String tampilan1 = "yyyy-MM-dd";
        SimpleDateFormat tgl1 = new SimpleDateFormat(tampilan1);
        String tanggalawal = String.valueOf(tgl1.format(jDate.getDate()));

        String tampilan2 = "yyyy-MM-dd";
        SimpleDateFormat tgl2 = new SimpleDateFormat(tampilan2); // Fix: Use tampilan2 for tgl2
        String tanggalakhir = String.valueOf(tgl2.format(jDate1.getDate()));
        System.out.println(tanggalawal + tanggalakhir);
               try {
            String sql = "SELECT DISTINCT anggota.nama, denda.jumlah_denda, denda.status_denda, denda.total_pembayaran, detail_pengembalian.tanggal " +
                         "FROM denda " +
                         "JOIN anggota ON anggota.NISN = denda.NISN " +
                         "JOIN detail_pengembalian ON detail_pengembalian.kode_pengembalian = denda.kode_pengembalian " +
                         "WHERE detail_pengembalian.tanggal BETWEEN '" + tanggalawal + "' AND '" + tanggalakhir + "';";

            Connection conn = (Connection) koneksi.Koneksi();
                   Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery(sql);

            DefaultTableModel table = (DefaultTableModel) jTable2.getModel();
            table.setRowCount(0); // Clear the table before adding new rows

            while (res.next()) {
                table.addRow(new Object[]{
                    res.getString("nama"),
                    res.getInt("jumlah_denda"),
                    res.getString("status_denda"),
                    res.getInt("total_pembayaran"),
                    res.getString("tanggal")
                });
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

    private void jToggleButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton2ActionPerformed
        FolderExp();
    }//GEN-LAST:event_jToggleButton2ActionPerformed


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
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JTextField keyword;
    // End of variables declaration//GEN-END:variables
}
