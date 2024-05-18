/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Anggota;

import com.formdev.flatlaf.FlatClientProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import Navbar.koneksi;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Septian Galoh P
 */
public class Anggota extends javax.swing.JPanel {

    /**
     * Creates new form Anggota
     */
    private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs, rs1;
    private String nisn, kelamin, jurusan, FileExcelPath;

    public Anggota() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        JCombo();
        loadTable2();
        Jdialog();
        jPanel1.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        popup.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        popup2.putClientProperty(FlatClientProperties.STYLE, "arc:20");
        f_gender.putClientProperty(FlatClientProperties.STYLE, "arc:20");
        J_jurusan.putClientProperty(FlatClientProperties.STYLE, "arc:20");
        search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Anggota");

        UIManager.put("Button.arc", 15);
        search.putClientProperty("JComponent.roundRect", true);
        tabel.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel.getTableHeader().setForeground(Color.white);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        jDialog1.setSize(658, 395);
    }

    private void JCombo() {
        J_jurusan.removeAllItems();
        try {
            pst = con.prepareStatement("select jurusan from anggota");
            rs = pst.executeQuery();

            J_jurusan.addItem("Semua");
            f_gender.setSelectedItem("Semua");
            while (rs.next()) {
                String jurs = rs.getString("jurusan");
                J_jurusan.addItem(jurs);
            }

        } catch (Exception e) {
            System.out.println("Jcombo" + e);
        }
    }

    private void Jdialog() {
        jDialog1.setLocationRelativeTo(null);
        jDialog1.setBackground(Color.white);
        jDialog1.getRootPane().setOpaque(false);
        jDialog1.getContentPane().setBackground(new Color(0, 0, 0, 0));
        jDialog1.setBackground(new Color(0, 0, 0, 0));
    }

    private void getJK() {
        if (f_gender.getSelectedIndex() != 0) {
            kelamin = (String) f_gender.getSelectedItem();
        } else {
            kelamin = "";
        }
    }

    private void getJR() {
        if (J_jurusan.getSelectedIndex() != 0) {
            jurusan = (String) J_jurusan.getSelectedItem();
        } else {
            jurusan = "";
        }
    }

    private void loadTable2() throws SQLException {
        tabel.clearSelection();
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getTableHeader().setResizingAllowed(false);
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        getJK();
        getJR();
        String sql;
        String cari = search.getText();

        if (!cari.equals("")) {
            searchList();
        } else {

            if (kelamin.equals("") && jurusan.equals("")) {
                sql = "select * from view_anggota";
            } else if (!kelamin.equals("") && jurusan.equals("")) {
                sql = "select * from view_anggota where jenis_kelamin = '" + kelamin + "'";
            } else if (kelamin.equals("") && !jurusan.equals("")) {
                sql = "select * from view_anggota where jurusan= '" + jurusan + "'";
            } else {
                sql = "select * from view_anggota where jenis_kelamin= '" + kelamin + "' and jurusan= '" + jurusan + "'";
            }
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(rsmd.getColumnName(i));
                }

                // Add rows to the DefaultTableModel
                while (rs.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = rs.getObject(i);
                    }
                    model.addRow(rowData);
                }
                tabel.setModel(model);
            } catch (Exception e) {
                System.out.println("loadTable = " + e);
            }

        }

    }

    private void searchList() throws SQLException {
        tabel.clearSelection();
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String cari = search.getText();
        
        
        if (!cari.equals("")) {
            String query;
            try {
                if (kelamin.equals("") && jurusan.equals("")) {
                    query = "select * from view_anggota where nama Like '%" + cari + "%'";
                } else if (!kelamin.equals("") && jurusan.equals("")) {
                    query = "select * from view_anggota where jenis_kelamin = '" + kelamin + "' and nama Like '%" + cari + "%'";
                } else if (kelamin.equals("") && !jurusan.equals("")) {
                    query = "select * from view_anggota where jurusan= '" + jurusan + "' and nama Like '%" + cari + "%'";
                } else {
                    query = "select * from view_anggota where jenis_kelamin= '" + kelamin + "' and jurusan= '" + jurusan + "' and nama Like '%" + cari + "%'";
                }
                pst = con.prepareStatement(query);
                rs = pst.executeQuery();
                ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(rsmd.getColumnName(i));
                }

                // Add rows to the DefaultTableModel
                while (rs.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = rs.getObject(i);
                    }
                    model.addRow(rowData);
                }
                tabel.setModel(model);
            } catch (Exception e) {
                System.out.println("data cari" + e);
            }
        } else {
            try {
                loadTable2();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    
    
    private void changeFolderPath() {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        int result = folderChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            FileExcelPath = selectedFolder.getAbsolutePath();
            try {
                importData();
            } catch (Exception e) {
                System.out.println("Import " + e);
            }
        }
    }

    private void importData() throws IOException, SQLException {
        String excelPath = FileExcelPath;

        try (FileInputStream file = new FileInputStream(excelPath)) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }

                int column1 = (int) row.getCell(0).getNumericCellValue();
                String column2 = row.getCell(1).getStringCellValue();
                String column3 = row.getCell(2).getStringCellValue();
                String column4 = row.getCell(3).getStringCellValue();
                int column5 = (int) row.getCell(4).getNumericCellValue();
                String column6 = row.getCell(5).getStringCellValue();
                // Read other columns as needed

                String sql = "INSERT INTO anggota (NISN, nama, jenis_kelamin, jurusan, angkatan, status) Values (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pst = con.prepareStatement(sql)) {
                    pst.setString(1, Integer.toString(column1));
                    pst.setString(2, column2);
                    pst.setString(3, column3);
                    pst.setString(4, column4);
                    pst.setInt(5, column5);
                    pst.setString(6, column6);
                    // Set other columns as needed
                    System.out.println(pst);

                    pst.executeUpdate();
                }
            }
        }
        loadTable2();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        popup = new javax.swing.JPanel();
        popup2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        dial_nisn = new javax.swing.JTextField();
        dial_nama = new javax.swing.JTextField();
        dial_angkatan = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dial_simpan = new javax.swing.JButton();
        dial_batal = new javax.swing.JButton();
        dial_jenis = new javax.swing.JComboBox<>();
        dial_status = new javax.swing.JComboBox<>();
        dial_jurusan = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        f_gender = new javax.swing.JComboBox<>();
        J_jurusan = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ImportData = new javax.swing.JButton();

        jDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialog1.setBackground(new java.awt.Color(255, 255, 255));
        jDialog1.setIconImage(null);
        jDialog1.setMaximumSize(new java.awt.Dimension(658, 395));
        jDialog1.setMinimumSize(new java.awt.Dimension(658, 395));
        jDialog1.setUndecorated(true);
        jDialog1.setResizable(false);
        jDialog1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jDialog1KeyPressed(evt);
            }
        });

        popup.setBackground(new java.awt.Color(204, 204, 204));

        popup2.setBackground(new java.awt.Color(63, 148, 105));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("ANGGOTA");

        javax.swing.GroupLayout popup2Layout = new javax.swing.GroupLayout(popup2);
        popup2.setLayout(popup2Layout);
        popup2Layout.setHorizontalGroup(
            popup2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(popup2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        popup2Layout.setVerticalGroup(
            popup2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(popup2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        dial_nisn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_nisnKeyTyped(evt);
            }
        });

        dial_angkatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dial_angkatanKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_angkatanKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Nama");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("NISN");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Jenis Kelamin");

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Jurusan");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Angkatan");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Status");

        dial_simpan.setBackground(new java.awt.Color(63, 148, 105));
        dial_simpan.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        dial_simpan.setForeground(new java.awt.Color(255, 255, 255));
        dial_simpan.setText("SIMPAN");
        dial_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_simpanActionPerformed(evt);
            }
        });

        dial_batal.setBackground(new java.awt.Color(255, 0, 0));
        dial_batal.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        dial_batal.setForeground(new java.awt.Color(255, 255, 255));
        dial_batal.setText("BATAL");
        dial_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_batalActionPerformed(evt);
            }
        });

        dial_jenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Laki-Laki", "Perempuan" }));

        dial_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aktif", "Lulus" }));

        javax.swing.GroupLayout popupLayout = new javax.swing.GroupLayout(popup);
        popup.setLayout(popupLayout);
        popupLayout.setHorizontalGroup(
            popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(popupLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(popupLayout.createSequentialGroup()
                        .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(popupLayout.createSequentialGroup()
                                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dial_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dial_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dial_nisn, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(84, 84, 84)
                                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dial_angkatan, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dial_status, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dial_jurusan, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(popupLayout.createSequentialGroup()
                                .addGap(200, 200, 200)
                                .addComponent(dial_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(51, 51, 51)
                                .addComponent(dial_simpan)))
                        .addContainerGap(21, Short.MAX_VALUE))
                    .addGroup(popupLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(203, 203, 203))
                    .addGroup(popupLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addGap(227, 227, 227))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, popupLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(214, 214, 214))))
            .addComponent(popup2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        popupLayout.setVerticalGroup(
            popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(popupLayout.createSequentialGroup()
                .addComponent(popup2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_nisn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_jurusan, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_angkatan, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_status, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_jenis, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_simpan)
                    .addComponent(dial_batal))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(popup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(popup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        popup.getAccessibleContext().setAccessibleName("");

        jDialog1.getAccessibleContext().setAccessibleParent(null);

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
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setToolTipText("");
        jPanel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jPanel1AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("LIST DATA ANGGOTA");

        search.setToolTipText("");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeyReleased(evt);
            }
        });

        tabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "NISN", "Nama", "Jenis Kelamin", "Jurusan", "Angkatan", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel.setGridColor(new java.awt.Color(0, 0, 0));
        tabel.setShowGrid(true);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel);
        if (tabel.getColumnModel().getColumnCount() > 0) {
            tabel.getColumnModel().getColumn(0).setResizable(false);
            tabel.getColumnModel().getColumn(0).setHeaderValue("NISN");
            tabel.getColumnModel().getColumn(1).setResizable(false);
            tabel.getColumnModel().getColumn(1).setHeaderValue("Nama");
            tabel.getColumnModel().getColumn(2).setResizable(false);
            tabel.getColumnModel().getColumn(2).setHeaderValue("Jenis Kelamin");
            tabel.getColumnModel().getColumn(3).setResizable(false);
            tabel.getColumnModel().getColumn(3).setHeaderValue("Jurusan");
            tabel.getColumnModel().getColumn(4).setResizable(false);
            tabel.getColumnModel().getColumn(4).setHeaderValue("Angkatan");
            tabel.getColumnModel().getColumn(5).setResizable(false);
            tabel.getColumnModel().getColumn(5).setHeaderValue("Status");
        }

        addButton.setBackground(new java.awt.Color(63, 148, 105));
        addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/+.png"))); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editButton.setBackground(new java.awt.Color(255, 227, 130));
        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/edit_1160515 3.png"))); // NOI18N
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        deleteButton.setBackground(new java.awt.Color(255, 145, 66));
        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/trash-can_7343703 2.png"))); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        f_gender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Semua", "Laki-Laki", "Perempuan" }));
        f_gender.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                f_genderItemStateChanged(evt);
            }
        });

        J_jurusan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        J_jurusan.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                J_jurusanPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Jurusan :");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Jenis Kelamin :");

        ImportData.setBackground(new java.awt.Color(255, 227, 130));
        ImportData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/edit_1160515 3.png"))); // NOI18N
        ImportData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(J_jurusan, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(f_gender, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(ImportData, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1313, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel9)
                                .addComponent(J_jurusan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10)
                                .addComponent(f_gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(9, 9, 9)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ImportData, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        search.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
        dial_nisn.setText(null);
        dial_nama.setText(null);
        dial_jenis.setSelectedItem(this);
        dial_jurusan.setText(null);
        dial_angkatan.setText(null);
        dial_status.setSelectedItem(this);

        dial_nisn.setEnabled(true);
        jDialog1.setVisible(true);
    }//GEN-LAST:event_addButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
        try {
            pst = con.prepareStatement("select * from view_anggota where NISN = " + nisn);
            rs = pst.executeQuery();
            rs.next();
            String ag_nama = rs.getString("nama");
            String ag_Jenis = rs.getString("jenis_kelamin");
            String ag_jurusan = rs.getString("jurusan");
            Date angkatan = rs.getDate("angkatan");
            Format formater = new SimpleDateFormat("yyyy");
            String ag_angkatan = formater.format(angkatan);
            String ag_status = rs.getString("status");

            dial_nisn.setText(nisn);
            dial_nama.setText(ag_nama);
            dial_jenis.setSelectedItem(ag_Jenis);
            dial_jurusan.setText(ag_jurusan);
            dial_angkatan.setText(ag_angkatan);
            dial_status.setSelectedItem(ag_status);
            dial_nisn.enable(false);

            jDialog1.setVisible(true);
        } catch (Exception e) {
            System.out.println("click" + e);
            JOptionPane.showMessageDialog(popup, "Pilih Dahulu Data Yang Akan Di Ubah");
            jDialog1.setVisible(false);
        }

    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        if (nisn != null) {
            int result = JOptionPane.showConfirmDialog(jDialog1, "Apakah Anda Yakin Ingin Menghapus?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    pst = con.prepareStatement("select peminjaman.NISN, detail_peminjaman.jumlah_peminjaman from "
                            + "peminjaman Join detail_peminjaman on detail_peminjaman.kode_peminjaman = peminjaman.kode_peminjaman "
                            + "where peminjaman.NISN =  " + nisn);
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(jDialog1, "Anggota Sedang Meminjam Buku");
                    } else {
                        try {
                            pst = con.prepareStatement("select jumlah_denda, status_denda from denda where NISN = " + nisn);
                            rs = pst.executeQuery();
                            rs.next();
                            String SD = rs.getString("status_denda");
                            int jumlha = rs.getInt("jumlah_denda");
                            if (SD.equalsIgnoreCase("Belum Lunas")) {
                                JOptionPane.showMessageDialog(jDialog1, "Anggota Masih Memiliki Tanggungan Denda Sebesar: Rp." + jumlha);
                            } else {
                                try {
                                    pst = con.prepareStatement("delete from anggota where NISN = " + nisn);
                                    pst.execute();
                                    loadTable2();
                                    JOptionPane.showMessageDialog(jDialog1, "Data Berhasil di hapus");
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(jDialog1, "Data Gagal di hapus");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("SD" + e);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("data hapus" + e);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(jDialog1, "Pilih Dahulu Data Yang Ingin di Hapus");
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void jDialog1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDialog1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            jDialog1.setVisible(true);
        }
    }//GEN-LAST:event_jDialog1KeyPressed

    private void jPanel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel1AncestorAdded
        try {
            // TODO add your handling code here:
            loadTable2();
        } catch (SQLException ex) {
            Logger.getLogger(Anggota.class.getName()).log(Level.SEVERE, null, ex);
        }
        Jdialog();
    }//GEN-LAST:event_jPanel1AncestorAdded

    private void dial_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_batalActionPerformed
        // TODO add your handling code here:
        dial_nisn.setText(null);
        dial_nama.setText(null);
        dial_jenis.setSelectedItem(this);
        dial_jurusan.setText(null);
        dial_angkatan.setText(null);
        dial_status.setSelectedItem(this);

        nisn = null;
        jDialog1.dispose();
    }//GEN-LAST:event_dial_batalActionPerformed

    private void tabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelMouseClicked
        // TODO add your handling code here:
        int index = tabel.getSelectedRow();
        TableModel model = tabel.getModel();
        nisn = model.getValueAt(index, 0).toString();
    }//GEN-LAST:event_tabelMouseClicked

    private void dial_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_simpanActionPerformed
        // TODO add your handling code here:
        String NISN = dial_nisn.getText();
        String NAMA = dial_nama.getText();
        Object JENIS = dial_jenis.getSelectedItem();
        String JURUSAN = dial_jurusan.getText();
        String ANGKATAN = dial_angkatan.getText();
        Object STATUS = dial_status.getSelectedItem();

        if (NISN.equals("") || NAMA.equals("") || JURUSAN.equals("") || ANGKATAN.equals("")) {
            JOptionPane.showMessageDialog(jDialog1, "Anda Harus Mengisi Semua Data Terlebih Dahulu");
        } else {
            int result = JOptionPane.showConfirmDialog(jDialog1, "Apakah Anda Ingin Menyimpan?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    pst = con.prepareStatement("select * from anggota where NISN = " + NISN);
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        pst = con.prepareStatement("update anggota set nama='" + NAMA + "', jenis_kelamin='" + JENIS + "', "
                                + "jurusan='" + JURUSAN + "', angkatan=" + ANGKATAN + ", status='" + STATUS + "' where NISN=" + NISN);
                        pst.execute();
                        JOptionPane.showMessageDialog(jDialog1, "Data berhasil di edit");
                    } else {
                        try {
                            pst1 = con.prepareStatement("Insert into anggota (NISN, nama, jenis_kelamin, jurusan, angkatan, status)"
                                    + " values ('" + NISN + "','" + NAMA + "','" + JENIS + "','" + JURUSAN + "'," + ANGKATAN + " ,'" + STATUS + "')");
                            pst1.execute();
                            JOptionPane.showMessageDialog(jDialog1, "Data berhasil Di Tambahkan");
                        } catch (Exception r) {
                            System.out.println("insert simpan " + r);
                        }
                    }

                    loadTable2();
                } catch (Exception e) {
                    System.out.println("nisn anggota" + e);
                }
                nisn = null;
                jDialog1.dispose();
            }
        }
    }//GEN-LAST:event_dial_simpanActionPerformed

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        try {
            // TODO add your handling code here:
            loadTable2();
            JCombo();
        } catch (SQLException ex) {
            Logger.getLogger(Anggota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formAncestorAdded

    private void searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                searchList();
            } catch (SQLException ex) {
                Logger.getLogger(Anggota.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_searchKeyPressed

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
        // TODO add your handling code here:
        String key = search.getText().trim();
        try {
            searchList();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_searchKeyReleased

    private void dial_angkatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_angkatanKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_angkatanKeyReleased

    private void dial_angkatanKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_angkatanKeyTyped
        // TODO add your handling code here:
        String angkat = dial_angkatan.getText();
        int angka = angkat.length();
        if (!(Character.isAlphabetic(evt.getKeyChar()) || (Character.isWhitespace(evt.getKeyChar())))) {
            if (angka >= 4) {
                JOptionPane.showMessageDialog(jDialog1, "Hanya Bisa di Isi 4 Karakter");
                evt.consume();
            }
        } else if (Character.isWhitespace(evt.getKeyChar())) {
            JOptionPane.showMessageDialog(jDialog1, "Tidak Boleh ada Spasi");
            evt.consume();
        } else {
            JOptionPane.showMessageDialog(jDialog1, "Hanya Bisa di isi dengan Angka");
            evt.consume();
        }
    }//GEN-LAST:event_dial_angkatanKeyTyped

    private void dial_nisnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_nisnKeyTyped
        // TODO add your handling code here:
        String batas = dial_nisn.getText();
        int batasan = batas.length();
        if (!(Character.isAlphabetic(evt.getKeyChar()) || (Character.isWhitespace(evt.getKeyChar())))) {
            if (batasan >= 10) {
                JOptionPane.showMessageDialog(jDialog1, "Hanya Bisa di 10 Karakter");
                evt.consume();
            }
        } else if (Character.isWhitespace(evt.getKeyChar())) {
            JOptionPane.showMessageDialog(jDialog1, "Tidak Boleh ada Spasi");
            evt.consume();
        } else {
            JOptionPane.showMessageDialog(jDialog1, "Hanya Bisa di isi dengan Angka");
            evt.consume();
        }
    }//GEN-LAST:event_dial_nisnKeyTyped

    private void f_genderItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_f_genderItemStateChanged
        try {
            // TODO add your handling code here:
            loadTable2();
        } catch (SQLException ex) {
            Logger.getLogger(Anggota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_f_genderItemStateChanged

    private void J_jurusanPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_J_jurusanPopupMenuWillBecomeInvisible
        try {
            // TODO add your handling code here:
            loadTable2();
        } catch (SQLException ex) {
            Logger.getLogger(Anggota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_J_jurusanPopupMenuWillBecomeInvisible

    private void ImportDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportDataActionPerformed
        // TODO add your handling code here:
        changeFolderPath();
    }//GEN-LAST:event_ImportDataActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ImportData;
    private javax.swing.JComboBox<String> J_jurusan;
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField dial_angkatan;
    private javax.swing.JButton dial_batal;
    private javax.swing.JComboBox<String> dial_jenis;
    private javax.swing.JTextField dial_jurusan;
    private javax.swing.JTextField dial_nama;
    private javax.swing.JTextField dial_nisn;
    private javax.swing.JButton dial_simpan;
    private javax.swing.JComboBox<String> dial_status;
    private javax.swing.JButton editButton;
    private javax.swing.JComboBox<String> f_gender;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel popup;
    private javax.swing.JPanel popup2;
    private javax.swing.JTextField search;
    private javax.swing.JTable tabel;
    // End of variables declaration//GEN-END:variables
}
