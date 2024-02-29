/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transaksi;

import Navbar.koneksi;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author USER
 */
public class Pengembalian extends javax.swing.JPanel {

    /**
     * Creates new form Pengembalian1
     */
    private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs;

    public Pengembalian() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        load_table();
        load_table1();
        jDialog1();
        id_autoincrement();
        loadtable2();

        tabel_return.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel_return.getTableHeader().setForeground(Color.white);

        tabel_pending.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel_pending.getTableHeader().setForeground(Color.white);

        tabel_pending.getColumnModel().getColumn(0).setPreferredWidth(20);

        jDialog1.setSize(1000, 700);
    }

    private void loadtable2() throws SQLException {
        tabel_pending.clearSelection();
        tabel_return.clearSelection();
        DefaultTableModel model = new DefaultTableModel();
    }

    private void jDialog1() {
        jDialog1.setSize(1000, 700);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - jDialog1.getWidth()) / 2;
        final int y = (screenSize.height - jDialog1.getHeight()) / 2;
        jDialog1.setLocation(x, y);
    }

    private void id_autoincrement() {
        try {

            String sqlquery = "SELECT kode_pengembalian FROM pengembalian ORDER BY kode_pengembalian DESC LIMIT 1";
            pst = con.prepareStatement(sqlquery);
            rs = pst.executeQuery();
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
            txt_kode_pengembalian.setText(newId); // Set the new ID to the text field
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void insDenda() {
        try {
        } catch (Exception e) {
        }
    }

    private void load_table() {

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Membuat semua sel tidak dapat diedit
                return false;
            }
        };

        model.addColumn("Kode Peminjaman");
        model.addColumn("Nama");
        model.addColumn("Petugas");
        model.addColumn("Tanggal Pinjam");
        model.addColumn("Tanggal Kembali");
        model.addColumn("Jumlah Pinjam");
        model.addColumn("Kode Buku");
        model.addColumn("Judul Buku");

        try {
            loadtable2();
            pst = con.prepareStatement("delete from detail_peminjaman where jumlah_peminjaman = 0");
            pst.execute();
            String sql = "SELECT peminjaman.kode_peminjaman, anggota.nama, users.username, detail_peminjaman.tanggal_peminjaman, detail_peminjaman.tanggal_kembali, detail_peminjaman.jumlah_peminjaman, buku.kode_buku, buku.judul_buku FROM peminjaman JOIN users ON peminjaman.id_users = users.id_users JOIN detail_peminjaman ON peminjaman.kode_peminjaman = detail_peminjaman.kode_peminjaman JOIN buku ON detail_peminjaman.No_buku = buku.No_buku JOIN anggota ON peminjaman.NISN = anggota.NISN;";

            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_peminjaman"),
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("tanggal_peminjaman"),
                    rs.getString("tanggal_kembali"),
                    rs.getString("jumlah_peminjaman"), // Pastikan nama kolom sesuai dengan hasil query
                    rs.getString("kode_buku"),
                    rs.getString("judul_buku"),});
            }

            // Set model untuk tabel_pengembalian
            tabel_pending.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void load_table1() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Membuat semua sel tidak dapat diedit
                return false;
            }
        };
        model.addColumn("Kode Pengembalian");
        model.addColumn("Nama");
        model.addColumn("Petugas");
        model.addColumn("Jumlah Pengembalian");
        model.addColumn("Status Pengembalian");
        model.addColumn("Waktu Pengembalian");
        model.addColumn("Kode Buku");
        model.addColumn("Judul Buku");
        model.addColumn("Kondisi Buku");
        model.addColumn("Denda");
        try {
            String sql = "SELECT pengembalian.kode_pengembalian, anggota.nama, users.username, detail_pengembalian.jumlah_pengembalian, detail_pengembalian.status_pengembalian,detail_pengembalian.waktu_pengembalian, buku.kode_buku,buku.judul_buku,detail_pengembalian.kondisi_buku, detail_pengembalian.denda FROM pengembalian JOIN detail_pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN buku ON detail_pengembalian.No_buku = buku.No_buku JOIN users ON pengembalian.id_users = users.id_users JOIN anggota ON detail_pengembalian.NISN = anggota.NISN;";

            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("kode_pengembalian"),
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("jumlah_pengembalian"),
                    rs.getString("status_pengembalian"),
                    rs.getString("waktu_pengembalian"),
                    rs.getString("kode_buku"), // Pastikan nama kolom sesuai dengan hasil query
                    rs.getString("judul_buku"),
                    rs.getString("kondisi_buku"),
                    rs.getString("denda"),});
            }

            // Set model untuk tabel_pengembalian
            tabel_return.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private String getKodeBukuByJudulFromDatabase(String judulBuku) {
        String kodeBuku = "";
        try {
            // Menghubungkan ke database Anda

            // Membuat kueri untuk mendapatkan kode buku berdasarkan judul buku
            String query = "SELECT kode_buku FROM buku WHERE judul_buku = ?";
            pst = con.prepareStatement(query);
            pst.setString(1, judulBuku);

            // Mengeksekusi kueri
            rs = pst.executeQuery();

            // Mengambil hasil kueri jika ada
            if (rs.next()) {
                kodeBuku = rs.getString("kode_buku");
            }

            // Menutup koneksi dan sumber daya terkait
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return kodeBuku;
    }

    private void masuktabelreturn() {
        DefaultTableModel model = (DefaultTableModel) tabel_return.getModel();
// Mendapatkan teks dari field-field yang relevan
        String kodepeminjaman = txt_kode_peminjaman.getText();
        String nama = txt_nama.getText();
        String username = txt_petugas.getText();
        int totalpeminjaman = (int) spinner_pinjam.getValue();
        String statuskembali = (String) status_pengembalian.getSelectedItem();
        String waktukembali = (String) waktu_pengembalian.getSelectedItem();
        String kodebuku = txt_kode_buku.getText();
        String judulbuku = txt_judul_buku.getText();
        String kondisikembali = (String) kondisi_buku.getSelectedItem();
        String denda = txt_denda.getText();

        // Tambahkan baris baru ke dalam tabel
        Object[] row = {kodepeminjaman, nama, username, totalpeminjaman, statuskembali, waktukembali, kodebuku, judulbuku, kondisikembali, denda};
        model.addRow(row);
    }

    private void tambahStokBuku(Connection conn, String namabuku, int jumlahkembali) throws SQLException {
        String sql = "UPDATE buku SET jumlah_stock = jumlah_stock + ? WHERE No_buku = (SELECT No_buku FROM buku WHERE judul_buku = ?)";
        try (PreparedStatement pst1 = conn.prepareStatement(sql)) {
            pst1.setInt(1, jumlahkembali);
            pst1.setString(2, namabuku);
            int rowsAffected = pst1.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Gagal menambahkan stok buku");
            }
        }
    }

    private int getStokBuku(Connection conn, String kodeBuku) throws SQLException {
        String sql = "SELECT jumlah_stock FROM buku WHERE No_buku = (SELECT No_buku FROM buku WHERE judul_buku = ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        pengembalian = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_kode_peminjaman = new javax.swing.JTextField();
        txt_nama = new javax.swing.JTextField();
        btn_tambah = new javax.swing.JButton();
        status_pengembalian = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_petugas = new javax.swing.JTextField();
        txt_kode_buku = new javax.swing.JTextField();
        txt_judul_buku = new javax.swing.JTextField();
        txt_kode_pengembalian = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        kondisi_buku = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_denda = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        spinner_kembali = new javax.swing.JSpinner();
        spinner_pinjam = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        waktu_pengembalian = new javax.swing.JComboBox<>();
        tanggal_pinjam = new com.toedter.calendar.JDateChooser();
        tanggal_kembali_awal = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        tanggal_kembali_akhir = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_pending = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txt_search = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_return = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        txt_search1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        jDialog1.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jDialog1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDialog1.setUndecorated(true);

        pengembalian.setBackground(new java.awt.Color(255, 255, 255));
        pengembalian.setBorder(new javax.swing.border.MatteBorder(null));
        pengembalian.setPreferredSize(new java.awt.Dimension(990, 700));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setText("Kode Peminjaman");

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel4.setText("Tanggal Peminjaman");

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel5.setText("Total Pinjam");

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel6.setText("Nama");

        txt_kode_peminjaman.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_kode_peminjaman.setEnabled(false);

        txt_nama.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_nama.setEnabled(false);

        btn_tambah.setBackground(new java.awt.Color(0, 255, 0));
        btn_tambah.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        btn_tambah.setText("Submit");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        status_pengembalian.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        status_pengembalian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "dipinjam", "kembali" }));
        status_pengembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                status_pengembalianActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel7.setText("Tanggal Kembali");

        jButton1.setBackground(new java.awt.Color(240, 0, 0));
        jButton1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jButton1.setText("Cancel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel8.setText("Petugas");

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel9.setText("Kode Buku");

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel10.setText("Judul Buku");

        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel11.setText("Waktu Peminjaman");
        jLabel11.setPreferredSize(new java.awt.Dimension(169, 25));

        txt_petugas.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_petugas.setEnabled(false);

        txt_kode_buku.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_kode_buku.setEnabled(false);

        txt_judul_buku.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        txt_judul_buku.setEnabled(false);
        txt_judul_buku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_judul_bukuActionPerformed(evt);
            }
        });

        txt_kode_pengembalian.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        txt_kode_pengembalian.setEnabled(false);

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel12.setText("Kondisi");

        kondisi_buku.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        kondisi_buku.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Hilang" }));

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel13.setText("Kode Pengembalian");

        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel14.setText("Denda");

        txt_denda.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel15.setText("Total Kembali");

        spinner_kembali.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_kembali.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_kembali.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        spinner_kembali.setPreferredSize(new java.awt.Dimension(37, 25));
        spinner_kembali.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_kembaliStateChanged(evt);
            }
        });
        spinner_kembali.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                spinner_kembaliMouseClicked(evt);
            }
        });
        spinner_kembali.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                spinner_kembaliPropertyChange(evt);
            }
        });
        spinner_kembali.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                spinner_kembaliVetoableChange(evt);
            }
        });

        spinner_pinjam.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_pinjam.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_pinjam.setPreferredSize(new java.awt.Dimension(37, 25));

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel16.setText("Status Peminjaman");
        jLabel16.setPreferredSize(new java.awt.Dimension(169, 25));

        waktu_pengembalian.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        waktu_pengembalian.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "tepat waktu", "telat" }));
        waktu_pengembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                waktu_pengembalianActionPerformed(evt);
            }
        });

        tanggal_pinjam.setDateFormatString("yyyy-MM-dd");
        tanggal_pinjam.setEnabled(false);

        tanggal_kembali_awal.setDateFormatString("yyyy-MM-dd");
        tanggal_kembali_awal.setEnabled(false);

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel17.setText("Tanggal Kembali");

        tanggal_kembali_akhir.setDateFormatString("yyyy-MM-dd");
        tanggal_kembali_akhir.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                tanggal_kembali_akhirAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        tanggal_kembali_akhir.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tanggal_kembali_akhirFocusLost(evt);
            }
        });
        tanggal_kembali_akhir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tanggal_kembali_akhirMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tanggal_kembali_akhirMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tanggal_kembali_akhirMousePressed(evt);
            }
        });
        tanggal_kembali_akhir.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tanggal_kembali_akhirPropertyChange(evt);
            }
        });
        tanggal_kembali_akhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tanggal_kembali_akhirKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout pengembalianLayout = new javax.swing.GroupLayout(pengembalian);
        pengembalian.setLayout(pengembalianLayout);
        pengembalianLayout.setHorizontalGroup(
            pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pengembalianLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_kode_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(status_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(waktu_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(pengembalianLayout.createSequentialGroup()
                                    .addComponent(spinner_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(spinner_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pengembalianLayout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(162, 162, 162)
                                    .addComponent(jLabel15))
                                .addGroup(pengembalianLayout.createSequentialGroup()
                                    .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(tanggal_kembali_awal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pengembalianLayout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel17))
                                        .addGroup(pengembalianLayout.createSequentialGroup()
                                            .addGap(63, 63, 63)
                                            .addComponent(tanggal_kembali_akhir, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)))
                                    .addGap(4, 4, 4))))
                        .addGap(154, 154, 154)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel12)
                            .addComponent(jLabel9)
                            .addComponent(txt_kode_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(txt_judul_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(kondisi_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(tanggal_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addGap(158, 158, 158)
                                .addComponent(jLabel8)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txt_petugas, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(405, 405, 405))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(txt_kode_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(291, 291, 291))
        );
        pengembalianLayout.setVerticalGroup(
            pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pengembalianLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_kode_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_petugas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tanggal_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel17))
                        .addGap(11, 11, 11)))
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_kode_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tanggal_kembali_awal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(pengembalianLayout.createSequentialGroup()
                                    .addGap(28, 28, 28)
                                    .addComponent(jLabel15))
                                .addGroup(pengembalianLayout.createSequentialGroup()
                                    .addGap(22, 22, 22)
                                    .addComponent(jLabel10)))
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addGap(4, 4, 4)))
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(spinner_pinjam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinner_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_judul_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(status_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(kondisi_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(22, 22, 22)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(waktu_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_denda, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(txt_kode_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(69, 69, 69))
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addComponent(tanggal_kembali_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(457, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 990, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 990, Short.MAX_VALUE)
            .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jDialog1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
            .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jDialog1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                formAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(1399, 472));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel20.setText("PENDING");

        tabel_pending.setModel(new javax.swing.table.DefaultTableModel(
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
                false, false, false, false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_pending.getTableHeader().setReorderingAllowed(false);
        tabel_pending.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_pendingMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabel_pending);
        if (tabel_pending.getColumnModel().getColumnCount() > 0) {
            tabel_pending.getColumnModel().getColumn(0).setResizable(false);
            tabel_pending.getColumnModel().getColumn(1).setResizable(false);
            tabel_pending.getColumnModel().getColumn(2).setResizable(false);
            tabel_pending.getColumnModel().getColumn(3).setResizable(false);
            tabel_pending.getColumnModel().getColumn(4).setResizable(false);
            tabel_pending.getColumnModel().getColumn(5).setResizable(false);
            tabel_pending.getColumnModel().getColumn(6).setResizable(false);
        }

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_searchKeyReleased(evt);
            }
        });
        jPanel2.add(txt_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 10, 100, 20));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-search-18.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 20, 20));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1359, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel21.setText("RETURN");

        tabel_return.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Peminjaman", "Nama Anggota", "Petugas", "Jumlah Pinjam", "Status Kembali", "Waktu Kembali", "Kondisi Buku", "Judul Buku", "Kode Buku", "Denda"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        tabel_return.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_return);
        if (tabel_return.getColumnModel().getColumnCount() > 0) {
            tabel_return.getColumnModel().getColumn(0).setResizable(false);
            tabel_return.getColumnModel().getColumn(1).setResizable(false);
            tabel_return.getColumnModel().getColumn(2).setResizable(false);
            tabel_return.getColumnModel().getColumn(3).setResizable(false);
            tabel_return.getColumnModel().getColumn(4).setResizable(false);
            tabel_return.getColumnModel().getColumn(5).setResizable(false);
            tabel_return.getColumnModel().getColumn(6).setResizable(false);
            tabel_return.getColumnModel().getColumn(7).setResizable(false);
            tabel_return.getColumnModel().getColumn(8).setResizable(false);
            tabel_return.getColumnModel().getColumn(9).setResizable(false);
        }

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_search1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_search1KeyReleased(evt);
            }
        });
        jPanel3.add(txt_search1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 10, 100, 20));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-search-18.png"))); // NOI18N
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 20, 20));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1359, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tabel_pendingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_pendingMouseClicked
        int index = tabel_pending.getSelectedRow();
        TableModel model = tabel_pending.getModel();
        String kodepeminjaman = model.getValueAt(index, 0).toString();
        String tanggalpinjam = model.getValueAt(index, 3).toString();
        String tanggalkembali = model.getValueAt(index, 4).toString();
        String totalpinjam = model.getValueAt(index, 5).toString();
        String nisn = model.getValueAt(index, 1).toString();
        String idpetugas = model.getValueAt(index, 2).toString();
        String judulbuku = model.getValueAt(index, 7).toString();
        String kodebuku = model.getValueAt(index, 6).toString();

        txt_kode_peminjaman.setText(kodepeminjaman);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date tanggalKembaliDate = dateFormat.parse(tanggalpinjam);
            tanggal_pinjam.setDate(tanggalKembaliDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date tanggalKembaliDate = dateFormat.parse(tanggalkembali);
            tanggal_kembali_awal.setDate(tanggalKembaliDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        int totalPinjamInt = Integer.parseInt(totalpinjam);
        spinner_pinjam.setValue(totalPinjamInt);
        txt_nama.setText(nisn);
        txt_petugas.setText(idpetugas);
        txt_judul_buku.setText(judulbuku);
        txt_kode_buku.setText(kodebuku);
        spinner_pinjam.enable(false);

        jDialog1.setVisible(true);
    }//GEN-LAST:event_tabel_pendingMouseClicked

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        {

            String selectedStatus = (String) status_pengembalian.getSelectedItem();
            int jumlahpeminjaman = (int) spinner_pinjam.getValue();
            String namabuku = txt_judul_buku.getText();
            String kodebuku = txt_kode_buku.getText();
            String kodepeminjaman = txt_kode_peminjaman.getText();

            // Query UPDATE
            String sqlUpdate = "UPDATE detail_peminjaman SET status_peminjaman = ?, jumlah_peminjaman = ? WHERE kode_peminjaman = ? AND No_buku = (SELECT No_buku from buku where judul_buku = ?)";

            try (Connection conn = koneksi.Koneksi(); PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdate)) {

                pstUpdate.setString(1, selectedStatus);
                pstUpdate.setInt(2, jumlahpeminjaman);
                pstUpdate.setString(3, kodepeminjaman);  // Urutan parameter sesuai dengan urutan pada query
                pstUpdate.setString(4, namabuku);
                int rowsAffected = pstUpdate.executeUpdate();

                String kodepengembalian = txt_kode_pengembalian.getText();
                String kodepeminjaman1 = txt_kode_peminjaman.getText();
                String petugas = txt_petugas.getText();

                // Insert data ke tabel 'pengembalian'
                String sqlInsertPengembalian = "INSERT INTO pengembalian (kode_pengembalian, kode_peminjaman, ID_users) VALUES (?, ?, (SELECT ID_users FROM users WHERE username = ?))";
                try (PreparedStatement pstInsertPengembalian = conn.prepareStatement(sqlInsertPengembalian)) {
                    pstInsertPengembalian.setString(1, kodepengembalian);
                    pstInsertPengembalian.setString(2, kodepeminjaman1);
                    pstInsertPengembalian.setString(3, petugas);

                    pstInsertPengembalian.executeUpdate();
                } catch (Exception r) {
                    System.out.println("ins pengembalian" + r);
                }

                String kodepengembalian1 = txt_kode_pengembalian.getText();
                String selectedStatus1 = (String) status_pengembalian.getSelectedItem();
                String waktupengembalian = (String) waktu_pengembalian.getSelectedItem();
                String kondisibuku = (String) kondisi_buku.getSelectedItem();
                int jumlahkembali = (int) spinner_kembali.getValue();
                String kodebuku1 = txt_kode_buku.getText();
                int denda = Integer.parseInt(txt_denda.getText());
                String nama = txt_nama.getText();

                // Insert data ke tabel 'detail_pengembalian'
                String sqlInsertDetailPengembalian = "INSERT INTO detail_pengembalian VALUES (?, ?, ?, ?, ?, ?, (SELECT No_buku from buku where judul_buku = ?), ?, (SELECT NISN FROM anggota WHERE nama = ?))";
                try (PreparedStatement pstInsertDetailPengembalian = conn.prepareStatement(sqlInsertDetailPengembalian)) {
                    pstInsertDetailPengembalian.setString(1, kodepengembalian1);
                    pstInsertDetailPengembalian.setString(2, selectedStatus1);
                    pstInsertDetailPengembalian.setString(3, waktupengembalian);
                    pstInsertDetailPengembalian.setString(4, kondisibuku);
                    java.sql.Date tanggalPinjamSQL = new java.sql.Date(tanggal_pinjam.getDate().getTime());
                    pstInsertDetailPengembalian.setDate(5, tanggalPinjamSQL);
                    pstInsertDetailPengembalian.setInt(6, jumlahkembali);
                    pstInsertDetailPengembalian.setString(7, namabuku);
                    pstInsertDetailPengembalian.setInt(8, denda);
                    pstInsertDetailPengembalian.setString(9, nama);

                    System.out.println(pstInsertDetailPengembalian);

                    pstInsertDetailPengembalian.executeUpdate();

                    try {
                        pst = con.prepareStatement("Select nama, jumlah_denda from denda where nama = '" + nama + "'");
                        rs = pst.executeQuery();
                        rs.next();
                        int jml_denda = rs.getInt(2);
                        int tot_denda = jml_denda + denda;
                        if (rs.next()) {
                            pst = con.prepareStatement("INSERT INTO denda (jumlah_denda, status_denda, total_pembayaran, NISN, No_buku, kode_pengembalian) VALUES (\n"
                                    + "?, 'Belum Lunas', 0, (SELECT NISN FROM anggota WHERE nama = ?), (SELECT No_buku from buku where judul_buku = ?), ?);");
                            pst.setInt(1, denda);
                            pst.setString(2, nama);
                            pst.setString(3, namabuku);
                            pst.setString(4, kodepengembalian);
                            pst.execute();
                        } else {
                            pst = con.prepareStatement("update denda set jumlah_denda = ? where nama = ?");
                            pst.setInt(1, tot_denda);
                            pst.setString(2, nama);
                            pst.execute();
                        }
                    } catch (Exception e) {
                    }

                    pst = con.prepareStatement("delete from denda where jumlah_denda = 0");
                    pst.execute();
                } catch (Exception e) {
                    System.out.println("ins detPengembalian" + e);
                }

                // Update status kondisi buku
                String kondisiBukuStatus = (Integer.parseInt(spinner_pinjam.getValue().toString()) != 0) ? "kembali" : "kembali"; // Ubah sesuai kebutuhan
                JOptionPane.showMessageDialog(null, "Update berhasil");
                spinner_kembali.setValue(0);
                tanggal_kembali_akhir.setDate(null);

                jDialog1.dispose();
                masuktabelreturn();
                id_autoincrement();
                tambahStokBuku(conn, namabuku, jumlahkembali);
                load_table();
                // Tambahkan stok buku

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void status_pengembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_status_pengembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_status_pengembalianActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        spinner_kembali.setValue(0);
        tanggal_kembali_akhir.setDate(null);

        jDialog1.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txt_judul_bukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_judul_bukuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_judul_bukuActionPerformed

    private void spinner_kembaliStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinner_kembaliStateChanged
        // TODO add your handling code here:
        Object kmbl = spinner_kembali.getValue();
        Object pnjm = spinner_pinjam.getValue();
        String str = String.valueOf(kmbl);
        int jmlh_pnjm = Integer.parseInt(String.valueOf(pnjm));
        int jmlh_Kem = Integer.parseInt(str);
        System.out.println(jmlh_Kem);
        if (jmlh_Kem <= 0) {
            txt_denda.setText("0");
        } else {
            if (tanggal_kembali_akhir.getDate() != null) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String format_kembali = format.format(tanggal_kembali_akhir.getDate());
                String format_pinjam = format.format(tanggal_kembali_awal.getDate());

                System.out.println(format_pinjam);
                System.out.println(format_kembali);
                try {
                    pst = con.prepareStatement("SELECT datediff('" + format_kembali + "', '" + format_pinjam + "')");
                    rs = pst.executeQuery();
                    rs.next();
                    int selisih = rs.getInt(1);
                    if (selisih >= 0) {
                        int total_denda = selisih * 1000;
                        int total_keseluruhan = jmlh_Kem * total_denda;

                        txt_denda.setText(Integer.toString(total_keseluruhan));
                    } else {
                        JOptionPane.showMessageDialog(pengembalian, "Pastikan ada memasukkan tanggal pengembalian yang benar");
                        spinner_kembali.setValue(jmlh_Kem - 1);
                    }

                } catch (Exception e) {
                    System.out.println("dateDenda" + e);
                }
            } else if (jmlh_Kem >= jmlh_pnjm) {
                JOptionPane.showMessageDialog(pengembalian, "Jumlah Pengembalian tidak boleh lebih dari buku yang di pinjam");
                spinner_kembali.setValue(jmlh_Kem - 1);
            } else {
                JOptionPane.showMessageDialog(pengembalian, "Harap Isi Tanggal Kembali Terlebih Dahulu");
                spinner_kembali.setValue(0);
            }
        }
    }//GEN-LAST:event_spinner_kembaliStateChanged

    private void waktu_pengembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waktu_pengembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_waktu_pengembalianActionPerformed

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        load_table();
    }//GEN-LAST:event_formAncestorAdded

    private void tanggal_kembali_akhirAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_tanggal_kembali_akhirAncestorAdded

    }//GEN-LAST:event_tanggal_kembali_akhirAncestorAdded

    private void tanggal_kembali_akhirMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tanggal_kembali_akhirMousePressed

    }//GEN-LAST:event_tanggal_kembali_akhirMousePressed

    private void tanggal_kembali_akhirMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tanggal_kembali_akhirMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_tanggal_kembali_akhirMouseEntered

    private void txt_searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyReleased
        String keyword = txt_search.getText().trim();
        String sql = "SELECT peminjaman.kode_peminjaman,anggota.nama,users.username,detail_peminjaman.tanggal_peminjaman,detail_peminjaman.tanggal_kembali, detail_peminjaman.jumlah_peminjaman, buku.kode_buku,buku.judul_buku, detail_peminjaman.status_peminjaman \n"
                + "FROM peminjaman \n"
                + "LEFT JOIN detail_peminjaman ON peminjaman.kode_peminjaman = detail_peminjaman.kode_peminjaman \n"
                + "LEFT JOIN anggota ON peminjaman.NISN = anggota.NISN \n"
                + "LEFT JOIN buku ON detail_peminjaman.No_buku = buku.No_buku \n"
                + "LEFT JOIN users ON peminjaman.ID_users = users.ID_users "
                + "WHERE anggota.nama LIKE '%" + keyword + "%' OR "
                + "buku.judul_buku LIKE '%" + keyword + "%' OR "
                + "detail_peminjaman.jumlah_peminjaman LIKE '%" + keyword + "%'";

        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            // Creating a model to store the filtered data
            DefaultTableModel filteredModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Membuat semua sel tidak dapat diedit
                    return false;
                }
            };

            filteredModel.addColumn("Kode Peminjaman");
            filteredModel.addColumn("Nama");
            filteredModel.addColumn("Petugas");
            filteredModel.addColumn("Tanggal Pinjam");
            filteredModel.addColumn("Tanggal Kembali");
            filteredModel.addColumn("Jumlah Pinjam");
            filteredModel.addColumn("Kode Buku");
            filteredModel.addColumn("Judul Buku");

            while (rs.next()) {
                filteredModel.addRow(new Object[]{
                    rs.getString("kode_peminjaman"),
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("tanggal_peminjaman"),
                    rs.getString("tanggal_kembali"),
                    rs.getString("jumlah_peminjaman"),
                    rs.getString("kode_buku"),
                    rs.getString("judul_buku"), // ... (add other columns as needed)
                });
            }

            // Set the filtered model to the JTable
            tabel_pending.setModel(filteredModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_txt_searchKeyReleased

    private void txt_search1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search1KeyReleased
        String keyword = txt_search1.getText().trim();
        String sql = "SELECT pengembalian.kode_pengembalian, anggota.nama, users.username, detail_pengembalian.jumlah_pengembalian, detail_pengembalian.status_pengembalian,detail_pengembalian.waktu_pengembalian, buku.kode_buku,buku.judul_buku,detail_pengembalian.kondisi_buku, detail_pengembalian.denda FROM pengembalian JOIN detail_pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN buku ON detail_pengembalian.No_buku = buku.No_buku JOIN users ON pengembalian.id_users = users.id_users JOIN anggota ON detail_pengembalian.NISN = anggota.NISN WHERE anggota.nama LIKE '%" + keyword + "%' OR "
                + "buku.judul_buku LIKE '%" + keyword + "%' OR "
                + "detail_pengembalian.status_pengembalian LIKE '%" + keyword + "%' OR "
                + "detail_pengembalian.waktu_pengembalian LIKE '%" + keyword + "%' OR "
                + "detail_pengembalian.kondisi_buku LIKE '%" + keyword + "%' OR "
                + "detail_pengembalian.jumlah_pengembalian LIKE '%" + keyword + "%'";

        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            // Creating a model to store the filtered data
            DefaultTableModel filteredModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    // Membuat semua sel tidak dapat diedit
                    return false;
                }
            };
            filteredModel.addColumn("Kode Pengembalian");
            filteredModel.addColumn("Nama");
            filteredModel.addColumn("Petugas");
            filteredModel.addColumn("Jumlah Pengembalian");
            filteredModel.addColumn("Status Pengembalian");
            filteredModel.addColumn("Waktu Pengembalian");
            filteredModel.addColumn("Kode Buku");
            filteredModel.addColumn("Judul Buku");
            filteredModel.addColumn("Kondisi Buku");
            filteredModel.addColumn("Denda");

            while (rs.next()) {
                filteredModel.addRow(new Object[]{
                    rs.getString("kode_pengembalian"),
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("jumlah_pengembalian"),
                    rs.getString("status_pengembalian"),
                    rs.getString("waktu_pengembalian"),
                    rs.getString("kode_buku"), // Pastikan nama kolom sesuai dengan hasil query
                    rs.getString("judul_buku"),
                    rs.getString("kondisi_buku"),
                    rs.getString("denda"), // ... (add other columns as needed)
                });
            }

            // Set the filtered model to the JTable
            tabel_return.setModel(filteredModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_txt_search1KeyReleased

    private void tanggal_kembali_akhirPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tanggal_kembali_akhirPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_tanggal_kembali_akhirPropertyChange

    private void tanggal_kembali_akhirFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tanggal_kembali_akhirFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_tanggal_kembali_akhirFocusLost

    private void tanggal_kembali_akhirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tanggal_kembali_akhirKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tanggal_kembali_akhirKeyPressed

    private void tanggal_kembali_akhirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tanggal_kembali_akhirMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tanggal_kembali_akhirMouseClicked

    private void spinner_kembaliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_spinner_kembaliMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_spinner_kembaliMouseClicked

    private void spinner_kembaliPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_spinner_kembaliPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_spinner_kembaliPropertyChange

    private void spinner_kembaliVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_spinner_kembaliVetoableChange
        // TODO add your handling code here:
    }//GEN-LAST:event_spinner_kembaliVetoableChange

    DefaultTableModel model = new DefaultTableModel() {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JComboBox<String> kondisi_buku;
    private javax.swing.JPanel pengembalian;
    private javax.swing.JSpinner spinner_kembali;
    public javax.swing.JSpinner spinner_pinjam;
    public javax.swing.JComboBox<String> status_pengembalian;
    private javax.swing.JTable tabel_pending;
    private javax.swing.JTable tabel_return;
    private com.toedter.calendar.JDateChooser tanggal_kembali_akhir;
    public com.toedter.calendar.JDateChooser tanggal_kembali_awal;
    public com.toedter.calendar.JDateChooser tanggal_pinjam;
    public javax.swing.JTextField txt_denda;
    public javax.swing.JTextField txt_judul_buku;
    public javax.swing.JTextField txt_kode_buku;
    public javax.swing.JTextField txt_kode_peminjaman;
    public javax.swing.JTextField txt_kode_pengembalian;
    public javax.swing.JTextField txt_nama;
    public javax.swing.JTextField txt_petugas;
    private javax.swing.JTextField txt_search;
    private javax.swing.JTextField txt_search1;
    public javax.swing.JComboBox<String> waktu_pengembalian;
    // End of variables declaration//GEN-END:variables
}
