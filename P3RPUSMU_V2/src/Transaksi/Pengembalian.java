/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transaksi;

import Login.Config;
import Navbar.koneksi;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
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
        txt_denda_total.setText("0");
        txt_denda_telat.setVisible(false);
        txt_denda_kondisi.setVisible(false);

        tabel_return.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel_return.getTableHeader().setForeground(Color.white);

        tabel_pending.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel_pending.getTableHeader().setForeground(Color.white);

        tabel_pending.getColumnModel().getColumn(0).setPreferredWidth(20);

        jDialog1.setSize(1000, 700);

    }

    public int getHargaBuku(String judul_buku) {
        int harga = 0;
        try {
            String query = "SELECT harga FROM buku WHERE judul_buku = ?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, judul_buku);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                harga = resultSet.getInt("harga");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return harga;
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
            txt_kode_pengembalian.setText(newId); // Set the new ID to the text field
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
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
        model.addColumn("Status Peminjaman");

        try {
            loadtable2();
            pst = con.prepareStatement("delete from detail_peminjaman where jumlah_peminjaman = 0");
            pst.execute();
            String sql = "SELECT peminjaman.kode_peminjaman, anggota.nama, users.username, detail_peminjaman.tanggal_peminjaman, detail_peminjaman.tanggal_kembali, detail_peminjaman.jumlah_peminjaman, buku.kode_buku, buku.judul_buku, detail_peminjaman.status_peminjaman FROM peminjaman JOIN users ON peminjaman.id_users = users.id_users JOIN detail_peminjaman ON peminjaman.kode_peminjaman = detail_peminjaman.kode_peminjaman JOIN buku ON detail_peminjaman.No_buku = buku.No_buku JOIN anggota ON peminjaman.NISN = anggota.NISN;";

            Statement stm = con.createStatement();
            ResultSet res = stm.executeQuery(sql);

            while (res.next()) {
                String statusPeminjaman;
                int jumlahPeminjaman = res.getInt("jumlah_peminjaman");

                if (jumlahPeminjaman != 0) {
                    statusPeminjaman = "Dipinjam";
                } else {
                    statusPeminjaman = "Kembali";
                }
                model.addRow(new Object[]{
                    res.getString("kode_peminjaman"),
                    res.getString("nama"),
                    res.getString("username"),
                    res.getString("tanggal_peminjaman"),
                    res.getString("tanggal_kembali"),
                    res.getString("jumlah_peminjaman"), // Pastikan nama kolom sesuai dengan hasil query
                    res.getString("kode_buku"),
                    res.getString("judul_buku"),
                    res.getString("status_peminjaman"),});
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

            Statement stm = con.createStatement();
            ResultSet res = stm.executeQuery(sql);

            while (res.next()) {
                String statusPeminjaman;
                int jumlahPeminjaman = res.getInt("jumlah_pengembalian");

                if (jumlahPeminjaman != 0) {
                    statusPeminjaman = "kembali";
                } else {
                    statusPeminjaman = "Kembali";
                }
                model.addRow(new Object[]{
                    res.getString("kode_pengembalian"),
                    res.getString("nama"),
                    res.getString("username"),
                    res.getString("jumlah_pengembalian"),
                    res.getString("status_pengembalian"),
                    res.getString("waktu_pengembalian"),
                    res.getString("kode_buku"), // Pastikan nama kolom sesuai dengan hasil query
                    res.getString("judul_buku"),
                    res.getString("kondisi_buku"),
                    res.getString("denda"),});
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
            resultSet.close();
            statement.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return kodeBuku;
    }

    private void masuktabelpending() {
        DefaultTableModel model = (DefaultTableModel) tabel_pending.getModel();
        // Mendapatkan teks dari field-field yang relevan
        String kodepeminjaman = txt_kode_peminjaman.getText();
        String nama = txt_nama.getText();
        String username = txt_petugas.getText();
        java.sql.Date tanggalpinjam = new java.sql.Date(tanggal_pinjam.getDate().getTime());
        java.sql.Date tanggalkembali = new java.sql.Date(tanggal_kembali_awal.getDate().getTime());
        int totalpeminjaman = (int) spinner_pinjam.getValue();
        String kodebuku = txt_kode_buku.getText();
        String judulbuku = txt_judul_buku.getText();
        String statuskembali;

        if (totalpeminjaman != 0) {
            statuskembali = "Dipinjam";
        } else {
            statuskembali = "Kembali";
        }

        // Tambahkan baris baru ke dalam tabel
        Object[] row = {kodepeminjaman, nama, username, tanggalpinjam, tanggalkembali, totalpeminjaman, kodebuku, judulbuku, statuskembali};
        model.addRow(row);
    }

    private void masuktabelreturn() {
        DefaultTableModel model = (DefaultTableModel) tabel_return.getModel();
        // Mendapatkan teks dari field-field yang relevan
        String kodepeminjaman = txt_kode_peminjaman.getText();
        String nama = txt_nama.getText();
        String username = txt_petugas.getText();
        int totalpeminjaman = (int) spinner_kembali.getValue();
        String statuskembali = "Kembali"; // Mengatur status menjadi "Kembali" untuk semua data

        // Mendapatkan tanggal kembali awal dan tanggal kembali akhir
        Date tanggalKembaliAwal = tanggal_kembali_awal.getDate();
        Date tanggalKembaliAkhir = tanggal_kembali_akhir.getDate();

        // Mendapatkan waktukembali berdasarkan perbandingan tanggal
        String waktukembali;
        if (tanggalKembaliAkhir.after(tanggalKembaliAwal)) {
            waktukembali = "Telat";
        } else {
            waktukembali = "Tepat Waktu";
        }

        String kodebuku = txt_kode_buku.getText();
        String judulbuku = txt_judul_buku.getText();
        String kondisikembali = (String) kondisi_buku.getSelectedItem();
        String denda = txt_denda_total.getText();

        // Tambahkan baris baru ke dalam tabel
        Object[] row = {kodepeminjaman, nama, username, totalpeminjaman, statuskembali, waktukembali, kodebuku, judulbuku, kondisikembali, denda};
        model.addRow(row);
    }

    private void tambahStokBuku(Connection con, int jumlahPengembalian, String kodeBuku) throws SQLException {
        String sql = "UPDATE buku SET jumlah_stock = jumlah_stock + ? WHERE No_buku = (SELECT No_buku FROM buku WHERE judul_buku = ?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, jumlahPengembalian);
            pst.setString(2, kodeBuku);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Gagal menambahkan stok buku");
            }
        }
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
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_petugas = new javax.swing.JTextField();
        txt_kode_buku = new javax.swing.JTextField();
        txt_judul_buku = new javax.swing.JTextField();
        txt_kode_pengembalian = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_denda_total = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        spinner_kembali = new javax.swing.JSpinner();
        spinner_pinjam = new javax.swing.JSpinner();
        tanggal_pinjam = new com.toedter.calendar.JDateChooser();
        tanggal_kembali_awal = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        tanggal_kembali_akhir = new com.toedter.calendar.JDateChooser();
        kondisi_buku = new javax.swing.JComboBox<>();
        txt_denda_telat = new javax.swing.JTextField();
        txt_denda_kondisi = new javax.swing.JTextField();
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

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel13.setText("Kode Pengembalian");

        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel14.setText("Denda");

        txt_denda_total.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txt_denda_total.setEnabled(false);
        txt_denda_total.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                txt_denda_totalAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        txt_denda_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_denda_totalActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel15.setText("Total Kembali");

        spinner_kembali.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_kembali.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_kembali.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        spinner_kembali.setPreferredSize(new java.awt.Dimension(37, 25));
        spinner_kembali.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                spinner_kembaliAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        spinner_kembali.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_kembaliStateChanged(evt);
            }
        });

        spinner_pinjam.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_pinjam.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_pinjam.setPreferredSize(new java.awt.Dimension(37, 25));
        spinner_pinjam.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_pinjamStateChanged(evt);
            }
        });

        tanggal_pinjam.setDateFormatString("yyyy-MM-dd");
        tanggal_pinjam.setEnabled(false);

        tanggal_kembali_awal.setDateFormatString("yyyy-MM-dd");
        tanggal_kembali_awal.setEnabled(false);

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel17.setText("Tanggal Kembali");

        tanggal_kembali_akhir.setDateFormatString("yyyy-MM-dd");
        tanggal_kembali_akhir.setEnabled(false);
        tanggal_kembali_akhir.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                tanggal_kembali_akhirAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        tanggal_kembali_akhir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tanggal_kembali_akhirMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tanggal_kembali_akhirMousePressed(evt);
            }
        });

        kondisi_buku.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        kondisi_buku.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Hilang" }));
        kondisi_buku.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                kondisi_bukuItemStateChanged(evt);
            }
        });

        txt_denda_kondisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_denda_kondisiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pengembalianLayout = new javax.swing.GroupLayout(pengembalian);
        pengembalian.setLayout(pengembalianLayout);
        pengembalianLayout.setHorizontalGroup(
            pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                                    .addComponent(spinner_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(spinner_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(162, 162, 162)
                                    .addComponent(jLabel15))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                                    .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addComponent(tanggal_kembali_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pengembalianLayout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel17))
                                        .addGroup(pengembalianLayout.createSequentialGroup()
                                            .addGap(63, 63, 63)
                                            .addComponent(tanggal_kembali_akhir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGap(4, 4, 4))
                                .addComponent(jLabel12)
                                .addComponent(kondisi_buku, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(158, 158, 158)
                            .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel14)
                                .addComponent(jLabel9)
                                .addComponent(txt_kode_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel10)
                                .addComponent(txt_judul_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_denda_total, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGap(252, 252, 252)
                        .addComponent(txt_denda_telat, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(217, 217, 217)
                        .addComponent(txt_denda_kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(405, 405, 405))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1))
                            .addComponent(txt_kode_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(295, 295, 295))))
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5)
                                .addGap(4, 4, 4)))
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(spinner_pinjam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spinner_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_judul_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14)))
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addComponent(tanggal_kembali_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(162, 162, 162)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_denda_total, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kondisi_buku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(txt_kode_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_denda_kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_denda_telat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59))
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
        int selectedRow = tabel_pending.getSelectedRow();
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
        // Mengatur tanggal_kembali_akhir dengan tanggal sekarang
        Date currentDate = new Date(); // Mendapatkan tanggal saat ini
        tanggal_kembali_akhir.setDate(currentDate);
        spinner_kembali.setValue(0);
        kondisi_buku.setSelectedItem("Baik");

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

        // Menghapus baris yang diklik dari tabel
        ((DefaultTableModel) model).removeRow(selectedRow);

        jDialog1.setVisible(true);
    }//GEN-LAST:event_tabel_pendingMouseClicked

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        int jumlahpeminjaman = (int) spinner_pinjam.getValue();
        String kodebuku = txt_judul_buku.getText();
        String kodepeminjaman = txt_kode_peminjaman.getText();
        String kodepengembalian = txt_kode_pengembalian.getText();
        String petugas = txt_petugas.getText();

        String kondisiBukuStatus;
        if (jumlahpeminjaman != 0) {
            kondisiBukuStatus = "Dipinjam";
        } else {
            kondisiBukuStatus = "Kembali";
        }

        String kondisibuku = (String) kondisi_buku.getSelectedItem();
        int jumlahkembali = (int) spinner_kembali.getValue();
        String kodebuku1 = txt_judul_buku.getText();
        String denda = txt_denda_total.getText();
        String nama = txt_nama.getText();

        try {
            con.setAutoCommit(false); // Mulai transaksi

            // Update detail peminjaman
            String sqlUpdate = "UPDATE detail_peminjaman SET status_peminjaman = ?, jumlah_peminjaman = ? WHERE kode_peminjaman = ? AND No_buku = (SELECT No_buku FROM buku WHERE judul_buku = ?)";
            try (PreparedStatement pstUpdate = con.prepareStatement(sqlUpdate)) {
                pstUpdate.setString(1, kondisiBukuStatus);
                pstUpdate.setInt(2, jumlahpeminjaman);
                pstUpdate.setString(3, kodepeminjaman);
                pstUpdate.setString(4, kodebuku);
                masuktabelpending();
                int rowsAffected = pstUpdate.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Update gagal. Tidak ada baris yang terpengaruh.");
                }
            }

            // Insert data ke tabel 'pengembalian'
            String sqlInsertPengembalian = "INSERT INTO pengembalian (kode_pengembalian, kode_peminjaman, ID_users) VALUES (?, ?, (SELECT ID_users FROM users WHERE username = ?LIMIT 1))";
            try (PreparedStatement pstInsertPengembalian = con.prepareStatement(sqlInsertPengembalian)) {
                pstInsertPengembalian.setString(1, kodepengembalian);
                pstInsertPengembalian.setString(2, kodepeminjaman);
                pstInsertPengembalian.setString(3, petugas);
                pstInsertPengembalian.executeUpdate();
            }

            // Insert data ke tabel 'detail_pengembalian'
            String sqlInsertDetailPengembalian = "INSERT INTO detail_pengembalian VALUES (?, ?, ?, ?, ?, ?, (SELECT No_buku FROM buku WHERE judul_buku = ? LIMIT 1), ?, (SELECT NISN FROM anggota WHERE nama = ?LIMIT 1))";
            try (PreparedStatement pstInsertDetailPengembalian = con.prepareStatement(sqlInsertDetailPengembalian)) {
                // Set status menjadi "Kembali" untuk semua data yang diinsert ke detail_pengembalian
                String statusDetailPengembalian = "Kembali";

                // Mendapatkan tanggal kembali awal dan tanggal kembali akhir
                Date tanggalKembaliAwal = tanggal_kembali_awal.getDate();
                Date tanggalKembaliAkhir = tanggal_kembali_akhir.getDate();

                // Mendapatkan status waktu kembali berdasarkan perbandingan tanggal
                String waktupengembalian;
                if (tanggalKembaliAkhir != null && tanggalKembaliAwal != null) {
                    if (tanggalKembaliAkhir.after(tanggalKembaliAwal)) {
                        waktupengembalian = "Telat";
                    } else {
                        waktupengembalian = "Tepat Waktu";
                    }
                } else {
                    waktupengembalian = "Tidak Diketahui";
                }

                pstInsertDetailPengembalian.setString(1, kodepengembalian);
                pstInsertDetailPengembalian.setString(2, statusDetailPengembalian);
                pstInsertDetailPengembalian.setString(3, waktupengembalian);
                pstInsertDetailPengembalian.setString(4, kondisibuku);
                java.sql.Date tanggalPinjamSQL = new java.sql.Date(tanggal_pinjam.getDate().getTime());
                pstInsertDetailPengembalian.setDate(5, tanggalPinjamSQL);
                pstInsertDetailPengembalian.setInt(6, jumlahkembali);
                pstInsertDetailPengembalian.setString(7, kodebuku1);
                pstInsertDetailPengembalian.setString(8, denda);
                pstInsertDetailPengembalian.setString(9, nama);
                pstInsertDetailPengembalian.executeUpdate();
            }

            // Tambahkan logika untuk memasukkan data ke tabel 'denda' jika jumlah denda tidak sama dengan nol
            if (!denda.equals("0")) {
                // Cek apakah ada entri denda dengan NISN yang sama
                String sqlCheckExistingDenda = "SELECT COUNT(*) FROM denda WHERE NISN = (SELECT NISN FROM anggota WHERE nama = ? LIMIT 1)";
                try (PreparedStatement pstCheckExistingDenda = con.prepareStatement(sqlCheckExistingDenda)) {
                    pstCheckExistingDenda.setString(1, nama); // Gunakan NISN atau nama sesuai kebutuhan Anda
                    ResultSet rs = pstCheckExistingDenda.executeQuery();
                    rs.next();
                    int rowCount = rs.getInt(1);
                    if (rowCount > 0) {
                        // Jika ada, update jumlah denda yang ada dengan jumlah denda yang baru ditambahkan
                        String sqlUpdateDenda = "UPDATE denda SET jumlah_denda = jumlah_denda + ? WHERE NISN = (SELECT NISN FROM anggota WHERE nama = ? LIMIT 1)";
                        try (PreparedStatement pstUpdateDenda = con.prepareStatement(sqlUpdateDenda)) {
                            pstUpdateDenda.setString(1, denda);
                            pstUpdateDenda.setString(2, nama); // Gunakan NISN atau nama sesuai kebutuhan Anda
                            pstUpdateDenda.executeUpdate();
                        }
                    } else {
                        // Jika tidak ada, masukkan data baru ke tabel 'denda'
                        String sqlInsertDenda = "INSERT INTO denda (jumlah_denda, status_denda, total_pembayaran, NISN, No_buku, kode_pengembalian) VALUES (?, ?, ?, (SELECT NISN FROM anggota WHERE nama = ? LIMIT 1), (SELECT No_buku FROM buku WHERE judul_buku = ? LIMIT 1), ?)";
                        try (PreparedStatement pstInsertDenda = con.prepareStatement(sqlInsertDenda)) {
                            pstInsertDenda.setString(1, denda);
                            pstInsertDenda.setString(2, "Belum Lunas"); // Mengasumsikan status awal adalah 'Belum Lunas'
                            pstInsertDenda.setString(3, "0"); // Mengasumsikan total_pembayaran awal sama dengan jumlah_denda
                            pstInsertDenda.setString(4, nama);
                            pstInsertDenda.setString(5, kodebuku1);
                            pstInsertDenda.setString(6, kodepengembalian);
                            pstInsertDenda.executeUpdate();
                        }
                    }
                }
            }

            // Jika tabel 'denda' kosong, atur ID_denda ke 1
            String sqlCheckEmptyDenda = "SELECT COUNT(*) FROM denda";
            try (PreparedStatement pstCheckEmptyDenda = con.prepareStatement(sqlCheckEmptyDenda)) {
                ResultSet rs = pstCheckEmptyDenda.executeQuery();
                rs.next();
                int rowCount = rs.getInt(1);
                if (rowCount == 0) {
                    // Tabel 'denda' kosong, atur ID_denda ke 1
                    String sqlResetID = "ALTER TABLE denda AUTO_INCREMENT = 1";
                    try (PreparedStatement pstResetID = con.prepareStatement(sqlResetID)) {
                        pstResetID.executeUpdate();
                    }
                }
            }

            try {
                pst = con.prepareStatement("DELETE FROM denda WHERE jumlah_denda = 0");
                pst.execute();
            } catch (Exception e) {
                System.out.println("sat" + e);
            }
            // Tambahkan stok buku
            if (!kondisi_buku.getSelectedItem().equals("Hilang")) {
                tambahStokBuku(con, jumlahkembali, kodebuku1);

            }

            // Selesai transaksi
            con.commit();
            JOptionPane.showMessageDialog(null, "Update berhasil");
            masuktabelreturn();
            id_autoincrement();
            jDialog1.dispose();
            load_table();
            txt_denda_total.setText("0");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
        }
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        spinner_kembali.setValue(0);
        txt_denda_total.setText("0");
        kondisi_buku.setSelectedItem("Baik");
        txt_denda_telat.setText("0");
        txt_denda_kondisi.setText("0");

        jDialog1.dispose();
        load_table();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txt_judul_bukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_judul_bukuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_judul_bukuActionPerformed

    private void spinner_kembaliStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinner_kembaliStateChanged
        Object kmbl = spinner_kembali.getValue();
        Object pnjm = spinner_pinjam.getValue();
        String str = String.valueOf(kmbl);
        int jmlh_pnjm = Integer.parseInt(String.valueOf(pnjm));
        int jmlh_Kem = Integer.parseInt(str);
        txt_denda_kondisi.setText("0");
        System.out.println(jmlh_Kem);
        if (jmlh_Kem <= 0) {
            txt_denda_telat.setText("0");
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
                        txt_denda_telat.setText(Integer.toString(total_keseluruhan));
                        // Menangani langsung jika kondisi "Baik"
                        kondisi_buku.getSelectedItem().toString().equals("Baik");
                        // Mengambil nilai dari txt_denda_telat
                        int denda_telat = Integer.parseInt(txt_denda_telat.getText());

                        // Menetapkan nilai denda langsung ke txt_denda_total
                        txt_denda_total.setText(String.valueOf(denda_telat));
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

        // Recalculate the value of jmlh_pnjm based on the values of spinner_pinjam and spinner_kembali
        jmlh_pnjm = Integer.parseInt(spinner_pinjam.getValue().toString()) - 1;
        spinner_pinjam.setValue(jmlh_pnjm);
    }//GEN-LAST:event_spinner_kembaliStateChanged

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
        String sql = "SELECT peminjaman.kode_peminjaman,anggota.nama,users.username,detail_peminjaman.tanggal_peminjaman,detail_peminjaman.tanggal_kembali, detail_peminjaman.jumlah_peminjaman, buku.kode_buku,buku.judul_buku \n"
                + "FROM peminjaman \n"
                + "LEFT JOIN detail_peminjaman ON peminjaman.kode_peminjaman = detail_peminjaman.kode_peminjaman \n"
                + "LEFT JOIN anggota ON peminjaman.NISN = anggota.NISN \n"
                + "LEFT JOIN buku ON detail_peminjaman.No_buku = buku.No_buku \n"
                + "LEFT JOIN users ON peminjaman.ID_users = users.ID_users "
                + "WHERE anggota.nama LIKE '%" + keyword + "%' OR "
                + "buku.judul_buku LIKE '%" + keyword + "%' OR "
                + "detail_peminjaman.jumlah_peminjaman LIKE '%" + keyword + "%'";

        try {
//            java.sql.Connection con = (java.sql.Connection) Config.configDB();
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();

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

            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();

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

    private void spinner_kembaliAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_spinner_kembaliAncestorAdded

    }//GEN-LAST:event_spinner_kembaliAncestorAdded

    private void kondisi_bukuItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_kondisi_bukuItemStateChanged
        String kondisi = kondisi_buku.getSelectedItem().toString();
        String judul_buku = txt_judul_buku.getText();
        String kode_buku = txt_kode_buku.getText();
        int denda = 0;

        if (!txt_denda_kondisi.getText().isEmpty()) {
            denda = Integer.parseInt(txt_denda_kondisi.getText());
        }

        int harga_buku = getHargaBuku(judul_buku);
        System.out.println(harga_buku);

        if (kondisi.equals("Baik")) {
            denda = 0;
        } else if (kondisi.equals("Rusak")) {
            denda = harga_buku / 2;
        } else if (kondisi.equals("Hilang")) {
            denda = harga_buku;
        }

        txt_denda_kondisi.setText(String.valueOf(denda));

        // TODO add your handling code here:
        // Mengambil nilai dari txt_denda_telat dan txt_denda_kondisi
        int denda_telat = Integer.parseInt(txt_denda_telat.getText());
        int denda_kondisi = Integer.parseInt(txt_denda_kondisi.getText());

        // Menjumlahkan nilai dari txt_denda_telat dengan txt_denda_kondisi
        int total_denda = denda_telat + denda_kondisi;
        System.out.println(total_denda);

        // Menetapkan nilai total denda ke txt_denda_total
        txt_denda_total.setText(String.valueOf(total_denda));

    }//GEN-LAST:event_kondisi_bukuItemStateChanged

    private void txt_denda_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_denda_totalActionPerformed

    }//GEN-LAST:event_txt_denda_totalActionPerformed

    private void txt_denda_kondisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_denda_kondisiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_denda_kondisiActionPerformed

    private void txt_denda_totalAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_txt_denda_totalAncestorAdded

    }//GEN-LAST:event_txt_denda_totalAncestorAdded

    private void spinner_pinjamStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinner_pinjamStateChanged

    }//GEN-LAST:event_spinner_pinjamStateChanged

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
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JComboBox<String> kondisi_buku;
    private javax.swing.JPanel pengembalian;
    private javax.swing.JSpinner spinner_kembali;
    public javax.swing.JSpinner spinner_pinjam;
    private javax.swing.JTable tabel_pending;
    private javax.swing.JTable tabel_return;
    private com.toedter.calendar.JDateChooser tanggal_kembali_akhir;
    public com.toedter.calendar.JDateChooser tanggal_kembali_awal;
    public com.toedter.calendar.JDateChooser tanggal_pinjam;
    private javax.swing.JTextField txt_denda_kondisi;
    private javax.swing.JTextField txt_denda_telat;
    public javax.swing.JTextField txt_denda_total;
    public javax.swing.JTextField txt_judul_buku;
    public javax.swing.JTextField txt_kode_buku;
    public javax.swing.JTextField txt_kode_peminjaman;
    public javax.swing.JTextField txt_kode_pengembalian;
    public javax.swing.JTextField txt_nama;
    public javax.swing.JTextField txt_petugas;
    private javax.swing.JTextField txt_search;
    private javax.swing.JTextField txt_search1;
    // End of variables declaration//GEN-END:variables
}
