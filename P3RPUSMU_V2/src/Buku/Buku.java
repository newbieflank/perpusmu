package Buku;

import com.formdev.flatlaf.FlatClientProperties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import Navbar.koneksi;
import java.awt.Color;
import java.awt.Font;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class Buku extends javax.swing.JPanel {

    private Connection con;
    private PreparedStatement pst, pst1;
    private ResultSet rs;
    private String kode_buku;
    private String No_buku;
    private int noBukuEdit = -1;

    public Buku() throws SQLException {
        con = koneksi.Koneksi();
        initComponents();
        loadTabel();
        jPanel1.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        jPanel2.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        jPanel3.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        UIManager.put("Button.arc", 15);
        txt_search.putClientProperty("JComponent.roundRect", true);
        JTabel1.getTableHeader().setBackground(new Color(63, 148, 105));
        JTabel1.getTableHeader().setForeground(Color.white);
        JTabel1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        Tambah.setSize(570, 514);
        Edit.setSize(570, 514);
        txt_search.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cari");

    }

    private void addHistory(int harga, String kondisi) {
        String kode_buku = dial_kode.getText();
        String judul_buku = dial_judul.getText();
        String referensi = dial_referensi.getText();
        String jilid = dial_jilid.getText();
        Object kategori = dial_kategori.getSelectedItem();
        String pengarang = dial_pengarang.getText();
        String lokasi = dial_lokasi.getText();
        Object kondisi_buku = dial_kondisi.getSelectedItem();
        int tahun_terbit = Integer.parseInt(dial_tahun.getText());
        String asal_buku = dial_asal.getText();
        int harga_buku = Integer.parseInt(dial_harga.getText());
        int jumlah_stock = Integer.parseInt(dial_stock.getText());

        String sql = "insert into history (No_buku, peristiwa, tanggal, harga_buku, keterangan) "
                + "values ((select No_buku from buku where kode_buku = ? and referensi = ? and judul_buku = ? and"
                + " jilid = ? and kategori = ? and"
                + " pengarang = ? and lokasi = ? and kondisi_buku = ? and tahun_terbit = ? and asal_buku = ? and harga =? and"
                + " jumlah_stock = ?), 'masuk', current_date(), ?, ?)";
        String keterangan = "Buku ini masuk dalam kondisi " + kondisi;

        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, kode_buku);
            pst.setString(2, referensi);
            pst.setString(3, judul_buku);
            pst.setString(4, jilid);
            pst.setString(5, (String) kategori);
            pst.setString(6, pengarang);
            pst.setString(7, lokasi);
            pst.setString(8, (String) kondisi_buku);
            pst.setInt(9, tahun_terbit);
            pst.setString(10, asal_buku);
            pst.setInt(11, harga_buku);
            pst.setInt(12, jumlah_stock);
            pst.setInt(13, harga);
            pst.setString(14, keterangan);
            pst.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Jdialog() {
        Tambah.setLocationRelativeTo(null);
        Tambah.setBackground(Color.white);
        Tambah.getRootPane().setOpaque(false);
        Tambah.getContentPane().setBackground(new Color(0, 0, 0, 0));
        Tambah.setBackground(new Color(0, 0, 0, 0));
        Edit.setLocationRelativeTo(null);
        Edit.setBackground(Color.white);
        Edit.getRootPane().setOpaque(false);
        Edit.getContentPane().setBackground(new Color(0, 0, 0, 0));
        Edit.setBackground(new Color(0, 0, 0, 0));
    }

    private void jcombo() {
        if (dial_kategori.getSelectedItem() == null) {
            dial_kategori.setSelectedItem("Kategori Buku");
        }
    }

    private void loadTabel() throws SQLException {
        JTabel1.clearSelection();
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // all cells false
                return false;
            }
        };

        try {
            pst = con.prepareStatement("SELECT * FROM buku_view");
            rs = pst.executeQuery();
            ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(rsmd.getColumnName(i));
            }

            // Add rows to the DefaultTableModel
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    if (rsmd.getColumnName(i).equalsIgnoreCase("tahun_terbit")) {
                        // Assuming 'tahun_terbit' is a Date or Timestamp type
                        java.util.Date date = rs.getDate(i);
                        if (date != null) {
                            rowData[i - 1] = yearFormat.format(date);
                        } else {
                            rowData[i - 1] = null;
                        }
                    } else {
                        rowData[i - 1] = rs.getObject(i);
                    }
                }
                model.addRow(rowData);
            }
            JTabel1.setModel(model);
        } catch (Exception e) {
            System.out.println("loadTable" + e);
        }
    }

    private void editData() {
        String kode_buku = edit_kode.getText();
        String judul_buku = edit_judul.getText();
        String referensi = edit_referensi.getText();
        String jilid = edit_jilid.getText();
        Object kategori = edit_kategori.getSelectedItem();
        String pengarang = edit_pengarang.getText();
        String lokasi = edit_lokasi.getText();
        Object kondisi_buku = edit_kondisi.getSelectedItem();
        int tahun_terbit = Integer.parseInt(edit_tahun.getText());
        String asal_buku = edit_asal.getText();
        int harga = Integer.parseInt(edit_harga.getText());
        int jumlah_stock = Integer.parseInt(edit_stock.getText());
        try {
            pst = con.prepareStatement("UPDATE buku SET kode_buku = ?, referensi = ?, judul_buku = ?, jilid = ?, kategori = ?,"
                    + " pengarang = ?, lokasi = ?, kondisi_buku = ?, tahun_terbit = ?, asal_buku = ?, harga =?,"
                    + " jumlah_stock = ? WHERE No_buku = ?");
            pst.setString(1, kode_buku);
            pst.setString(2, referensi);
            pst.setString(3, judul_buku);
            pst.setString(4, jilid);
            pst.setString(5, (String) kategori);
            pst.setString(6, pengarang);
            pst.setString(7, lokasi);
            pst.setString(8, (String) kondisi_buku);
            if (tahun_terbit != 0) {
                pst.setInt(9, tahun_terbit);
            } else {
                pst.setNull(9, java.sql.Types.INTEGER);
            }
            pst.setString(10, asal_buku);
            if (harga != 0) {
                pst.setInt(11, harga);
            } else {
                pst.setNull(11, java.sql.Types.INTEGER);
            }
            if (jumlah_stock != 0) {
                pst.setInt(12, jumlah_stock);
            } else {
                pst.setNull(12, java.sql.Types.INTEGER);
            }

            pst.setInt(13, noBukuEdit);
            pst.executeUpdate();
            noBukuEdit = -1;

            loadTabel();
            JOptionPane.showMessageDialog(null, "Data berhasil di Update");
            Edit.dispose();
        } catch (Exception e) {
            System.out.println("Edit" + e);
        }
    }

    private void clearEdit() {
        edit_kode.setText("");
        edit_judul.setText("");
        edit_referensi.setText("");
        edit_jilid.setText("");
        edit_kategori.setSelectedItem(null);
        edit_pengarang.setText("");
        edit_lokasi.setText("");
        edit_kondisi.setSelectedItem("");
        edit_tahun.setText("");
        edit_asal.setText("");
        edit_harga.setText("");
        edit_stock.setText("");
    }

    private void GetEdit(int row) {
        if (row < 0) {
            JOptionPane.showMessageDialog(jPanel4, "Pilih Dahulu Data Yang Akan Di Ubah");
        }
        try {
            //get data dari table  berdasarkan row index dan colom keberapa
            //0 index colom no buku
            int noBuku = (int) JTabel1.getValueAt(row, 0);
            pst = con.prepareStatement("SELECT * FROM buku WHERE No_buku = '" + noBuku + "'");
            rs = pst.executeQuery();
            rs.next();
            System.out.println(noBuku);
            String kode_buku = rs.getString("kode_buku");
            String judul_buku = rs.getString("judul_buku");
            String referensi = rs.getString("referensi");
            String jilid = rs.getString("jilid");
            String kategori = rs.getString("kategori");
            String pengarang = rs.getString("pengarang");
            String lokasi = rs.getString("lokasi");
            String kondisi_buku = rs.getString("kondisi_buku");
            Date tahun_terbit = rs.getDate("tahun_terbit");
            Format formater = new SimpleDateFormat("yyyy");
            String tahunterbit = formater.format(tahun_terbit);
            String asal_buku = rs.getString("asal_buku");
            int harga = rs.getInt("harga");
            int jumlah_stock = rs.getInt("jumlah_stock");

            edit_kode.setText(kode_buku);
            edit_judul.setText(judul_buku);
            edit_referensi.setText(referensi);
            edit_jilid.setText(jilid);
            edit_kategori.setSelectedItem(kategori);
            edit_pengarang.setText(pengarang);
            edit_lokasi.setText(lokasi);
            edit_kondisi.setSelectedItem(kondisi_buku);
            edit_tahun.setText(String.valueOf(tahunterbit));
            edit_asal.setText(asal_buku);
            edit_harga.setText(String.valueOf(harga));
            edit_stock.setText(String.valueOf(jumlah_stock));
            edit_kode.enable(false);
            noBukuEdit = noBuku;
            Edit.setVisible(true);
        } catch (Exception e) {
            System.out.println("click" + e.getMessage());
            JOptionPane.showMessageDialog(jPanel4, "Pilih Dahulu Data Yang Akan Di Ubah");
        }
    }

    private void Simpan() {
        String kode_buku = dial_kode.getText();
        String judul_buku = dial_judul.getText();
        String referensi = dial_referensi.getText();
        String jilid = dial_jilid.getText();
        Object kategori = dial_kategori.getSelectedItem();
        String pengarang = dial_pengarang.getText();
        String lokasi = dial_lokasi.getText();
        Object kondisi_buku = dial_kondisi.getSelectedItem();
        int tahun_terbit = Integer.parseInt(dial_tahun.getText());
        String asal_buku = dial_asal.getText();
        int harga = Integer.parseInt(dial_harga.getText());
        int jumlah_stock = Integer.parseInt(dial_stock.getText());

        if (kode_buku.equals("") || referensi.equals("") || judul_buku.equals("") || jilid.equals("") || kategori.equals("") || pengarang.equals("") || lokasi.equals("") || kondisi_buku.equals("") || tahun_terbit == -1 || asal_buku.equals("") || harga == -1 || jumlah_stock == -1) {
            JOptionPane.showMessageDialog(Tambah, "Anda Harus Mengisi Semua Data Terlebih Dahulu");
        } else {
            int result = JOptionPane.showConfirmDialog(Tambah, "Apakah Anda Ingin Menyimpan?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    pst = con.prepareStatement("SELECT jumlah_stock FROM buku WHERE kode_buku = ? and referensi = ?"
                            + " and judul_buku = ? and jilid = ? and kategori = ? and pengarang = ? and lokasi = ? "
                            + "and kondisi_buku = ? and tahun_terbit = ? and asal_buku = ? and harga = ?");
                    pst.setString(1, kode_buku);
                    pst.setString(2, referensi);
                    pst.setString(3, judul_buku);
                    pst.setString(4, jilid);
                    pst.setString(5, (String) kategori);
                    pst.setString(6, pengarang);
                    pst.setString(7, lokasi);
                    pst.setString(8, (String) kondisi_buku);
                    pst.setInt(9, tahun_terbit);
                    pst.setString(10, asal_buku);
                    pst.setInt(11, harga);
                    rs = pst.executeQuery();
                    if (rs.next()) {
                        int stok_awal = rs.getInt((1));
                        int stok = stok_awal + jumlah_stock;
                        pst = con.prepareStatement("UPDATE buku SET jumlah_stock = " + stok);
                        pst.execute();
                    } else {
                        try {
                            pst1 = con.prepareStatement("Insert into buku (kode_buku, referensi, judul_buku, jilid, kategori, pengarang, lokasi, kondisi_buku, tahun_terbit, asal_buku, harga, jumlah_stock) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                            pst1.setString(1, kode_buku);
                            pst1.setString(2, referensi);
                            pst1.setString(3, judul_buku);
                            pst1.setString(4, jilid);
                            pst1.setString(5, (String) kategori);
                            pst1.setString(6, pengarang);
                            pst1.setString(7, lokasi);
                            pst1.setString(8, (String) kondisi_buku);
                            pst1.setInt(9, tahun_terbit);
                            pst1.setString(10, asal_buku);
                            pst1.setInt(11, harga);
                            pst1.setInt(12, jumlah_stock);
                            pst1.executeUpdate();
                            addHistory(harga, (String) kondisi_buku);
                            JOptionPane.showMessageDialog(null, "Data berhasil di Tambahkan");
                        } catch (Exception r) {
                            JOptionPane.showMessageDialog(null, "Data Gagal di Tambahkan");
                            System.out.println("insert simpan " + r);
                        }
                    }

                    loadTabel();
                } catch (Exception e) {
                    System.out.println("No_buku buku" + e.getMessage());
                }
                Tambah.dispose();
            }
        }

    }

    private void searchFunc(String keyword) {
        JTabel1.clearSelection();
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        String sql = "SELECT * FROM buku_view "
                + "WHERE `No buku` LIKE '%" + keyword + "%' OR "
                + "`judul buku` LIKE '%" + keyword + "%' OR "
                + "`kategori` LIKE '%" + keyword + "%' OR "
                + "`lokasi` LIKE '%" + keyword + "%'";

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
            JTabel1.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void hapus() {
        DefaultTableModel model = (DefaultTableModel) JTabel1.getModel();
        int selectedRowIndex = JTabel1.getSelectedRow();
        if (selectedRowIndex != -1) { // Pastikan baris yang dipilih tidak kosong
            // Ambil nilai dari kolom No_buku pada baris yang dipilih
            int No_buku = Integer.parseInt(model.getValueAt(selectedRowIndex, 0).toString());

            // Tampilkan dialog konfirmasi
            int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus buku dengan No Buku " + No_buku + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    // Buat koneksi ke database
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/perpusmu", "root", "");

                    // Cek apakah buku sedang dipinjam
                    String cekPeminjaman = "SELECT COUNT(*) FROM detail_peminjaman WHERE No_buku = ? AND status_peminjaman = 'Dipinjam'";
                    PreparedStatement pstCek = conn.prepareStatement(cekPeminjaman);
                    pstCek.setInt(1, No_buku);
                    ResultSet rsCek = pstCek.executeQuery();

                    if (rsCek.next() && rsCek.getInt(1) > 0) {
                        // Jika buku sedang dipinjam, tampilkan pesan dan batalkan penghapusan
                        JOptionPane.showMessageDialog(null, "Buku sedang dipinjam dan tidak bisa dihapus");
                    } else {
                        // Buat pernyataan SQL DELETE
                        String sql = "DELETE FROM buku WHERE No_buku = ?";
                        PreparedStatement pstDelete = conn.prepareStatement(sql);
                        pstDelete.setInt(1, No_buku);

                        // Eksekusi pernyataan DELETE
                        pstDelete.executeUpdate();

                        // Hapus baris dari model tabel
                        model.removeRow(selectedRowIndex);
                        loadTabel();
                    }
                } catch (Exception e) {
                    System.out.println("click" + e);
                    JOptionPane.showMessageDialog(null, "Gagal menghapus buku: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih baris data yang ingin dihapus terlebih dahulu");
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

        Tambah = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dial_kode = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        dial_judul = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        dial_jilid = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        dial_pengarang = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        dial_lokasi = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        dial_tahun = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        dial_asal = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        dial_harga = new javax.swing.JTextField();
        dial_kondisi = new javax.swing.JComboBox<>();
        btn_simpan = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        dial_stock = new javax.swing.JTextField();
        btn_cancel = new javax.swing.JButton();
        dial_kategori = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        dial_referensi = new javax.swing.JTextField();
        Edit = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        edit_kode = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        edit_judul = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        edit_jilid = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        edit_pengarang = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        edit_lokasi = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        edit_tahun = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        edit_asal = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        edit_harga = new javax.swing.JTextField();
        edit_kondisi = new javax.swing.JComboBox<>();
        btn_simpan_edit = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        edit_stock = new javax.swing.JTextField();
        btn_cancel1 = new javax.swing.JButton();
        edit_kategori = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        edit_referensi = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTabel1 = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        btn_edit = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_barcode = new javax.swing.JButton();

        Tambah.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Tambah.setUndecorated(true);

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setMaximumSize(new java.awt.Dimension(649, 649));
        jPanel2.setMinimumSize(new java.awt.Dimension(649, 649));

        jPanel3.setBackground(new java.awt.Color(63, 148, 105));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("TAMBAH BUKU");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(389, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setText("Kode Buku");

        dial_kode.setMaximumSize(new java.awt.Dimension(570, 73));
        dial_kode.setMinimumSize(new java.awt.Dimension(570, 73));
        dial_kode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_kodeActionPerformed(evt);
            }
        });
        dial_kode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_kodeKeyTyped(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel14.setText("Judul Buku");

        jLabel15.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel15.setText("Jilid");

        dial_jilid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_jilidKeyTyped(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel16.setText("Kategori");

        jLabel17.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel17.setText("Pengarang");

        jLabel18.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel18.setText("Lokasi");

        jLabel19.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel19.setText("Kondisi");

        jLabel20.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel20.setText("Tahun Terbit");

        dial_tahun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_tahunKeyTyped(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel21.setText("Asal Buku");

        jLabel22.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel22.setText("Harga");

        dial_harga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_hargaKeyTyped(evt);
            }
        });

        dial_kondisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Hilang" }));

        btn_simpan.setBackground(new java.awt.Color(63, 148, 105));
        btn_simpan.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btn_simpan.setForeground(new java.awt.Color(255, 255, 255));
        btn_simpan.setText("SIMPAN");
        btn_simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpanActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Stock");

        dial_stock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_stockKeyTyped(evt);
            }
        });

        btn_cancel.setBackground(new java.awt.Color(204, 0, 51));
        btn_cancel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btn_cancel.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancel.setText("CANCEL");
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        dial_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendidikan", "Non Pendidikan" }));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel7.setText("No Referensi");

        dial_referensi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dial_referensiKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(14, 14, 14)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(dial_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(dial_judul, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(dial_jilid, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(dial_kategori, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(dial_pengarang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(dial_referensi, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(dial_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(dial_asal, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dial_tahun, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dial_kondisi, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(dial_lokasi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(dial_stock, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(199, 199, 199)
                        .addComponent(btn_cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_simpan)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dial_referensi, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dial_tahun, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(dial_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(dial_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dial_stock, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(dial_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dial_jilid, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(dial_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(dial_pengarang, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout TambahLayout = new javax.swing.GroupLayout(Tambah.getContentPane());
        Tambah.getContentPane().setLayout(TambahLayout);
        TambahLayout.setHorizontalGroup(
            TambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 570, Short.MAX_VALUE)
        );
        TambahLayout.setVerticalGroup(
            TambahLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 514, Short.MAX_VALUE)
        );

        Edit.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Edit.setUndecorated(true);

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setMaximumSize(new java.awt.Dimension(649, 649));
        jPanel4.setMinimumSize(new java.awt.Dimension(649, 649));

        jPanel5.setBackground(new java.awt.Color(63, 148, 105));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("EDIT BUKU");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(441, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel6.setText("Kode Buku");

        edit_kode.setMaximumSize(new java.awt.Dimension(570, 73));
        edit_kode.setMinimumSize(new java.awt.Dimension(570, 73));
        edit_kode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edit_kodeKeyTyped(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel23.setText("Judul Buku");

        jLabel25.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel25.setText("Jilid");

        edit_jilid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edit_jilidKeyTyped(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel26.setText("Kategori");

        jLabel27.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel27.setText("Pengarang");

        jLabel28.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel28.setText("Lokasi");

        jLabel29.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel29.setText("Kondisi");

        jLabel30.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel30.setText("Tahun Terbit");

        edit_tahun.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edit_tahunKeyTyped(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel31.setText("Asal Buku");

        jLabel32.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel32.setText("Harga");

        edit_harga.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edit_hargaKeyTyped(evt);
            }
        });

        edit_kondisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Hilang" }));

        btn_simpan_edit.setBackground(new java.awt.Color(63, 148, 105));
        btn_simpan_edit.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btn_simpan_edit.setForeground(new java.awt.Color(255, 255, 255));
        btn_simpan_edit.setText("SIMPAN");
        btn_simpan_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_simpan_editActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("Stock");

        edit_stock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edit_stockKeyTyped(evt);
            }
        });

        btn_cancel1.setBackground(new java.awt.Color(204, 0, 51));
        btn_cancel1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btn_cancel1.setForeground(new java.awt.Color(255, 255, 255));
        btn_cancel1.setText("CANCEL");
        btn_cancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancel1ActionPerformed(evt);
            }
        });

        edit_kategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendidikan", "Non Pendidikan" }));

        jLabel8.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel8.setText("Referensi");

        edit_referensi.setMaximumSize(new java.awt.Dimension(570, 73));
        edit_referensi.setMinimumSize(new java.awt.Dimension(570, 73));
        edit_referensi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                edit_referensiKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGap(14, 14, 14)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(edit_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addGap(8, 8, 8)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(edit_referensi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                    .addComponent(edit_judul, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(edit_jilid, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(edit_kategori, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(edit_pengarang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(51, 51, 51)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(edit_harga, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                                            .addComponent(edit_asal)
                                            .addComponent(edit_tahun)
                                            .addComponent(edit_kondisi, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(edit_lokasi, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                                            .addComponent(edit_stock)))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(59, 59, 59)
                                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(199, 199, 199)
                        .addComponent(btn_cancel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_simpan_edit)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit_kode, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edit_lokasi, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(edit_kondisi, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(edit_referensi, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edit_tahun, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(edit_asal, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(edit_harga, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(edit_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edit_jilid, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(edit_kategori, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(edit_pengarang, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(edit_stock, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_simpan_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_cancel1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout EditLayout = new javax.swing.GroupLayout(Edit.getContentPane());
        Edit.getContentPane().setLayout(EditLayout);
        EditLayout.setHorizontalGroup(
            EditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 570, Short.MAX_VALUE)
        );
        EditLayout.setVerticalGroup(
            EditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 514, Short.MAX_VALUE)
        );

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
        jLabel1.setText("LIST DATA BUKU");

        txt_search.setToolTipText("");
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

        JTabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        JTabel1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No", "Kode Buku", "No Referensi", "Judul Buku", "Jilid", "Kategori", "Pengarang", "Lokasi", "Kondisi ", "Tahun Terbit", "Asal Buku", "Harga"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTabel1.setGridColor(new java.awt.Color(0, 0, 0));
        JTabel1.setShowGrid(true);
        JTabel1.getTableHeader().setReorderingAllowed(false);
        JTabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTabel1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(JTabel1);
        if (JTabel1.getColumnModel().getColumnCount() > 0) {
            JTabel1.getColumnModel().getColumn(0).setResizable(false);
            JTabel1.getColumnModel().getColumn(1).setResizable(false);
            JTabel1.getColumnModel().getColumn(3).setResizable(false);
            JTabel1.getColumnModel().getColumn(4).setResizable(false);
            JTabel1.getColumnModel().getColumn(5).setResizable(false);
            JTabel1.getColumnModel().getColumn(6).setResizable(false);
        }

        btn_tambah.setBackground(new java.awt.Color(63, 148, 105));
        btn_tambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/+.png"))); // NOI18N
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_edit.setBackground(new java.awt.Color(255, 227, 130));
        btn_edit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/edit_1160515 3.png"))); // NOI18N
        btn_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editActionPerformed(evt);
            }
        });

        btn_hapus.setBackground(new java.awt.Color(255, 145, 66));
        btn_hapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/image_button/trash-can_7343703 2.png"))); // NOI18N
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        btn_barcode.setBackground(new java.awt.Color(51, 204, 255));
        btn_barcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_10/barcode 1.png"))); // NOI18N
        btn_barcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_barcodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1259, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(83, 83, 83))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btn_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_barcode, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        txt_search.getAccessibleContext().setAccessibleName("");

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

    private void txt_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_searchActionPerformed

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        dial_kode.setText(null);
        dial_judul.setText(null);
        dial_referensi.setText(null);
        dial_jilid.setText(null);
        dial_kategori.setSelectedItem(null);
        dial_pengarang.setText(null);
        dial_lokasi.setText(null);
        dial_kondisi.setSelectedItem(null);
        dial_tahun.setText(String.valueOf(""));
        dial_asal.setText(null);
        dial_harga.setText(String.valueOf(""));
        dial_stock.setText(String.valueOf(""));
        dial_kode.setEnabled(true);
        noBukuEdit = -1;

        Tambah.setVisible(true);
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_editActionPerformed
        int row = JTabel1.getSelectedRow();
        System.out.println("index " + row);
        GetEdit(row);
    }//GEN-LAST:event_btn_editActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        hapus();
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
        Simpan();
    }//GEN-LAST:event_btn_simpanActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        Tambah.dispose();
    }//GEN-LAST:event_btn_cancelActionPerformed

    private void JTabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTabel1MouseClicked
        int index = JTabel1.getSelectedRow();
        TableModel model = JTabel1.getModel();
        kode_buku = model.getValueAt(index, 1).toString();
    }//GEN-LAST:event_JTabel1MouseClicked

    private void jPanel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel1AncestorAdded
        try {
            // TODO add your handling code here:
            loadTabel();
            Jdialog();

        } catch (SQLException ex) {
            Logger.getLogger(Buku.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jPanel1AncestorAdded

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        try {
            // TODO add your handling code here:
            loadTabel();

        } catch (SQLException ex) {
            Logger.getLogger(Buku.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formAncestorAdded

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        Tambah.dispose();
    }//GEN-LAST:event_formMouseClicked

    private void txt_searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyReleased
        String keyword = txt_search.getText().trim();
        searchFunc(keyword);
    }//GEN-LAST:event_txt_searchKeyReleased

    private void btn_simpan_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpan_editActionPerformed
        // TODO add your handling code here:
        editData();
    }//GEN-LAST:event_btn_simpan_editActionPerformed

    private void btn_cancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancel1ActionPerformed
        // TODO add your handling code here:
        clearEdit();
        Edit.dispose();
    }//GEN-LAST:event_btn_cancel1ActionPerformed

    private void dial_kodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_kodeKeyTyped

        char c = evt.getKeyChar();

        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        String batas = dial_kode.getText();
        int batasan = batas.length();
        if (batasan >= 4) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa 4 karakter");
            evt.consume();
            return;
        }

        if (!Character.isLetterOrDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya boleh huruf dan angka");
            evt.consume();
        }


    }//GEN-LAST:event_dial_kodeKeyTyped

    private void dial_referensiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_referensiKeyTyped
        char c = evt.getKeyChar();

        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        String batas = dial_referensi.getText();
        int batasan = batas.length();
        if (batasan >= 13) {
            JOptionPane.showMessageDialog(Tambah, "Tidak boleh lebih dari 13 angka");
            evt.consume();
            return;
        }

        if (!Character.isLetterOrDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya boleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_dial_referensiKeyTyped

    private void dial_kodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_kodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_kodeActionPerformed

    private void edit_kodeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edit_kodeKeyTyped
        char c = evt.getKeyChar();

        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        String batas = dial_kode.getText();
        int batasan = batas.length();
        if (batasan >= 4) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa 4 karakter");
            evt.consume();
            return;
        }

        if (!Character.isLetterOrDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya boleh huruf dan angka");
            evt.consume();
        }

    }//GEN-LAST:event_edit_kodeKeyTyped

    private void edit_referensiKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edit_referensiKeyTyped
        char c = evt.getKeyChar();

        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        String batas = dial_referensi.getText();
        int batasan = batas.length();
        if (batasan >= 13) {
            JOptionPane.showMessageDialog(Tambah, "Tidak boleh lebih dari 13 angka");
            evt.consume();
            return;
        }

        if (!Character.isLetterOrDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya boleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_edit_referensiKeyTyped

    private void txt_searchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_searchKeyPressed

    private void btn_barcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_barcodeActionPerformed

        try {
            int row = JTabel1.getSelectedRow();
            String sqlQuery;
            String s;

            if (row == -1) {
                sqlQuery = "SELECT * FROM buku";

            } else {
                s = JTabel1.getValueAt(row, 1).toString();
                sqlQuery = "SELECT * FROM buku WHERE referensi = '" + s + "'";
            }

            String pathi = "src/Buku/report1.jrxml";

            JasperDesign jasperDesign = JRXmlLoader.load(pathi);
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sqlQuery);
            jasperDesign.setQuery(newQuery);

            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            Map<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String tanggalWaktu = dateFormat.format(new Date());
            String namaFile = "satu_br-" + tanggalWaktu + ".pdf";

            String path = System.getProperty("user.home") + "/Documents/barcode/";
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            JasperExportManager.exportReportToPdfFile(jasperPrint, path + namaFile);
            JOptionPane.showMessageDialog(null, "Data berhasil disimpan di " + path);

        } catch (JRException ex) {
            Logger.getLogger(Buku.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage());
        }

    }//GEN-LAST:event_btn_barcodeActionPerformed

    private void dial_jilidKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_jilidKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        // Izinkan "Backspace" dan "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Dapatkan teks yang ada saat ini
        String key = dial_jilid.getText();

        // Cek panjang input
        if (key.length() >= 2) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 2 angka");
            evt.consume();
            return;
        }

        // Cek apakah karakter adalah angka
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa diisi oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_dial_jilidKeyTyped

    private void dial_tahunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_tahunKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        // Allow "Backspace" and "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Check the length of the input
        String key = dial_tahun.getText();
        if (key.length() >= 4) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 4 angka");
            evt.consume();
            return;
        }

        // Check if the character is a digit
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa Di isi Oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_dial_tahunKeyTyped

    private void dial_hargaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_hargaKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        // Izinkan "Backspace" dan "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Cek panjang input
        String key = dial_harga.getText();
        if (key.length() == 10) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 10 angka");
            evt.consume();
            return;
        }

        // Cek apakah karakter adalah angka
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa diisi oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_dial_hargaKeyTyped

    private void dial_stockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dial_stockKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        // Izinkan "Backspace" dan "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Cek panjang input
        String key = dial_stock.getText();
        if (key.length() >= 4) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 4 angka");
            evt.consume();
            return;
        }

        // Cek apakah karakter adalah angka
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa diisi oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_dial_stockKeyTyped

    private void edit_jilidKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edit_jilidKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        // Izinkan "Backspace" dan "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Dapatkan teks yang ada saat ini
        String key = dial_jilid.getText();

        // Cek panjang input
        if (key.length() >= 2) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 2 angka");
            evt.consume();
            return;
        }

        // Cek apakah karakter adalah angka
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa diisi oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_edit_jilidKeyTyped

    private void edit_tahunKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edit_tahunKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        // Allow "Backspace" and "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Check the length of the input
        String key = dial_tahun.getText();
        if (key.length() >= 4) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 4 angka");
            evt.consume();
            return;
        }

        // Check if the character is a digit
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa Di isi Oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_edit_tahunKeyTyped

    private void edit_hargaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edit_hargaKeyTyped
        char c = evt.getKeyChar();

        // Izinkan "Backspace" dan "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Cek panjang input
        String key = dial_harga.getText();
        if (key.length() == 10) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 10 angka");
            evt.consume();
            return;
        }

        // Cek apakah karakter adalah angka
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa diisi oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_edit_hargaKeyTyped

    private void edit_stockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_edit_stockKeyTyped
        char c = evt.getKeyChar();

        // Izinkan "Backspace" dan "Delete"
        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
            return;
        }

        // Cek panjang input
        String key = dial_stock.getText();
        if (key.length() >= 4) {
            JOptionPane.showMessageDialog(Tambah, "Maksimal 4 angka");
            evt.consume();
            return;
        }

        // Cek apakah karakter adalah angka
        if (!Character.isDigit(c)) {
            JOptionPane.showMessageDialog(Tambah, "Hanya bisa diisi oleh angka");
            evt.consume();
        }
    }//GEN-LAST:event_edit_stockKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog Edit;
    private javax.swing.JTable JTabel1;
    private javax.swing.JDialog Tambah;
    private javax.swing.JButton btn_barcode;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_cancel1;
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_simpan_edit;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JTextField dial_asal;
    private javax.swing.JTextField dial_harga;
    private javax.swing.JTextField dial_jilid;
    private javax.swing.JTextField dial_judul;
    private javax.swing.JComboBox<String> dial_kategori;
    private javax.swing.JTextField dial_kode;
    private javax.swing.JComboBox<String> dial_kondisi;
    private javax.swing.JTextField dial_lokasi;
    private javax.swing.JTextField dial_pengarang;
    private javax.swing.JTextField dial_referensi;
    private javax.swing.JTextField dial_stock;
    private javax.swing.JTextField dial_tahun;
    private javax.swing.JTextField edit_asal;
    private javax.swing.JTextField edit_harga;
    private javax.swing.JTextField edit_jilid;
    private javax.swing.JTextField edit_judul;
    private javax.swing.JComboBox<String> edit_kategori;
    private javax.swing.JTextField edit_kode;
    private javax.swing.JComboBox<String> edit_kondisi;
    private javax.swing.JTextField edit_lokasi;
    private javax.swing.JTextField edit_pengarang;
    private javax.swing.JTextField edit_referensi;
    private javax.swing.JTextField edit_stock;
    private javax.swing.JTextField edit_tahun;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txt_search;
    // End of variables declaration//GEN-END:variables
}
