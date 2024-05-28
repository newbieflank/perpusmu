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
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.smartcardio.*;
import javax.swing.SwingUtilities;

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
    private int nilaiSpinnerBaikSebelumnya = 0;
    private int nilaiSpinnerRusakSebelumnya = 0;
    private int nilaiSpinnerHilangSebelumnya = 0;

    public Pengembalian() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        load_table();
        load_table1();
        jDialog1();
        id_autoincrement();
        loadtable2();
        txt_denda_total.setText("0");
        baik.setText("0");
        rusak.setText("0");
        hilang.setText("0");
        baik.setVisible(false);
        rusak.setVisible(false);
        hilang.setVisible(false);
        kondisi_rusak.setVisible(false);
        kondisi_hilang.setVisible(false);
        telat_rusak.setVisible(false);
        telat_hilang.setVisible(false);

        tabel_return.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel_return.getTableHeader().setForeground(Color.white);

        tabel_pending.getTableHeader().setBackground(new Color(63, 148, 105));
        tabel_pending.getTableHeader().setForeground(Color.white);

        tabel_pending.getColumnModel().getColumn(0).setPreferredWidth(20);

        jDialog1.setSize(1000, 700);
        setVisible(true);
        
        // Menambahkan key listener untuk mendeteksi perubahan teks
        txt_search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Memastikan kursor tetap terlihat di akhir teks
                SwingUtilities.invokeLater(() -> {
                    txt_search.setCaretPosition(txt_search.getText().length());
                    txt_search.requestFocusInWindow();
                });
            }
        });

    }

    // Method untuk membaca ID dari tag RFID
    private void readRFID() {
        // Inisialisasi context untuk Smart Card IO API
        CardTerminals terminals = TerminalFactory.getDefault().terminals();

        try {
            // Cari pembaca RFID yang tersedia
            for (CardTerminal terminal : terminals.list()) {
                // Lakukan koneksi ke pembaca RFID
                terminal.waitForCardPresent(0);
                Card card = terminal.connect("*");
                CardChannel channel = card.getBasicChannel();

                // Perintah APDU untuk membaca data dari tag RFID
                byte[] command = {(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                ResponseAPDU response = channel.transmit(new CommandAPDU(command));

                // Mendapatkan data yang dibaca dari tag RFID sebagai string
                String rfidData = new String(response.getData());

                // Menampilkan ID RFID pada JTextField
                txt_search.setText(rfidData);

                // Putuskan koneksi dari pembaca RFID
                card.disconnect(false);
            }
        } catch (CardException ex) {
            ex.printStackTrace();
        }
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

        try {
            loadtable2();
            pst = con.prepareStatement("delete from detail_peminjaman where jumlah_peminjaman = 0");
            pst.execute();
            String sql = "SELECT peminjaman.kode_peminjaman, anggota.nama, users.username, detail_peminjaman.tanggal_peminjaman, detail_peminjaman.tanggal_kembali, detail_peminjaman.jumlah_peminjaman, buku.kode_buku, buku.judul_buku FROM peminjaman JOIN users ON peminjaman.id_users = users.id_users JOIN detail_peminjaman ON peminjaman.kode_peminjaman = detail_peminjaman.kode_peminjaman JOIN buku ON detail_peminjaman.No_buku = buku.No_buku JOIN anggota ON peminjaman.NISN = anggota.NISN;";

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
                    res.getString("judul_buku"),});
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
            String sql = "SELECT pengembalian.kode_pengembalian, anggota.nama, users.username, detail_pengembalian.jumlah_pengembalian, detail_pengembalian.status_pengembalian, detail_pengembalian.waktu_pengembalian, buku.kode_buku, buku.judul_buku, detail_pengembalian.kondisi_buku, detail_pengembalian.denda FROM pengembalian JOIN detail_pengembalian ON pengembalian.kode_pengembalian = detail_pengembalian.kode_pengembalian JOIN buku ON detail_pengembalian.No_buku = buku.No_buku JOIN users ON pengembalian.id_users = users.id_users JOIN anggota ON detail_pengembalian.NISN = anggota.NISN;";

            Statement stm = con.createStatement();
            ResultSet res = stm.executeQuery(sql);

            while (res.next()) {
                String statusPengembalian;
                int jumlahPengembalian = res.getInt("jumlah_pengembalian");

                if (jumlahPengembalian != 0) {
                    statusPengembalian = "Kembali";
                } else {
                    statusPengembalian = "Belum Kembali";
                }
                model.addRow(new Object[]{
                    res.getString("kode_pengembalian"),
                    res.getString("nama"),
                    res.getString("username"),
                    jumlahPengembalian, // Menggunakan getInt karena jumlah pengembalian merupakan nilai numerik
                    statusPengembalian, // Menggunakan statusPengembalian yang telah disesuaikan
                    res.getString("waktu_pengembalian"),
                    res.getString("kode_buku"),
                    res.getString("judul_buku"),
                    res.getString("kondisi_buku"),
                    res.getString("denda"),});
            }

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

    private void updateTotalDenda() {
        // Mendapatkan nilai dari JTextField baik, rusak, dan hilang
        int dendaBaik = Integer.parseInt(baik.getText());
        int dendaRusak = Integer.parseInt(rusak.getText());
        int dendaHilang = Integer.parseInt(hilang.getText());

        // Menjumlahkan total denda dari JTextField baik, rusak, dan hilang
        int totalDendaTextField = dendaBaik + dendaRusak + dendaHilang;

        // Mendapatkan nilai dari JTextField kondisi_rusak dan telat_rusak
        int dendaKondisi = 0;
        int dendaTelat = 0;
        try {
            dendaKondisi = kondisi_rusak.getText().isEmpty() ? 0 : Integer.parseInt(kondisi_rusak.getText());
            dendaTelat = telat_rusak.getText().isEmpty() ? 0 : Integer.parseInt(telat_rusak.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Menjumlahkan total denda dari JTextField kondisi_rusak dan telat_rusak
        int totalDendaKondisiTelat = dendaKondisi + dendaTelat;

        // Menentukan total denda akhir dengan mengambil nilai maksimum dari total denda dari kedua sumber
        int totalDenda = Math.max(totalDendaTextField, totalDendaKondisiTelat);

        // Mengatur nilai JTextField txt_denda_total dengan total denda
        txt_denda_total.setText(Integer.toString(totalDenda));
    }

    private void clear() {
        txt_search.setText("");
        txt_search1.setText("");
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        // Memindahkan fokus kursor ke txt_search saat frame terlihat
        if (visible) {
            txt_search.requestFocusInWindow();
        }
    }

    private void insertDetailPengembalian(PreparedStatement pst, String kodepengembalian, String status, String waktu, String kondisi, java.sql.Date tanggalPinjam, int jumlah, String kodebuku, String denda, String nama) throws SQLException {
        if (jumlah > 0) {
            pst.setString(1, kodepengembalian);
            pst.setString(2, status);
            pst.setString(3, waktu);
            pst.setString(4, kondisi);
            pst.setDate(5, tanggalPinjam);
            pst.setInt(6, jumlah);
            pst.setString(7, kodebuku);
            pst.setString(8, denda);
            pst.setString(9, nama);
            pst.executeUpdate();
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
        spinner_pinjam = new javax.swing.JSpinner();
        tanggal_pinjam = new com.toedter.calendar.JDateChooser();
        tanggal_kembali_awal = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        tanggal_kembali_akhir = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        spinner_baik = new javax.swing.JSpinner();
        spinner_rusak = new javax.swing.JSpinner();
        spinner_hilang = new javax.swing.JSpinner();
        baik = new javax.swing.JTextField();
        rusak = new javax.swing.JTextField();
        hilang = new javax.swing.JTextField();
        kondisi_rusak = new javax.swing.JTextField();
        kondisi_hilang = new javax.swing.JTextField();
        telat_rusak = new javax.swing.JTextField();
        telat_hilang = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_pending = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txt_search = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btn_clear = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_return = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        txt_search1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btn_clear2 = new javax.swing.JButton();

        jDialog1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jDialog1.setUndecorated(true);

        pengembalian.setBackground(new java.awt.Color(255, 255, 255));
        pengembalian.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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

        btn_tambah.setBackground(new java.awt.Color(10, 169, 50));
        btn_tambah.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        btn_tambah.setForeground(new java.awt.Color(255, 255, 255));
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
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
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
        jLabel12.setText("Kondisi Baik");

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

        spinner_pinjam.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_pinjam.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_pinjam.setEnabled(false);
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

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel16.setText("Kondisi Rusak");

        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel18.setText("Kondisi Hilang");

        spinner_baik.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_baik.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_baik.setPreferredSize(new java.awt.Dimension(37, 25));
        spinner_baik.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                spinner_baikAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        spinner_baik.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_baikStateChanged(evt);
            }
        });

        spinner_rusak.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_rusak.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_rusak.setPreferredSize(new java.awt.Dimension(37, 25));
        spinner_rusak.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                spinner_rusakAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        spinner_rusak.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_rusakStateChanged(evt);
            }
        });

        spinner_hilang.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        spinner_hilang.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spinner_hilang.setPreferredSize(new java.awt.Dimension(37, 25));
        spinner_hilang.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                spinner_hilangAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        spinner_hilang.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinner_hilangStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pengembalianLayout = new javax.swing.GroupLayout(pengembalian);
        pengembalian.setLayout(pengembalianLayout);
        pengembalianLayout.setHorizontalGroup(
            pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pengembalianLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pengembalianLayout.createSequentialGroup()
                                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(spinner_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel18))
                                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pengembalianLayout.createSequentialGroup()
                                        .addGap(63, 63, 63)
                                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(spinner_baik, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(tanggal_kembali_akhir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(tanggal_kembali_awal, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                                                .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING))
                                            .addComponent(spinner_rusak, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(spinner_hilang, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(pengembalianLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(hilang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pengembalianLayout.createSequentialGroup()
                                .addComponent(baik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pengembalianLayout.createSequentialGroup()
                                        .addComponent(telat_rusak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(telat_hilang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pengembalianLayout.createSequentialGroup()
                                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(rusak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(pengembalianLayout.createSequentialGroup()
                                                .addComponent(kondisi_rusak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(kondisi_hilang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addGap(158, 158, 158)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(txt_denda_total, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(txt_kode_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_kode_buku, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txt_judul_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_kode_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(169, 169, 169)
                                .addComponent(jLabel6))
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(tanggal_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(376, 376, 376)
                                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(txt_petugas, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(295, 295, 295))
        );
        pengembalianLayout.setVerticalGroup(
            pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pengembalianLayout.createSequentialGroup()
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pengembalianLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_kode_peminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nama, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tanggal_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tanggal_kembali_awal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel5)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pengembalianLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_petugas, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spinner_pinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_kode_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tanggal_kembali_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_judul_buku, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spinner_baik, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))))
                .addGap(39, 39, 39)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(spinner_rusak, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_denda_total, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinner_hilang, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(8, 8, 8)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_kode_pengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(baik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rusak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hilang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kondisi_rusak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kondisi_hilang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(73, 73, 73))
                    .addGroup(pengembalianLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(pengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(telat_rusak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(telat_hilang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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

        txt_search.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                txt_searchAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        txt_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_searchActionPerformed(evt);
            }
        });
        txt_search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_searchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_searchKeyReleased(evt);
            }
        });
        jPanel2.add(txt_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 10, 100, 20));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-search-18.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 20, 20));

        btn_clear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_clear.setText("Clear (F9)");
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1375, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_clear)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btn_clear))
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_search1KeyReleased(evt);
            }
        });
        jPanel3.add(txt_search1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, 10, 100, 20));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8-search-18.png"))); // NOI18N
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 20, 20));

        btn_clear2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_clear2.setText("Clear (F10)");
        btn_clear2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clear2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1375, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_clear2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btn_clear2))
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
        spinner_baik.setValue(0);
        spinner_rusak.setValue(0);
        spinner_hilang.setValue(0);

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
         if (jumlahpeminjaman < 0) {
        // Klik tombol batal dan keluar dari metode ini
        jButton1ActionPerformed(evt);
        JOptionPane.showMessageDialog(null, "Transaksi Gagal Karena Nilai Pengembalian Anda Tidak Sesuai Jumlah Pinjam");
        return;
    }
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

        String kondisikembali;

        // Mengganti kondisikembali sesuai dengan nilai spinner_baik, spinner_rusak, atau spinner_hilang
        if (spinner_baik.isEnabled()) {
            kondisikembali = "Baik";
        } else if (spinner_rusak.isEnabled()) {
            kondisikembali = "Rusak";
        } else {
            kondisikembali = "Hilang";
        }
        // Mendapatkan nilai dari semua spinners
        int nilaiSpinnerBaik = (int) spinner_baik.getValue();
        int nilaiSpinnerRusak = (int) spinner_rusak.getValue();
        int nilaiSpinnerHilang = (int) spinner_hilang.getValue();
        // Periksa apakah salah satu dari spinner_baik, spinner_rusak, atau spinner_hilang kosong
        if (nilaiSpinnerBaik == 0 && nilaiSpinnerRusak == 0 && nilaiSpinnerHilang == 0) {
            JOptionPane.showMessageDialog(null, "Harap isi jumlah pengembalian terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Menghentikan eksekusi metode jika salah satu spinner kosong
        }

        // Menghitung total pinjaman
        int totalPeminjaman = nilaiSpinnerBaik + nilaiSpinnerRusak;
        String kodebuku1 = txt_judul_buku.getText();
        String denda;
        String dendatotal = txt_denda_total.getText();

// Mendapatkan nilai dari semua JTextField
        String nilaibaik = baik.getText();
        String nilairusak = rusak.getText();
        String nilaihilang = hilang.getText();

// Mengganti denda sesuai dengan nilai dari JTextField
        if (!nilaibaik.isEmpty()) {
            denda = "0";  // Jika baik ada isinya, dendanya 0
        } else if (!nilairusak.isEmpty()) {
            denda = rusak.getText();  // Jika rusak ada isinya, denda sesuai dengan txt_rusak
        } else if (!nilaihilang.isEmpty()) {
            denda = hilang.getText();  // Jika hilang ada isinya, denda sesuai dengan txt_hilang
        } else {
            denda = "Tidak Diketahui";  // Default case if all fields are empty
        }
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
            String sqlInsertPengembalian = "INSERT INTO pengembalian (kode_pengembalian, kode_peminjaman, ID_users) VALUES (?, ?, (SELECT ID_users FROM users WHERE username = ? LIMIT 1))";
            try (PreparedStatement pstInsertPengembalian = con.prepareStatement(sqlInsertPengembalian)) {
                pstInsertPengembalian.setString(1, kodepengembalian);
                pstInsertPengembalian.setString(2, kodepeminjaman);
                pstInsertPengembalian.setString(3, petugas);
                pstInsertPengembalian.executeUpdate();
            }

            // Insert data ke tabel 'detail_pengembalian'
            String sqlInsertDetailPengembalian = "INSERT INTO detail_pengembalian (kode_pengembalian, status_pengembalian, waktu_pengembalian, kondisi_buku, tanggal, jumlah_pengembalian, No_buku, denda, NISN) VALUES (?, ?, ?, ?, ?, ?, (SELECT No_buku FROM buku WHERE judul_buku = ? LIMIT 1), ?, (SELECT NISN FROM anggota WHERE nama = ? LIMIT 1))";
            try (PreparedStatement pstInsertDetailPengembalian = con.prepareStatement(sqlInsertDetailPengembalian)) {
                // Set status menjadi "Kembali" untuk semua data yang diinsert ke detail_pengembalian
                String statusDetailPengembalian = "Kembali";

                // Mendapatkan tanggal kembali awal dan tanggal kembali akhir
                Date tanggalKembaliAwal = tanggal_kembali_awal.getDate();
                Date tanggalKembaliAkhir = tanggal_kembali_akhir.getDate();

                // Mendapatkan status waktu kembali berdasarkan perbandingan tanggal
                String waktupengembalian;
                if (tanggalKembaliAwal != null && tanggalKembaliAkhir != null) {
                    // Mengubah tanggal menjadi string hanya dengan format tanggal (tanpa jam)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String tanggalAwal = sdf.format(tanggalKembaliAwal);
                    String tanggalAkhir = sdf.format(tanggalKembaliAkhir);

                    if (tanggalAwal.equals(tanggalAkhir)) {
                        waktupengembalian = "Tepat Waktu";
                    } else if (tanggalKembaliAkhir.after(tanggalKembaliAwal)) {
                        waktupengembalian = "Telat";
                    } else {
                        waktupengembalian = "Tepat Waktu";
                    }
                } else {
                    waktupengembalian = "Tidak Diketahui";
                }

                java.sql.Date tanggalPinjamSQL = new java.sql.Date(tanggal_kembali_akhir.getDate().getTime());

                // Menggunakan metode insertDetailPengembalian untuk setiap kondisi buku
                insertDetailPengembalian(pstInsertDetailPengembalian, kodepengembalian, statusDetailPengembalian, waktupengembalian, "Baik", tanggalPinjamSQL, nilaiSpinnerBaik, kodebuku, "0", nama);
                insertDetailPengembalian(pstInsertDetailPengembalian, kodepengembalian, statusDetailPengembalian, waktupengembalian, "Rusak", tanggalPinjamSQL, nilaiSpinnerRusak, kodebuku, rusak.getText(), nama);
                insertDetailPengembalian(pstInsertDetailPengembalian, kodepengembalian, statusDetailPengembalian, waktupengembalian, "Hilang", tanggalPinjamSQL, nilaiSpinnerHilang, kodebuku, hilang.getText(), nama);
            }

            if (!dendatotal.equals("0")) {
    // Cek apakah ada entri denda dengan NISN yang sama
    String sqlCheckExistingDenda = "SELECT COUNT(*) FROM denda WHERE NISN = (SELECT NISN FROM anggota WHERE nama = ? LIMIT 1)";
    try (PreparedStatement pstCheckExistingDenda = con.prepareStatement(sqlCheckExistingDenda)) {
        pstCheckExistingDenda.setString(1, nama);
        ResultSet rs = pstCheckExistingDenda.executeQuery();
        rs.next();
        int rowCount = rs.getInt(1);
        if (rowCount > 0) {
            // Jika ada, update jumlah denda yang ada dengan jumlah denda yang baru ditambahkan
            String sqlUpdateDenda = "UPDATE denda SET jumlah_denda = jumlah_denda + ? WHERE NISN = (SELECT NISN FROM anggota WHERE nama = ? LIMIT 1)";
            try (PreparedStatement pstUpdateDenda = con.prepareStatement(sqlUpdateDenda)) {
                pstUpdateDenda.setInt(1, Integer.parseInt(dendatotal));
                pstUpdateDenda.setString(2, nama);
                pstUpdateDenda.executeUpdate();
            }
        } else {
            // Jika tidak ada, masukkan data baru ke tabel 'denda'
            String sqlInsertDenda = "INSERT INTO denda (jumlah_denda, status_denda, total_pembayaran, NISN, No_buku, kode_pengembalian) VALUES (?, ?, ?, (SELECT NISN FROM anggota WHERE nama = ? LIMIT 1), (SELECT No_buku FROM buku WHERE judul_buku = ? LIMIT 1), ?)";
            try (PreparedStatement pstInsertDenda = con.prepareStatement(sqlInsertDenda)) {
                pstInsertDenda.setInt(1, Integer.parseInt(dendatotal));
                pstInsertDenda.setString(2, "Belum Lunas"); // Mengasumsikan status awal adalah 'Belum Lunas'
                pstInsertDenda.setInt(3, 0); // Mengasumsikan total_pembayaran awal sama dengan jumlah_denda
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

            // Insert data ke tabel 'history' jika ada buku yang hilang
            if (nilaiSpinnerHilang > 0) {
                String sqlInsertHistory = "INSERT INTO history (id_history, No_buku, peristiwa, tanggal, harga_buku, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstInsertHistory = con.prepareStatement(sqlInsertHistory)) {
                    // Generate a random 10-digit number for id_history
                    long idHistory = (long) (Math.random() * 100000L);
                    // Get the current date
                    java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

                    // Retrieve the id_buku (No_buku) and harga from the 'buku' table based on judul_buku
                    String sqlSelectBuku = "SELECT No_buku, harga FROM buku WHERE judul_buku = ? LIMIT 1";
                    try (PreparedStatement pstSelectBuku = con.prepareStatement(sqlSelectBuku)) {
                        pstSelectBuku.setString(1, kodebuku1);
                        ResultSet rsBuku = pstSelectBuku.executeQuery();
                        if (rsBuku.next()) {
                            int noBuku = rsBuku.getInt("No_buku");
                            int hargaBuku = rsBuku.getInt("harga");

                            pstInsertHistory.setLong(1, idHistory);
                            pstInsertHistory.setInt(2, noBuku);
                            pstInsertHistory.setString(3, "Hilang");
                            pstInsertHistory.setDate(4, currentDate);
                            pstInsertHistory.setInt(5, hargaBuku);
                            pstInsertHistory.setString(6, "Buku hilang");
                            pstInsertHistory.executeUpdate();
                        }
                    }
                }
            }

            // Menghitung total buku yang dikembalikan (baik dan rusak)
            int totalBukuKembali = nilaiSpinnerBaik + nilaiSpinnerRusak;

            // Menambahkan stok buku dengan jumlah buku yang dikembalikan tanpa memperhatikan buku yang hilang
            tambahStokBuku(con, totalBukuKembali, kodebuku);

            // Selesai transaksi
            con.commit();
            JOptionPane.showMessageDialog(null, "Update berhasil");
        } catch (SQLException e) {
            // Tangani kesalahan dengan membatalkan transaksi jika terjadi kesalahan
            try {
                con.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal: " + e.getMessage());
        } finally {
            // Pastikan untuk mengatur ulang otomatis commit ke true setelah transaksi selesai atau gagal
            try {
                con.setAutoCommit(true);
            } catch (SQLException autoCommitException) {
                autoCommitException.printStackTrace();
            }
            load_table1();
            id_autoincrement();
            jDialog1.dispose();
            load_table();
            txt_denda_total.setText("0");
            txt_search.requestFocusInWindow();
        }
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        spinner_baik.setValue(0);
        spinner_rusak.setValue(0);
        spinner_hilang.setValue(0);
        txt_denda_total.setText("0");

        jDialog1.dispose();
        load_table();
        txt_search.requestFocusInWindow();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txt_judul_bukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_judul_bukuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_judul_bukuActionPerformed

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
        String sql = "SELECT peminjaman.kode_peminjaman, anggota.nama, users.username, detail_peminjaman.tanggal_peminjaman, detail_peminjaman.tanggal_kembali, detail_peminjaman.jumlah_peminjaman, buku.kode_buku, buku.judul_buku FROM peminjaman INNER JOIN detail_peminjaman ON peminjaman.kode_peminjaman = detail_peminjaman.kode_peminjaman LEFT JOIN anggota ON peminjaman.NISN = anggota.NISN LEFT JOIN buku ON detail_peminjaman.No_buku = buku.No_buku LEFT JOIN users ON peminjaman.ID_users = users.ID_users WHERE peminjaman.kode_peminjaman LIKE '%" + keyword + "%' OR anggota.nama LIKE '%" + keyword + "%'";

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

    private void txt_denda_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_denda_totalActionPerformed

    }//GEN-LAST:event_txt_denda_totalActionPerformed

    private void txt_denda_totalAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_txt_denda_totalAncestorAdded

    }//GEN-LAST:event_txt_denda_totalAncestorAdded

    private void spinner_pinjamStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinner_pinjamStateChanged
   int jumlahpeminjaman = (int) spinner_pinjam.getValue();
    if (jumlahpeminjaman < 0) {
        // Klik tombol batal dan keluar dari metode ini
        //jButton1ActionPerformed(new java.awt.event.ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        JOptionPane.showMessageDialog(null, "Kembalikan Buku Sesuai Jumlah Peminjaman");
        return;
    }
    }//GEN-LAST:event_spinner_pinjamStateChanged

    private void spinner_baikStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinner_baikStateChanged
        // TODO add your handling code here: 
        // Mendapatkan nilai spinner_baik yang baru
        int nilaiSpinnerBaikBaru = (int) spinner_baik.getValue();

        // Menghitung perubahan nilai spinner_baik
        int perubahanNilai = nilaiSpinnerBaikBaru - nilaiSpinnerBaikSebelumnya;

        // Mendapatkan nilai spinner_pinjam
        int nilaiSpinnerPinjam = (int) spinner_pinjam.getValue();

        // Jika nilai spinner_pinjam adalah 0, maka spinner_baik tidak bisa naik lagi
        if (nilaiSpinnerPinjam == 0 && perubahanNilai > 0) {
            // Mengembalikan nilai spinner_baik ke nilai sebelumnya
            spinner_baik.setValue(nilaiSpinnerBaikSebelumnya);
        } else {
            // Menyesuaikan nilai spinner_pinjam berdasarkan perubahan nilai spinner_baik
            spinner_pinjam.setValue(nilaiSpinnerPinjam - perubahanNilai);

            // Memperbarui nilai spinner_baik yang lama
            nilaiSpinnerBaikSebelumnya = nilaiSpinnerBaikBaru;

            // Mendapatkan tanggal kembali awal dan tanggal kembali akhir
            Date tanggalKembaliAwal = tanggal_kembali_awal.getDate();
            Date tanggalKembaliAkhir = tanggal_kembali_akhir.getDate();

            // Menghitung status waktu pengembalian
            String waktupengembalian = "Tepat Waktu"; // Default: tepat waktu

            // Jika tanggal_kembali_akhir sebelum atau sama dengan tanggal_kembali_awal
            if (tanggalKembaliAwal != null && tanggalKembaliAkhir != null) {
                // Mengubah tanggal menjadi string hanya dengan format tanggal (tanpa jam)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String tanggalAwal = sdf.format(tanggalKembaliAwal);
                String tanggalAkhir = sdf.format(tanggalKembaliAkhir);
                // Tidak terlambat, nilai txt_baik diisi dengan 0
                baik.setText("0");
            } else {
                // Jika terlambat, nilai txt_baik diisi dengan nilai spinner_baik dikali 1000
                baik.setText(String.valueOf(nilaiSpinnerBaikBaru * 1000));
            }
            updateTotalDenda();
        }
    }//GEN-LAST:event_spinner_baikStateChanged

    private void spinner_rusakStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinner_rusakStateChanged
        // TODO add your handling code here:
        // Mendapatkan nilai spinner_rusak yang baru
        int nilaiSpinnerRusakBaru = (int) spinner_rusak.getValue();

        // Menghitung perubahan nilai spinner_rusak
        int perubahanNilai = nilaiSpinnerRusakBaru - nilaiSpinnerRusakSebelumnya;

        // Mendapatkan nilai spinner_pinjam
        int nilaiSpinnerPinjam = (int) spinner_pinjam.getValue();

        // Jika nilai spinner_pinjam adalah 0, maka spinner_rusak tidak bisa naik lagi
        if (nilaiSpinnerPinjam == 0 && perubahanNilai > 0) {
            // Mengembalikan nilai spinner_rusak ke nilai sebelumnya
            spinner_rusak.setValue(nilaiSpinnerRusakSebelumnya);
        } else {
            // Menyesuaikan nilai spinner_pinjam berdasarkan perubahan nilai spinner_rusak
            spinner_pinjam.setValue(nilaiSpinnerPinjam - perubahanNilai);

            // Memperbarui nilai spinner_rusak yang lama
            nilaiSpinnerRusakSebelumnya = nilaiSpinnerRusakBaru;

            // Mendapatkan judul buku
            String judulBuku = txt_judul_buku.getText();

            // Mendapatkan harga buku dari metode getHargaBuku
            int hargaBuku = getHargaBuku(judulBuku);

            // Menghitung total denda
            int totalDenda = (int) (0.5 * nilaiSpinnerRusakBaru * hargaBuku);

            // Mengatur nilai txt_denda dengan total denda
            kondisi_rusak.setText(Integer.toString(totalDenda));

            // Mendapatkan tanggal kembali awal dan tanggal kembali akhir
            Date tanggalKembaliAwal = tanggal_kembali_awal.getDate();
            Date tanggalKembaliAkhir = tanggal_kembali_akhir.getDate();

            // Menghitung status waktu pengembalian
            String waktupengembalian = "Tepat Waktu"; // Default: tepat waktu

            // Jika tanggal_kembali_akhir sebelum atau sama dengan tanggal_kembali_awal
            if (tanggalKembaliAwal != null && tanggalKembaliAkhir != null) {
                // Mengubah tanggal menjadi string hanya dengan format tanggal (tanpa jam)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String tanggalAwal = sdf.format(tanggalKembaliAwal);
                String tanggalAkhir = sdf.format(tanggalKembaliAkhir);
                // Tidak terlambat, nilai txt_rusak diisi dengan kondisi_rusak
                rusak.setText(kondisi_rusak.getText());
            } else {
                // Jika terlambat, nilai txt_rusak diisi dengan jumlah kondisi_rusak dan telat_rusak
                int dendaTelat = Integer.parseInt(telat_rusak.getText());
                int dendaKondisi = Integer.parseInt(kondisi_rusak.getText());
                int totalDendaRusak = dendaTelat + dendaKondisi;
                rusak.setText(String.valueOf(totalDendaRusak));
            }
            updateTotalDenda();
        }
    }//GEN-LAST:event_spinner_rusakStateChanged

    private void spinner_hilangStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinner_hilangStateChanged
        // TODO add your handling code here:
        // Mendapatkan nilai spinner_hilang yang baru
        int nilaiSpinnerHilangBaru = (int) spinner_hilang.getValue();

        // Menghitung perubahan nilai spinner_hilang
        int perubahanNilai = nilaiSpinnerHilangBaru - nilaiSpinnerHilangSebelumnya;

        // Mendapatkan nilai spinner_pinjam
        int nilaiSpinnerPinjam = (int) spinner_pinjam.getValue();

        // Jika nilai spinner_pinjam adalah 0, maka spinner_hilang tidak bisa naik lagi
        if (nilaiSpinnerPinjam == 0 && perubahanNilai > 0) {
            // Mengembalikan nilai spinner_hilang ke nilai sebelumnya
            spinner_hilang.setValue(nilaiSpinnerHilangSebelumnya);
        } else {
            // Menyesuaikan nilai spinner_pinjam berdasarkan perubahan nilai spinner_hilang
            spinner_pinjam.setValue(nilaiSpinnerPinjam - perubahanNilai);

            // Memperbarui nilai spinner_hilang yang lama
            nilaiSpinnerHilangSebelumnya = nilaiSpinnerHilangBaru;

            // Mendapatkan judul buku
            String judulBuku = txt_judul_buku.getText();

            // Mendapatkan harga buku dari metode getHargaBuku
            int hargaBuku = getHargaBuku(judulBuku);

            // Menghitung total denda (harga buku dikali jumlah buku yang hilang)
            int totalDenda = nilaiSpinnerHilangBaru * hargaBuku;

            // Mengatur nilai txt_denda dengan total denda
            kondisi_hilang.setText(Integer.toString(totalDenda));

            // Mendapatkan tanggal kembali awal dan tanggal kembali akhir
            Date tanggalKembaliAwal = tanggal_kembali_awal.getDate();
            Date tanggalKembaliAkhir = tanggal_kembali_akhir.getDate();

            // Menghitung status waktu pengembalian
            String waktupengembalian = "Tepat Waktu"; // Default: tepat waktu

            // Jika tanggal_kembali_akhir sebelum atau sama dengan tanggal_kembali_awal
            if (tanggalKembaliAwal != null && tanggalKembaliAkhir != null) {
                // Mengubah tanggal menjadi string hanya dengan format tanggal (tanpa jam)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String tanggalAwal = sdf.format(tanggalKembaliAwal);
                String tanggalAkhir = sdf.format(tanggalKembaliAkhir);
                // Tidak terlambat, nilai txt_hilang diisi dengan kondisi_hilang
                hilang.setText(kondisi_hilang.getText());
            } else {
                // Jika terlambat, nilai txt_hilang diisi dengan jumlah kondisi_hilang dan telat_hilang
                int dendaTelat = Integer.parseInt(telat_hilang.getText());
                int dendaKondisi = Integer.parseInt(kondisi_hilang.getText());
                int totalDendaHilang = dendaTelat + dendaKondisi;
                hilang.setText(String.valueOf(totalDendaHilang));
            }
            updateTotalDenda();
        }
    }//GEN-LAST:event_spinner_hilangStateChanged

    private void spinner_baikAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_spinner_baikAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_spinner_baikAncestorAdded

    private void spinner_rusakAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_spinner_rusakAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_spinner_rusakAncestorAdded

    private void spinner_hilangAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_spinner_hilangAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_spinner_hilangAncestorAdded

    private void txt_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_searchActionPerformed
        txt_search.requestFocusInWindow();
    }//GEN-LAST:event_txt_searchActionPerformed

    private void txt_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_F9) {
            // Jika tombol Enter ditekan, tekan tombol jButton2
            btn_clear.doClick();
        } else if (evt.getKeyCode() == KeyEvent.VK_F10) {
            // Jika tombol F5 ditekan, pindah fokus ke txt_jumlah
            btn_clear2.doClick();}
    }//GEN-LAST:event_txt_searchKeyPressed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btn_clearActionPerformed

    private void btn_clear2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clear2ActionPerformed
        // TODO add your handling code here:
        clear();
    }//GEN-LAST:event_btn_clear2ActionPerformed

    private void txt_search1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_F9) {
            // Jika tombol Enter ditekan, tekan tombol jButton2
            btn_clear.doClick();
        } else if (evt.getKeyCode() == KeyEvent.VK_F10) {
            // Jika tombol F5 ditekan, pindah fokus ke txt_jumlah
            btn_clear2.doClick();}
    }//GEN-LAST:event_txt_search1KeyPressed

    private void txt_searchAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_txt_searchAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_searchAncestorAdded

    DefaultTableModel model = new DefaultTableModel() {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    // Main method
    public static void main(String args[]) {
        // Set look and feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        // Membuat dan menampilkan frame
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Pengembalian().setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(Pengembalian.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField baik;
    private javax.swing.JButton btn_clear;
    private javax.swing.JButton btn_clear2;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JTextField hilang;
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JTextField kondisi_hilang;
    private javax.swing.JTextField kondisi_rusak;
    private javax.swing.JPanel pengembalian;
    private javax.swing.JTextField rusak;
    public javax.swing.JSpinner spinner_baik;
    public javax.swing.JSpinner spinner_hilang;
    public javax.swing.JSpinner spinner_pinjam;
    public javax.swing.JSpinner spinner_rusak;
    private javax.swing.JTable tabel_pending;
    private javax.swing.JTable tabel_return;
    private com.toedter.calendar.JDateChooser tanggal_kembali_akhir;
    public com.toedter.calendar.JDateChooser tanggal_kembali_awal;
    public com.toedter.calendar.JDateChooser tanggal_pinjam;
    private javax.swing.JTextField telat_hilang;
    private javax.swing.JTextField telat_rusak;
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
