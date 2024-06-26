/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Admin;

import Admin.*;
import com.formdev.flatlaf.FlatClientProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import Navbar.koneksi;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

/**
 *
 * @author Septian Galoh P
 */
public class Admin extends javax.swing.JPanel {

    /**
     * Creates new form Admin
     */
    private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs;
    private String ID_users;

    public Admin() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        loadTabel();
        Jdialog();
//        jcombo();
        jPanel1.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        popup.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        popup2.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Nama");

        UIManager.put("Button.arc", 15);
        search.putClientProperty("JComponent.roundRect", true);
        tabel.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel.getTableHeader().setForeground(Color.white);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        jDialog1.setSize(658, 313);
    }

    private void Jdialog() {
        jDialog1.setLocationRelativeTo(null);
        jDialog1.setBackground(Color.white);
        jDialog1.getRootPane().setOpaque(false);
        jDialog1.getContentPane().setBackground(new Color(0, 0, 0, 0));
        jDialog1.setBackground(new Color(0, 0, 0, 0));
    }

//    private void jcombo() {
//        if (dial_status.getSelectedItem() == null) {
//            dial_status.setSelectedItem("Pilih Status");
//        }
//    }
    private void loadTabel() throws SQLException {
        tabel.clearSelection();
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Status");

        try {
            pst = con.prepareStatement("SELECT username, password, status FROM users");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
            tabel.setModel(model);
        } catch (Exception e) {
            System.out.println("loadTable" + e);
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

        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Status");

        try {
            pst = con.prepareStatement("SELECT username, password, status FROM users where username Like '%" + search.getText() + "%'");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
            tabel.setModel(model);
        } catch (Exception e) {
            System.out.println("searchTable" + e);
        }
    }

    private String getIdUsersByUsername(String username) throws SQLException {
        String idUsers = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            String query = "SELECT ID_users FROM users WHERE username = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();

            if (rs.next()) {
                idUsers = rs.getString("ID_users");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pst != null) {
                pst.close();
            }
        }

        return idUsers;
    }

    // Method untuk memeriksa apakah sudah ada pengguna dengan status admin
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
        dial_id = new javax.swing.JTextField();
        dial_username = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dial_simpan = new javax.swing.JButton();
        dial_batal = new javax.swing.JButton();
        dial_password = new javax.swing.JTextField();
        dial_status = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        jDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialog1.setBackground(new java.awt.Color(255, 255, 255));
        jDialog1.setIconImage(null);
        jDialog1.setMaximumSize(new java.awt.Dimension(658, 395));
        jDialog1.setMinimumSize(new java.awt.Dimension(658, 395));
        jDialog1.setUndecorated(true);
        jDialog1.setResizable(false);
        jDialog1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jDialog1FocusLost(evt);
            }
        });
        jDialog1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jDialog1KeyPressed(evt);
            }
        });

        popup.setBackground(new java.awt.Color(204, 204, 204));

        popup2.setBackground(new java.awt.Color(63, 148, 105));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("USER");

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

        dial_id.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_idKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setText("Username");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel4.setText("Id User");

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel6.setText("Password");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
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

        dial_password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_passwordKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout popupLayout = new javax.swing.GroupLayout(popup);
        popup.setLayout(popupLayout);
        popupLayout.setHorizontalGroup(
            popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, popupLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(202, 202, 202))
            .addComponent(popup2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(popupLayout.createSequentialGroup()
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(popupLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dial_username, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dial_id, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(popupLayout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel3)))
                        .addGap(84, 84, 84)
                        .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dial_password, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                            .addGroup(popupLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel8))
                            .addComponent(dial_status)))
                    .addGroup(popupLayout.createSequentialGroup()
                        .addGap(213, 213, 213)
                        .addComponent(dial_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(dial_simpan)))
                .addContainerGap(21, Short.MAX_VALUE))
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
                    .addComponent(dial_id, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_password, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(popupLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dial_username, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(popupLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dial_status, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(40, 40, 40)
                .addGroup(popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_simpan)
                    .addComponent(dial_batal))
                .addContainerGap(38, Short.MAX_VALUE))
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
        jLabel1.setText("DATA USER");

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
            public void keyTyped(java.awt.event.KeyEvent evt) {
                searchKeyTyped(evt);
            }
        });

        tabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id User", "Username", "Password", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
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
            tabel.getColumnModel().getColumn(1).setResizable(false);
            tabel.getColumnModel().getColumn(2).setResizable(false);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1031, Short.MAX_VALUE))
                .addGap(40, 40, 40))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .addGap(7, 7, 7))
        );

        search.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
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

        dial_id.setText(null);
        dial_username.setText(null);
        dial_password.setText(null);
        dial_status.setText(null); // Menambahkan ini untuk membersihkan field status

        // Menangani field status berdasarkan keberadaan admin
        if (alreadyHasAdmin()) {
            dial_status.setText("Petugas");
            dial_status.setEditable(false); // Membuat field tidak dapat diubah jika admin sudah ada
        } else {
            dial_status.setEditable(true); // Membuat field dapat diubah jika tidak ada admin
        }

        // Mengaktifkan field ID dan menampilkan dialog
        dial_id.setEnabled(true);
        jDialog1.setVisible(true);


    }//GEN-LAST:event_addButtonActionPerformed
    private boolean alreadyHasAdmin() {
        // Misalkan Anda menggunakan JDBC untuk mengakses database
        String query = "SELECT COUNT(*) FROM users WHERE status = 'admin'";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpusmu", "root", ""); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Kembalikan true jika ada admin
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
        if (ID_users != null) {
            try {
                pst = con.prepareStatement("SELECT * FROM users WHERE ID_users = ?");
                pst.setString(1, ID_users);
                rs = pst.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String status = rs.getString("status");

                    dial_id.setText(ID_users);
                    dial_username.setText(username);
                    dial_password.setText(password);
                    dial_status.setText(status);
                    dial_id.setEnabled(false); // Changed enable(false) to setEnabled(false)

                    jDialog1.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(popup, "Tidak ada data yang sesuai dengan ID yang diberikan");
                    jDialog1.setVisible(false);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(popup, "Gagal mengambil data: " + e.getMessage());
                e.printStackTrace();
                jDialog1.setVisible(false);
            }
        } else {
            JOptionPane.showMessageDialog(popup, "Pilih Dahulu Data Yang Akan Di Ubah");
            jDialog1.setVisible(false);
        }


    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        if (ID_users != null) {
            int result = JOptionPane.showConfirmDialog(jDialog1, "Apakah Anda Yakin Ingin Menghapus?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    // Check if the user is an admin
                    pst = con.prepareStatement("SELECT status FROM users WHERE ID_users = ?");
                    pst.setString(1, ID_users);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        String status = rs.getString("status");
                        if ("admin".equalsIgnoreCase(status)) {
                            JOptionPane.showMessageDialog(jDialog1, "Admin tidak dapat dihapus");
                        } else {
                            // Proceed with deletion
                            pst = con.prepareStatement("DELETE FROM users WHERE ID_users = ?");
                            pst.setString(1, ID_users);
                            pst.executeUpdate();
                            loadTabel();
                            JOptionPane.showMessageDialog(popup, "Data berhasil dihapus");
                        }
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(jDialog1, "Gagal menghapus data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(jDialog1, "Pilih Dahulu Data Yang Ingin di Hapus");
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void jDialog1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jDialog1FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jDialog1FocusLost

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
            loadTabel();
            Jdialog();

        } catch (SQLException ex) {
            Logger.getLogger(Admin.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jPanel1AncestorAdded

    private void dial_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_batalActionPerformed
        // TODO add your handling code here:
        dial_id.setText(null);
        dial_username.setText(null);
        dial_password.setText(null);
        dial_status.setText(null);

        ID_users = null;
        jDialog1.dispose();
    }//GEN-LAST:event_dial_batalActionPerformed

    private void tabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelMouseClicked
        int index = tabel.getSelectedRow();
        TableModel model = tabel.getModel();
        String username = model.getValueAt(index, 0).toString();

        try {
            ID_users = getIdUsersByUsername(username);
            System.out.println("ID_users: " + ID_users);
        } catch (SQLException e) {
            System.out.println("Error fetching ID_users: " + e);
        }
    }//GEN-LAST:event_tabelMouseClicked

    private void dial_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_simpanActionPerformed
        // TODO add your handling code here:
        String Id = dial_id.getText();
        String Username = dial_username.getText();
        String Password = dial_password.getText();
        String Status = (String) dial_status.getText();

        if (Id.equals("") || Username.equals("") || Password.equals("") || Status.equals("")) {
            JOptionPane.showMessageDialog(jDialog1, "Anda Harus Mengisi Semua Data Terlebih Dahulu");
        } else {
            int result = JOptionPane.showConfirmDialog(jDialog1, "Apakah Anda Ingin Menyimpan?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    pst = con.prepareStatement("select * from users where ID_users = ?");
                    pst.setString(1, Id);
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        pst = con.prepareStatement("update users set username=?, password=?, status=? where ID_users=?");
                        pst.setString(1, Username);
                        pst.setString(2, Password);
                        pst.setString(3, Status);
                        pst.setString(4, Id);
                        pst.execute();
                    } else {
                        pst = con.prepareStatement("Insert into users (ID_users, username, password, status) values (?, ?, ?, ?)");
                        pst.setString(1, Id);
                        pst.setString(2, Username);
                        pst.setString(3, Password);
                        pst.setString(4, Status);
                        pst.execute();
                    }

                    loadTabel();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(jDialog1, "Gagal menyimpan data: " + e.getMessage());
                    System.out.println("ID_users users" + e);
                }
                jDialog1.dispose();
            }
        }

    }//GEN-LAST:event_dial_simpanActionPerformed

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        try {
            // TODO add your handling code here:
            loadTabel();

        } catch (SQLException ex) {
            Logger.getLogger(Admin.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formAncestorAdded

    private void searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                searchList();

            } catch (SQLException ex) {
                Logger.getLogger(Admin.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_searchKeyPressed

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
        try {
            // TODO add your handling code here:
            searchList();
        } catch (SQLException ex) {
            Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_searchKeyReleased

    private void searchKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_searchKeyTyped

    private void dial_idKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_idKeyTyped
        char c = evt.getKeyChar();

        // Allow "Backspace" and "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Check the length of the input
        String key = dial_id.getText();
        if (key.length() >= 10) {
            JOptionPane.showMessageDialog(jDialog1, "Tidak boleh lebih dari 10 karakter");
            evt.consume();
            return;
        }
    }//GEN-LAST:event_dial_idKeyTyped

    private void dial_passwordKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_passwordKeyTyped

        char c = evt.getKeyChar();

        // Allow "Backspace" and "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Check the length of the input
        String key = dial_password.getText();
        if (key.length() >= 16) {
            JOptionPane.showMessageDialog(jDialog1, "Tidak boleh lebih dari 16 karakter");
            evt.consume();
            return;
        }


    }//GEN-LAST:event_dial_passwordKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton dial_batal;
    private javax.swing.JTextField dial_id;
    private javax.swing.JTextField dial_password;
    private javax.swing.JButton dial_simpan;
    private javax.swing.JTextField dial_status;
    private javax.swing.JTextField dial_username;
    private javax.swing.JButton editButton;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel popup;
    private javax.swing.JPanel popup2;
    private javax.swing.JTextField search;
    private javax.swing.JTable tabel;
    // End of variables declaration//GEN-END:variables
}
