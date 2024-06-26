/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package denda;

import com.formdev.flatlaf.FlatClientProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import Navbar.koneksi;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

/**
 *
 * @author Septian Galoh P
 */
public class Denda extends javax.swing.JPanel {

    /**
     * Creates new form Anggota
     */
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;
    private String nama;

    public Denda() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        loadTabel();
        Jdialog();
        jPanel1.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        denda_popup.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        denda_popup2.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari Nama");

        UIManager.put("Button.arc", 15);
        search.putClientProperty("JComponent.roundRect", true);
        tabel.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel.getTableHeader().setForeground(Color.white);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

    }

    private void Jdialog() {
        jDialog1.setSize(640, 293);

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - jDialog1.getWidth()) / 2;
        final int y = (screenSize.height - jDialog1.getHeight()) / 2;
        jDialog1.setLocation(x, y);
    }

    private void update() {
        int denda = Integer.parseInt(denda_total.getText());
        try {
            pst = con.prepareStatement("UPDATE denda set jumlah_denda = jumlah_denda - " + denda + " where "
                    + "NISN = (SELECT NISN FROM anggota WHERE nama = '" + nama + "');");
            pst.execute();
        } catch (Exception e) {
            System.out.println("update" + e);
        }
    }

    private void update2() {
        try {
            pst = con.prepareStatement("Update denda set status_denda = 'Belum Lunas' where jumlah_denda != 0");
            pst.execute();
        } catch (Exception e) {
            System.out.println("Dta Update2");
        }
    }

    private void loadTabel() throws SQLException {
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
        model.addColumn("Total Pembayaran");

        try {
            update2();
            pst = con.prepareStatement("SELECT anggota.nama , denda.jumlah_denda, denda.status_denda, denda.total_pembayaran "
                    + "from pengembalian join denda on pengembalian.kode_pengembalian = denda.kode_pengembalian join"
                    + " anggota on anggota.NISN = denda.NISN "
                    + "where denda.status_denda = 'Belum Lunas';");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getString(3), 0});
            }
            tabel.setModel(model);
        } catch (Exception e) {
            System.out.println("loadTable" + e);
        }
    }

    private void pencarian(String key) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        String sql = "SELECT anggota.nama as `Nama`, denda.jumlah_denda as `Jumlah Denda`, denda.status_denda as `Status Denda`, "
                + "denda.total_pembayaran as `Total Pembayaran` from pengembalian join"
                + " denda on pengembalian.kode_pengembalian = denda.kode_pengembalian join"
                + " anggota on anggota.NISN = denda.NISN "
                + "where denda.status_denda = 'Belum Lunas' OR `Nama` like '&" + key + "&'";

        try {
            if (key.length() == 0) {
                loadTabel();
            } else {
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
            }
        } catch (Exception e) {
            System.out.println("loadTable = " + e);
        }
    }

    private void enter(int denda) {
        try {
            int Bayar = Integer.parseInt(denda_total.getText());
            int bayar_awal;
            pst = con.prepareStatement("select total_pembayaran from denda where NISN = (select NISN from anggota where"
                    + " nama = '" + nama + "');");
            rs = pst.executeQuery();
            rs.next();
            bayar_awal = rs.getInt(1);
            int total_bayar = bayar_awal + Bayar;

            if (Bayar > denda) {
                JOptionPane.showMessageDialog(jDialog1, "Pastikan Harga yang di bayarkan sesusai dengan jumlah denda");
            } else {
                try {
                    pst = con.prepareStatement("update denda set total_pembayaran= " + total_bayar
                            + " where NISN = (SELECT NISN FROM anggota WHERE nama = '" + nama + "');");
                    pst.execute();
                    update();
                    try {
                        pst = con.prepareStatement("update denda set status_denda = "
                                + "'Lunas' where jumlah_denda = 0 or jumlah_denda < 0");
                        pst.execute();
                    } catch (Exception e) {
                        System.out.println("Lunas" + e);
                    }
                    loadTabel();
                    denda_total.setText(null);
                } catch (Exception e) {
                    System.out.println("2" + e);
                }
            }
        } catch (Exception e) {
            System.out.println("1" + e);
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

        jDialog1 = new javax.swing.JDialog();
        denda_popup = new javax.swing.JPanel();
        denda_popup2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        denda_jumlah = new javax.swing.JTextField();
        denda_nama = new javax.swing.JTextField();
        denda_total = new javax.swing.JTextField();
        denda_status = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        edit_denda = new javax.swing.JButton();
        hapus_denda = new javax.swing.JButton();

        jDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialog1.setLocationByPlatform(true);
        jDialog1.setUndecorated(true);

        denda_popup.setBackground(new java.awt.Color(204, 204, 204));

        denda_popup2.setBackground(new java.awt.Color(63, 148, 105));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("DENDA");

        javax.swing.GroupLayout denda_popup2Layout = new javax.swing.GroupLayout(denda_popup2);
        denda_popup2.setLayout(denda_popup2Layout);
        denda_popup2Layout.setHorizontalGroup(
            denda_popup2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(denda_popup2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(508, Short.MAX_VALUE))
        );
        denda_popup2Layout.setVerticalGroup(
            denda_popup2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(denda_popup2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        denda_jumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                denda_jumlahActionPerformed(evt);
            }
        });

        denda_total.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                denda_totalKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                denda_totalKeyReleased(evt);
            }
        });

        denda_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Belum Lunas", "Lunas" }));

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setText("Nama");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel4.setText("Status Denda");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel5.setText("Total Pembayaran");

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel6.setText("Jumlah Denda");

        jButton1.setBackground(new java.awt.Color(63, 148, 105));
        jButton1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("SIMPAN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 0, 0));
        jButton4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("BATAL");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout denda_popupLayout = new javax.swing.GroupLayout(denda_popup);
        denda_popup.setLayout(denda_popupLayout);
        denda_popupLayout.setHorizontalGroup(
            denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(denda_popup2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, denda_popupLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(denda_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(denda_status, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(denda_total, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(denda_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
            .addGroup(denda_popupLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(denda_popupLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6)
                        .addGap(163, 163, 163))
                    .addGroup(denda_popupLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(132, 132, 132))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, denda_popupLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(39, 39, 39)
                .addComponent(jButton1)
                .addGap(208, 208, 208))
        );
        denda_popupLayout.setVerticalGroup(
            denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(denda_popupLayout.createSequentialGroup()
                .addComponent(denda_popup2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(denda_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(denda_jumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(denda_total, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(denda_status, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(denda_popupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton4))
                .addGap(0, 23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(denda_popup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(denda_popup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("LIST DATA DENDA");

        search.setToolTipText("");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        search.addKeyListener(new java.awt.event.KeyAdapter() {
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

        edit_denda.setBackground(new java.awt.Color(255, 227, 130));
        edit_denda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/edit_1160515 3.png"))); // NOI18N
        edit_denda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_dendaActionPerformed(evt);
            }
        });

        hapus_denda.setBackground(new java.awt.Color(255, 145, 66));
        hapus_denda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/trash-can_7343703 2.png"))); // NOI18N
        hapus_denda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapus_dendaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(edit_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(hapus_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1313, Short.MAX_VALUE)))
                .addGap(40, 40, 40))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edit_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hapus_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .addContainerGap())
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

    private void edit_dendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_dendaActionPerformed
        // TODO add your handling code here:
        try {
            pst = con.prepareStatement("SELECT anggota.nama , denda.jumlah_denda, denda.status_denda, denda.total_pembayaran "
                    + "from pengembalian join denda on pengembalian.kode_pengembalian = denda.kode_pengembalian join anggota"
                    + " on anggota.NISN = denda.NISN "
                    + "where nama ='" + nama + "'");
            rs = pst.executeQuery();
            rs.next();
            String NAMA = rs.getString(1);
            String JUMLAH = Integer.toString(rs.getInt(2));
            String STATUS = rs.getString(3);

            denda_nama.setText(NAMA);
            denda_status.setSelectedItem(STATUS);
            denda_jumlah.setText(JUMLAH);
            denda_total.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
            denda_nama.enable(false);
            denda_status.enable(false);
            denda_jumlah.enable(false);

            jDialog1.setVisible(true);
        } catch (Exception e) {
            System.out.println("edit denda" + e);
            JOptionPane.showMessageDialog(denda_popup, "Pilih Dahulu Data Yang Akan Di Ubah");
            jDialog1.setVisible(false);
        }
    }//GEN-LAST:event_edit_dendaActionPerformed

    private void hapus_dendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapus_dendaActionPerformed
        // TODO add your handling code here:
        if (nama != null) {
            int result = JOptionPane.showConfirmDialog(jDialog1, "Apakah Anda Yakin Ingin Menghapus?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    String Debt;
                    pst = con.prepareStatement("Select status_denda from denda where"
                            + " NISN = (SELECT NISN FROM anggota WHERE nama = '" + nama + "');");
                    rs = pst.executeQuery();
                    if (!rs.next()) {
                        pst = con.prepareStatement("delete from denda  where"
                                + " NISN = (SELECT NISN FROM anggota WHERE nama = '" + nama + "');");
                        pst.execute();
                    } else {
                        Debt = rs.getString(1);
                        if (Debt.equals("Belum Lunas")) {
                            JOptionPane.showMessageDialog(jDialog1, "Anggota Masih Belum melunaskan tanggungannya");
                        } else {
                            pst = con.prepareStatement("delete from denda  where"
                                    + " NISN = (SELECT NISN FROM anggota WHERE nama = '" + nama + "');");
                            pst.execute();
                        }
                    }
                    loadTabel();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(jDialog1, "Pilih Dahulu Data Yang Ingin di Hapus");
                }
            }
        } else {
            JOptionPane.showMessageDialog(jDialog1, "Pilih Dahulu Data Yang Ingin di Hapus");
        }
    }//GEN-LAST:event_hapus_dendaActionPerformed

    private void tabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelMouseClicked
        // TODO add your handling code here:
        int index = tabel.getSelectedRow();
        TableModel model = tabel.getModel();
        nama = model.getValueAt(index, 0).toString();
    }//GEN-LAST:event_tabelMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        denda_nama.setText(null);
        denda_jumlah.setText(null);
        denda_status.setSelectedItem(this);
        denda_total.setText(null);

        jDialog1.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int denda = Integer.parseInt(denda_jumlah.getText());
        if (denda_total.getText().equals("") || denda_jumlah.getText().equals("")) {
            JOptionPane.showMessageDialog(jDialog1, "Isi Semua Data Yang ada");
        } else {
            int result = JOptionPane.showConfirmDialog(jDialog1, "Apakah Anda Yakin Ingin Mengubah Data Yang ada?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                enter(denda);
                nama = null;
                jDialog1.dispose();
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
        // TODO add your handling code here:
        String key = search.getText().trim();
        pencarian(key);
    }//GEN-LAST:event_searchKeyReleased

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        try {
            // TODO add your handling code here:
            loadTabel();
        } catch (SQLException ex) {
            Logger.getLogger(Denda.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formAncestorAdded

    private void denda_totalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_denda_totalKeyReleased
        // TODO add your handling code here:
        if ((Character.isAlphabetic(evt.getKeyChar())) || (Character.isWhitespace(evt.getKeyChar()))) {
            JOptionPane.showMessageDialog(jDialog1, "Hanya Bisa di isi dengan Angka");
            evt.consume();
        }
    }//GEN-LAST:event_denda_totalKeyReleased

    private void denda_jumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_denda_jumlahActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_denda_jumlahActionPerformed

    private void denda_totalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_denda_totalKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int denda = Integer.parseInt(denda_jumlah.getText());
            enter(denda);
            nama = null;
            jDialog1.dispose();
        }
    }//GEN-LAST:event_denda_totalKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField denda_jumlah;
    private javax.swing.JTextField denda_nama;
    private javax.swing.JPanel denda_popup;
    private javax.swing.JPanel denda_popup2;
    private javax.swing.JComboBox<String> denda_status;
    private javax.swing.JTextField denda_total;
    private javax.swing.JButton edit_denda;
    private javax.swing.JButton hapus_denda;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField search;
    private javax.swing.JTable tabel;
    // End of variables declaration//GEN-END:variables
}
