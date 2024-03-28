/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transaksi;

import Login.Config;
import Navbar.koneksi;
import java.awt.Color;
import java.awt.im.InputContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author USER
 */
public class Peminjaman extends javax.swing.JPanel {

    private int nomorUrutanTerakhir = 0;

    private Connection con;

    /**
     * Creates new form Peminjaman1
     */
    public Peminjaman() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        UIManager.put("Button.arc", 15);
        menu.add(nisn);
        menu2.add(kode_buku);
        menu3.add(petugas);
        load_table1();
        load_table2();
        id_autoincrement();
        setTanggalHariIni();
        loadtable();

        tabel_peminjaman.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel_peminjaman.getTableHeader().setForeground(Color.white);

    }

    private void loadtable() throws SQLException {
        tabel_peminjaman.clearSelection();
        DefaultTableModel model = new DefaultTableModel();
    }

    private void id_autoincrement() {
        try {
            String sqlquery = "SELECT kode_peminjaman FROM peminjaman ORDER BY kode_peminjaman DESC LIMIT 1";
            PreparedStatement pst = con.prepareStatement(sqlquery);
            ResultSet rs = pst.executeQuery();
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

            int newNumber = 1; // Default newNumber
            if (rs.next()) {
                String idStr = rs.getString(1);
                if (idStr != null && idStr.length() == 8) { // Check if idStr is not null and has correct length
                    String lastDateStr = idStr.substring(0, 6); // Extract last date from idStr
                    if (lastDateStr.equals(currentDate)) {
                        int lastNumber = Integer.parseInt(idStr.substring(6)); // Extract last number from idStr
                        newNumber = lastNumber + 1; // Increment last number
                    }
                }
            }

            String formattedNumber = String.format("%02d", newNumber); // Format new number with leading zeros
            String newId = currentDate + formattedNumber; // Concatenate current date and formatted number
            txt_kode_peminjaman.setText(newId); // Set the new ID to the text field
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void load_table3() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nama Petugas");

        try {
            String sql = "select * from users";
            java.sql.Connection con = (Connection) Config.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]{res.getString(1)});
            }
            tabel_users.setModel(model);
        } catch (Exception e) {

        }
    }

    private void load_table1() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NISN");
        model.addColumn("Nama");

        try {
            String sql = "select * from anggota";
            java.sql.Connection con = (Connection) Config.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]{res.getString(1), res.getString(2)});
            }
            tabel_anggota.setModel(model);
        } catch (Exception e) {

        }
    }

    private void load_table2() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Kode Buku");
        model.addColumn("Judul Buku");
        model.addColumn("Jumlah Stock");

        try {
            String sql = "select * from buku";
            java.sql.Connection con = (Connection) Config.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()) {
                model.addRow(new Object[]{res.getString(1), res.getString(2), res.getString(3)});
            }
            tabel_buku.setModel(model);
        } catch (Exception e) {

        }
    }

    public void clear() {
        txt_kode_buku.setText("");
        txt_judul_buku.setText("");
        txt_jumlah_pinjam.setText("");

        tanggal_kembali.setDate(null);
    }

    public void clear1() {
        txt_nisn.setText("");
        txt_anggota.setText("");
    }

    public void clear3() {
        txt_kode_buku.setText("");
        txt_judul_buku.setText("");
        txt_jumlah_pinjam.setText("");
        txt_nisn.setText("");
        txt_anggota.setText("");
        txt_petugas.setText("");
        tanggal_kembali.setDate(null);
    }

    public void setTanggalHariIni() {
        // Mendapatkan tanggal hari ini
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();

        // Mengatur tanggal_pinjam di JCalendar menjadi tanggal hari ini
        tanggal_pinjam.setDate(today);
    }

    private int getStokBuku(Connection con, String kodeBuku) throws SQLException {
        String sql = "SELECT jumlah_stock FROM buku WHERE No_buku = (SELECT No_buku FROM buku WHERE judul_buku = ?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, kodeBuku);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("jumlah_stock");
                } else {
                    throw new SQLException("Buku dengan kode " + kodeBuku + " tidak ditemukan");
                }
            }
        }
    }

    // Fungsi untuk mengurangi stok buku setelah peminjaman
    private void kurangiStokBuku(Connection con, String kodeBuku, int jumlahPeminjaman) throws SQLException {
        String sql = "UPDATE buku SET jumlah_stock = jumlah_stock - ? WHERE No_buku = (SELECT No_buku FROM buku WHERE judul_buku = ?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, jumlahPeminjaman);
            pst.setString(2, kodeBuku);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Gagal mengurangi stok buku");
            }
        }
    }

    private boolean isKodePeminjamanExists(String kodepeminjaman) {
        try {
            String query = "SELECT * FROM peminjaman WHERE kode_peminjaman = ?";
            try (Connection con = koneksi.Koneksi(); PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, kodepeminjaman);
                try (ResultSet rs = pst.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tabel_peminjaman.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    private String getKodeBukuByJudulFromDatabase(String judulBuku) {
        String kodeBuku = "";
        try {
            // Menghubungkan ke database Anda

            // Membuat kueri untuk mendapatkan kode buku berdasarkan judul buku
            String query = "SELECT kode_buku FROM buku WHERE judul_buku = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, judulBuku);

            // Mengeksekusi kueri
            ResultSet resultSet = statement.executeQuery();

            // Mengambil hasil kueri jika ada
            if (resultSet.next()) {
                kodeBuku = resultSet.getString("kode_buku");
            }

            // Menutup koneksi dan sumber daya terkait
//            resultSet.close();
//            statement.close();
//            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return kodeBuku;
    }

    public void refresh() {
        DefaultTableModel model = (DefaultTableModel) tabel_peminjaman.getModel();
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        try {
//            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpusmu_v2", "root", "");
            String sqlquery = "SELECT kode_peminjaman FROM peminjaman ORDER BY kode_peminjaman DESC LIMIT 1";
            PreparedStatement pst = con.prepareStatement(sqlquery);
            ResultSet rs = pst.executeQuery();
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

            int newNumber = 1; // Default newNumber
            if (rs.next()) {
                String idStr = rs.getString(1);
                if (idStr != null && idStr.length() == 8) { // Check if idStr is not null and has correct length
                    String lastDateStr = idStr.substring(0, 6); // Extract last date from idStr
                    if (lastDateStr.equals(currentDate)) {
                        int lastNumber = Integer.parseInt(idStr.substring(6)); // Extract last number from idStr
                        newNumber = lastNumber + 1; // Increment last number
                    }
                }
            }

            String formattedNumber = String.format("%02d", newNumber); // Format new number with leading zeros
            System.out.println(formattedNumber);
            String newId = currentDate + formattedNumber; // Concatenate current date and formatted number
            txt_kode_peminjaman.setText(newId); // Set the new ID to the text field
            System.out.println(newId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
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

        nisn = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_anggota = new javax.swing.JTable();
        menu = new javax.swing.JPopupMenu();
        kode_buku = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_buku = new javax.swing.JTable();
        menu2 = new javax.swing.JPopupMenu();
        petugas = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_users = new javax.swing.JTable();
        menu3 = new javax.swing.JPopupMenu();
        jPanel11 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        txt_kode_peminjaman = new javax.swing.JTextField();
        txt_nisn = new javax.swing.JTextField();
        txt_anggota = new javax.swing.JTextField();
        txt_petugas = new javax.swing.JTextField();
        txt_jumlah_pinjam = new javax.swing.JTextField();
        txt_kode_buku = new javax.swing.JTextField();
        txt_judul_buku = new javax.swing.JTextField();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        tanggal_pinjam = new com.toedter.calendar.JDateChooser();
        tanggal_kembali = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_peminjaman = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();

        tabel_anggota.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NISN", "Nama"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_anggota.getTableHeader().setReorderingAllowed(false);
        tabel_anggota.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_anggotaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabel_anggota);
        if (tabel_anggota.getColumnModel().getColumnCount() > 0) {
            tabel_anggota.getColumnModel().getColumn(0).setResizable(false);
            tabel_anggota.getColumnModel().getColumn(1).setResizable(false);
        }

        javax.swing.GroupLayout nisnLayout = new javax.swing.GroupLayout(nisn);
        nisn.setLayout(nisnLayout);
        nisnLayout.setHorizontalGroup(
            nisnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
        );
        nisnLayout.setVerticalGroup(
            nisnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
        );

        menu.setFocusable(false);

        tabel_buku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Buku", "No Refrensi", "Pengarang", "Judul Buku", "Jilid", "Jumlah Stok", "Kondisi", "Kategori", "Lokasi"
            }
        ));
        tabel_buku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_bukuMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabel_buku);

        javax.swing.GroupLayout kode_bukuLayout = new javax.swing.GroupLayout(kode_buku);
        kode_buku.setLayout(kode_bukuLayout);
        kode_bukuLayout.setHorizontalGroup(
            kode_bukuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        kode_bukuLayout.setVerticalGroup(
            kode_bukuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
        );

        menu2.setFocusable(false);

        tabel_users.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama Petugas"
            }
        ));
        tabel_users.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_usersMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabel_users);

        javax.swing.GroupLayout petugasLayout = new javax.swing.GroupLayout(petugas);
        petugas.setLayout(petugasLayout);
        petugasLayout.setHorizontalGroup(
            petugasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        petugasLayout.setVerticalGroup(
            petugasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
        );

        menu3.setFocusable(false);

        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                formAncestorAdded(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        jPanel11.setPreferredSize(new java.awt.Dimension(1399, 472));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel65.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel65.setText("Kode Peminjaman");

        jLabel66.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel66.setText("NISN");

        jLabel67.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel67.setText("Kode Buku");

        jLabel68.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel68.setText("Jumlah Pinjam");

        jLabel69.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel69.setText("Tanggal Peminjaman");

        jLabel70.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel70.setText("Tanggal Kembali");

        jLabel71.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel71.setText("Nama Anggota");

        jLabel72.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel72.setText("Petugas");

        jLabel73.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel73.setText("Judul Buku");

        txt_kode_peminjaman.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_kode_peminjaman.setEnabled(false);
        txt_kode_peminjaman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField28jTextField1ActionPerformed(evt);
            }
        });

        txt_nisn.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_nisn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField31jTextField4ActionPerformed(evt);
            }
        });
        txt_nisn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nisnKeyReleased(evt);
            }
        });

        txt_anggota.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_anggota.setEnabled(false);
        txt_anggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField32jTextField5ActionPerformed(evt);
            }
        });

        txt_petugas.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_petugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField33jTextField6ActionPerformed(evt);
            }
        });
        txt_petugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_petugasKeyReleased(evt);
            }
        });

        txt_jumlah_pinjam.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_jumlah_pinjam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField34jTextField7ActionPerformed(evt);
            }
        });

        txt_kode_buku.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_kode_buku.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txt_kode_bukuMouseClicked(evt);
            }
        });
        txt_kode_buku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField35jTextField8ActionPerformed(evt);
            }
        });
        txt_kode_buku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_kode_bukuKeyReleased(evt);
            }
        });

        txt_judul_buku.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_judul_buku.setEnabled(false);
        txt_judul_buku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField36jTextField9ActionPerformed(evt);
            }
        });

        jButton20.setBackground(new java.awt.Color(0, 255, 0));
        jButton20.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jButton20.setForeground(new java.awt.Color(255, 255, 255));
        jButton20.setText("SIMPAN");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setBackground(new java.awt.Color(255, 0, 0));
        jButton21.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jButton21.setForeground(new java.awt.Color(255, 255, 255));
        jButton21.setText("BATAL");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        tanggal_pinjam.setDateFormatString("yyyy-MM-dd");
        tanggal_pinjam.setEnabled(false);

        tanggal_kembali.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel65)
                            .addComponent(jLabel69)
                            .addComponent(jLabel70)
                            .addComponent(txt_kode_peminjaman)
                            .addComponent(tanggal_pinjam, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                            .addComponent(tanggal_kembali, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(255, 255, 255)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_nisn, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel66)
                            .addComponent(jLabel71)
                            .addComponent(txt_anggota, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72)
                            .addComponent(txt_petugas, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_kode_buku, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_judul_buku, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jumlah_pinjam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel67)
                            .addComponent(jLabel73)
                            .addComponent(jLabel68)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton21)
                        .addGap(18, 18, 18)
                        .addComponent(jButton20)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(jLabel66)
                    .addComponent(jLabel67))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_kode_buku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_nisn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_kode_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel73)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_judul_buku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel68)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_jumlah_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton21)
                            .addComponent(jButton20)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel69)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tanggal_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel70)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tanggal_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel71)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_anggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel72)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_petugas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tabel_peminjaman.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Peminjaman", "Tanggal Pinjam", "Tanggal Kembali", "Nama", "Jumlah Pinjam", "Judul Buku", "Petugas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_peminjaman.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tabel_peminjaman.setGridColor(new java.awt.Color(0, 0, 0));
        tabel_peminjaman.getTableHeader().setReorderingAllowed(false);
        tabel_peminjaman.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_peminjamanMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_peminjaman);
        if (tabel_peminjaman.getColumnModel().getColumnCount() > 0) {
            tabel_peminjaman.getColumnModel().getColumn(0).setResizable(false);
            tabel_peminjaman.getColumnModel().getColumn(1).setResizable(false);
            tabel_peminjaman.getColumnModel().getColumn(2).setResizable(false);
            tabel_peminjaman.getColumnModel().getColumn(3).setResizable(false);
            tabel_peminjaman.getColumnModel().getColumn(4).setResizable(false);
            tabel_peminjaman.getColumnModel().getColumn(5).setResizable(false);
            tabel_peminjaman.getColumnModel().getColumn(6).setResizable(false);
        }

        jButton5.setBackground(new java.awt.Color(0, 255, 0));
        jButton5.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("SIMPAN");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton5)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField36jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField36jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField36jTextField9ActionPerformed

    private void jTextField35jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField35jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField35jTextField8ActionPerformed

    private void jTextField34jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField34jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField34jTextField7ActionPerformed

    private void jTextField33jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField33jTextField6ActionPerformed

    }//GEN-LAST:event_jTextField33jTextField6ActionPerformed

    private void jTextField32jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField32jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField32jTextField5ActionPerformed

    private void jTextField31jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField31jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField31jTextField4ActionPerformed

    private void jTextField28jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField28jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField28jTextField1ActionPerformed

    private void txt_nisnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nisnKeyReleased
        String search = txt_nisn.getText();
        if (!search.equals("")) {
            menu.show(txt_nisn, 0, txt_nisn.getHeight());
        }
        String keyword = txt_nisn.getText().trim();
        String sql = "SELECT * FROM anggota WHERE nisn LIKE '%" + keyword + "%' OR nama LIKE '%" + keyword + "%'";

        try {
//            java.sql.Connection con = (java.sql.Connection) Config.configDB();
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();

            // Creating a model to store the filtered data
            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.addColumn("NISN");
            filteredModel.addColumn("Nama");

            while (rs.next()) {
                filteredModel.addRow(new Object[]{
                    rs.getString("nisn"),
                    rs.getString("nama"), // ... (add other columns as needed)
                });
            }

            // Set the filtered model to the JTable
            tabel_anggota.setModel(filteredModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_txt_nisnKeyReleased

    private void tabel_anggotaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_anggotaMouseClicked
        int index = tabel_anggota.getSelectedRow();
        TableModel model = tabel_anggota.getModel();
        String nisn = model.getValueAt(index, 0).toString();
        String nama = model.getValueAt(index, 1).toString();

        txt_nisn.setText(nisn);
        txt_anggota.setText(nama);
        menu.setVisible(false);
    }//GEN-LAST:event_tabel_anggotaMouseClicked

    private void tabel_bukuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_bukuMouseClicked
        int index = tabel_buku.getSelectedRow();
        TableModel model = tabel_buku.getModel();
        String kode_buku = model.getValueAt(index, 0).toString();
        String judul_buku = model.getValueAt(index, 1).toString();

        txt_kode_buku.setText(kode_buku);
        txt_judul_buku.setText(judul_buku);
        menu2.setVisible(false);
    }//GEN-LAST:event_tabel_bukuMouseClicked

    private void txt_kode_bukuKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kode_bukuKeyReleased
        String search = txt_kode_buku.getText();
        if (!search.equals("")) {
            menu2.show(txt_kode_buku, 0, txt_kode_buku.getHeight());
        }
        String keyword = txt_kode_buku.getText().trim();
        String sql = "SELECT * FROM buku WHERE kode_buku LIKE '%" + keyword + "%' OR judul_buku LIKE '%" + keyword + "%'";

        try {
//            java.sql.Connection con = (java.sql.Connection) Config.configDB();
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();

            // Creating a model to store the filtered data
            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.addColumn("Kode Buku");
            filteredModel.addColumn("Judul Buku");
            filteredModel.addColumn("Jumlah Stock");

            while (rs.next()) {
                filteredModel.addRow(new Object[]{
                    rs.getString("kode_buku"),
                    rs.getString("judul_buku"),
                    rs.getString("jumlah_stock")
                // ... (add other columns as needed)
                });
            }

            // Set the filtered model to the JTable
            tabel_buku.setModel(filteredModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_txt_kode_bukuKeyReleased

    private void txt_kode_bukuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_kode_bukuMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_kode_bukuMouseClicked

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        DefaultTableModel model = (DefaultTableModel) tabel_peminjaman.getModel();
// Mendapatkan teks dari field-field yang relevan
        String kodepeminjaman = txt_kode_peminjaman.getText();
        String nama = txt_anggota.getText();
        String judulbuku = txt_judul_buku.getText();
        String totalpeminjaman = txt_jumlah_pinjam.getText();
        String username = txt_petugas.getText();

// Mendapatkan tanggal_pinjam dan tanggal_kembali
        Date tanggalpinjam = tanggal_pinjam.getDate();
        Date tanggalkembali = tanggal_kembali.getDate();

// Memeriksa jika ada field yang kosong
        if (kodepeminjaman.isEmpty() || nama.isEmpty() || judulbuku.isEmpty() || totalpeminjaman.isEmpty() || username.isEmpty() || tanggalpinjam == null || tanggalkembali == null) {
            // Tampilkan pesan bahwa semua field harus diisi
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else if (tanggalkembali.before(tanggalpinjam)) {
            // Tampilkan notifikasi bahwa tanggal pengembalian tidak boleh mundur dari tanggal pinjam
            JOptionPane.showMessageDialog(null, "Tanggal pengembalian tidak boleh mundur dari tanggal pinjam!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        } else {
            // Format tanggal_pinjam dan tanggal_kembali menjadi string
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tanggalpinjamFormatted = dateFormat.format(tanggalpinjam);
            String tanggalkembaliFormatted = dateFormat.format(tanggalkembali);

            // Tambahkan baris baru ke dalam tabel
            Object[] row = {kodepeminjaman, tanggalpinjamFormatted, tanggalkembaliFormatted, nama, judulbuku, totalpeminjaman, username};
            model.addRow(row);
            clear();
        }
    }//GEN-LAST:event_jButton20ActionPerformed

    private void tabel_peminjamanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_peminjamanMouseClicked
        int selectedRow = tabel_peminjaman.getSelectedRow();
        TableModel model = tabel_peminjaman.getModel();

        // Memastikan baris yang diklik valid
        if (selectedRow != -1) {
            // Mengambil nilai dari kolom-kolom yang sesuai dari baris yang diklik
            String judulBuku = tabel_peminjaman.getValueAt(selectedRow, 4).toString();
            String jumlahPinjam = tabel_peminjaman.getValueAt(selectedRow, 5).toString();
            String tanggalKembali = tabel_peminjaman.getValueAt(selectedRow, 2).toString();

            // Menetapkan nilai-nilai yang diambil ke dalam komponen-komponen yang sesuai
            txt_judul_buku.setText(judulBuku);
            txt_jumlah_pinjam.setText(jumlahPinjam);

            // Mengatur tanggal_kembali dengan nilai yang diambil dari tabel
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date tanggalKembaliDate = dateFormat.parse(tanggalKembali);
                tanggal_kembali.setDate(tanggalKembaliDate);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            // Mengisi txt_kode_buku berdasarkan judul buku yang dipilih dari database
            String kodeBuku = getKodeBukuByJudulFromDatabase(judulBuku);
            txt_kode_buku.setText(kodeBuku);

            // Menghapus baris yang diklik dari tabel
            ((DefaultTableModel) model).removeRow(selectedRow);
        }
    }//GEN-LAST:event_tabel_peminjamanMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        DefaultTableModel model = (DefaultTableModel) tabel_peminjaman.getModel();

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tabel kosong");
        } else {
            try (Connection conn = Config.configDB()) {
                conn.setAutoCommit(false); // Mulai transaksi

                // Proses baris pertama untuk tabel peminjaman
                int barisPertama = 0;
                String kodePeminjamanPertama = model.getValueAt(barisPertama, 0).toString();
                String nisnPertama = model.getValueAt(barisPertama, 3).toString();
                String idPetugasPertama = model.getValueAt(barisPertama, 6).toString();

                try {
                    if (!isKodePeminjamanExists(kodePeminjamanPertama)) {
                        // Insert into tabel peminjaman
                        String sqlPeminjaman = "INSERT INTO peminjaman (kode_peminjaman, NISN, ID_users) VALUES (?, (SELECT NISN FROM anggota WHERE nama = ?), (SELECT ID_users FROM users WHERE username = ?))";
                        try (PreparedStatement pstPeminjaman = conn.prepareStatement(sqlPeminjaman)) {
                            pstPeminjaman.setString(1, kodePeminjamanPertama);
                            pstPeminjaman.setString(2, nisnPertama);
                            pstPeminjaman.setString(3, idPetugasPertama);
                            pstPeminjaman.executeUpdate();
                        }

                        // Proses sisa baris untuk tabel detail_peminjaman
                        for (int i = 0; i < model.getRowCount(); i++) {
                            String kodePeminjaman = model.getValueAt(i, 0).toString();
                            String totalPeminjamanStr = model.getValueAt(i, 5).toString();
                            String tanggalpinjam = model.getValueAt(i, 1).toString();
                            String tanggalkembali = model.getValueAt(i, 2).toString();
                            String kodeBuku = model.getValueAt(i, 4).toString();

                            // Pengecekan stok buku
                            int jumlahPeminjaman = Integer.parseInt(totalPeminjamanStr);
                            int stokBuku = getStokBuku(conn, kodeBuku);
                            if (stokBuku >= jumlahPeminjaman) {
                                // Jika stok mencukupi, kurangi jumlah stok buku
                                kurangiStokBuku(conn, kodeBuku, jumlahPeminjaman);

                                // Insert ke dalam tabel detail_peminjaman
                                String detailPeminjamanSql = "INSERT INTO detail_peminjaman (kode_peminjaman, jumlah_peminjaman, status_peminjaman, tanggal_peminjaman, tanggal_kembali, No_buku) VALUES (?, ?, ?, ?, ?, (SELECT No_buku FROM buku WHERE judul_buku = ?))";
                                try (PreparedStatement pstDetailPeminjaman = conn.prepareStatement(detailPeminjamanSql)) {
                                    pstDetailPeminjaman.setString(1, kodePeminjaman);
                                    pstDetailPeminjaman.setInt(2, jumlahPeminjaman);
                                    pstDetailPeminjaman.setString(3, "dipinjam");
                                    pstDetailPeminjaman.setString(4, tanggalpinjam);
                                    pstDetailPeminjaman.setString(5, tanggalkembali);
                                    pstDetailPeminjaman.setString(6, kodeBuku);
                                    pstDetailPeminjaman.executeUpdate();
                                }
                            } else {
                                // Jika stok tidak mencukupi, batalkan transaksi dan tampilkan pesan notifikasi
                                conn.rollback();
                                JOptionPane.showMessageDialog(this, "Error: Stok buku tidak mencukupi untuk peminjaman");
                                return;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: Kode Peminjaman sudah ada pada baris " + (barisPertama + 1));
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error: Format Total Peminjaman tidak valid pada baris " + (barisPertama + 1));
                }

                conn.commit(); // Commit transaksi
                JOptionPane.showMessageDialog(this, "Peminjaman berhasil disimpan!");
                kosong();
                refresh();
                clear3();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        clear();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void txt_petugasKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_petugasKeyReleased
        String search = txt_petugas.getText();
        if (!search.equals("")) {
            menu3.show(txt_petugas, 0, txt_petugas.getHeight());
        }
        String keyword = txt_petugas.getText().trim();
        String sql = "SELECT * FROM users WHERE username LIKE '%" + keyword + "%'";

        try {
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();

            // Creating a model to store the filtered data
            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.addColumn("Nama Petugas");

            while (rs.next()) {
                filteredModel.addRow(new Object[]{
                    rs.getString("username"), // ... (add other columns as needed)
                });
            }

            // Set the filtered model to the JTable
            tabel_users.setModel(filteredModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_txt_petugasKeyReleased

    private void tabel_usersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_usersMouseClicked
        int index = tabel_users.getSelectedRow();
        TableModel model = tabel_users.getModel();
        String username = model.getValueAt(index, 0).toString();

        txt_petugas.setText(username);
        menu3.setVisible(false);
    }//GEN-LAST:event_tabel_usersMouseClicked

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded

    }//GEN-LAST:event_formAncestorAdded


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private static javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel kode_buku;
    private javax.swing.JPopupMenu menu;
    private javax.swing.JPopupMenu menu2;
    private javax.swing.JPopupMenu menu3;
    private javax.swing.JPanel nisn;
    private javax.swing.JPanel petugas;
    private javax.swing.JTable tabel_anggota;
    private javax.swing.JTable tabel_buku;
    private javax.swing.JTable tabel_peminjaman;
    private javax.swing.JTable tabel_users;
    private com.toedter.calendar.JDateChooser tanggal_kembali;
    private com.toedter.calendar.JDateChooser tanggal_pinjam;
    private javax.swing.JTextField txt_anggota;
    private javax.swing.JTextField txt_judul_buku;
    private javax.swing.JTextField txt_jumlah_pinjam;
    private javax.swing.JTextField txt_kode_buku;
    private javax.swing.JTextField txt_kode_peminjaman;
    private javax.swing.JTextField txt_nisn;
    private javax.swing.JTextField txt_petugas;
    // End of variables declaration//GEN-END:variables

}
