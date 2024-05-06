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
import java.awt.event.KeyEvent;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import Buku.BukuMain;

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
    }

    private void addHistory(int harga, String kondisi) {
        String sql = "insert into history (id_buku, peristiwa, tanggal, harga_buku, keterangan) "
                + "values ((select count(*) + 1 as id_buku from buku), 'masuk', current_date(), ?, ?)";
        String keterangan = "Buku ini masuk dalam kondisi " + kondisi;
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(2, harga);
            pst.setString(3, keterangan);
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
                //all cells false
                return false;
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Buku");
        model.addColumn("No Referensi");
        model.addColumn("Judul Buku");
        model.addColumn("Jilid");
        model.addColumn("Kategori ");
        model.addColumn("Pengarang");
        model.addColumn("Lokasi");
        model.addColumn("Kondisi");
        model.addColumn("Tahun Terbit");
        model.addColumn("Asal Buku");
        model.addColumn("Harga");
        model.addColumn("Stock");

        try {
            pst = con.prepareStatement("SELECT * FROM buku");
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("No_buku"), rs.getString("kode_buku"), rs.getString("referensi"), rs.getString("judul_buku"), rs.getString("jilid"), rs.getString("kategori"), rs.getString("pengarang"), rs.getString("lokasi"), rs.getString("kondisi_buku"), rs.getInt("tahun_terbit"), rs.getString("asal_buku"), rs.getInt("harga"), rs.getInt("jumlah_stock")});
            }
            JTabel1.setModel(model);
        } catch (Exception e) {
            System.out.println("loadTable" + e);
        }
    }

    private void editData() {
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
        try {
            pst = con.prepareStatement("UPDATE buku SET kode_buku = ?, referensi = ?, judul_buku = ?, jilid = ?, kategori = ?, pengarang = ?, lokasi = ?, kondisi_buku = ?, tahun_terbit = ?, asal_buku = ?, harga =?, jumlah_stock = ? WHERE No_buku = ?");
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

        } catch (Exception e) {
            System.out.println("Edit" + e);
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
        dial_kode1 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        dial_judul1 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        dial_jilid1 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        dial_pengarang1 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        dial_lokasi1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        dial_tahun1 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        dial_asal1 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        dial_harga1 = new javax.swing.JTextField();
        dial_kondisi1 = new javax.swing.JComboBox<>();
        btn_simpan_edit = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        dial_stock1 = new javax.swing.JTextField();
        btn_cancel1 = new javax.swing.JButton();
        dial_kategori1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txt_search = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTabel1 = new javax.swing.JTable();
        btn_tambah = new javax.swing.JButton();
        btn_edit = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        Tambah.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        Tambah.setUndecorated(true);
        Tambah.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                TambahFocusLost(evt);
            }
        });
        Tambah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TambahKeyPressed(evt);
            }
        });

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

        jLabel14.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel14.setText("Judul Buku");

        jLabel15.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel15.setText("Jilid");

        dial_jilid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_jilidActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel16.setText("Kategori");

        jLabel17.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel17.setText("Pengarang");

        jLabel18.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel18.setText("Lokasi");

        dial_lokasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_lokasiActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel19.setText("Kondisi");

        jLabel20.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel20.setText("Tahun Terbit");

        jLabel21.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel21.setText("Asal Buku");

        dial_asal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_asalActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel22.setText("Harga");

        dial_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_hargaActionPerformed(evt);
            }
        });

        dial_kondisi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Hilang" }));
        dial_kondisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_kondisiActionPerformed(evt);
            }
        });

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

        dial_stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_stockActionPerformed(evt);
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
        dial_kategori.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_kategoriActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel7.setText("No Referensi");

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
        Edit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                EditFocusLost(evt);
            }
        });
        Edit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EditKeyPressed(evt);
            }
        });

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
                .addContainerGap(389, Short.MAX_VALUE))
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

        dial_kode1.setMaximumSize(new java.awt.Dimension(570, 73));
        dial_kode1.setMinimumSize(new java.awt.Dimension(570, 73));

        jLabel23.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel23.setText("Judul Buku");

        jLabel25.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel25.setText("Jilid");

        dial_jilid1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_jilid1ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel26.setText("Kategori");

        jLabel27.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel27.setText("Pengarang");

        jLabel28.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel28.setText("Lokasi");

        dial_lokasi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_lokasi1ActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel29.setText("Kondisi");

        jLabel30.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel30.setText("Tahun Terbit");

        jLabel31.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel31.setText("Asal Buku");

        dial_asal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_asal1ActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel32.setText("Harga");

        dial_harga1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_harga1ActionPerformed(evt);
            }
        });

        dial_kondisi1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Baik", "Rusak", "Hilang" }));
        dial_kondisi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_kondisi1ActionPerformed(evt);
            }
        });

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

        dial_stock1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_stock1ActionPerformed(evt);
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

        dial_kategori1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendidikan", "Non Pendidikan" }));
        dial_kategori1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dial_kategori1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(238, 238, 238)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dial_jilid1)
                            .addComponent(dial_judul1)
                            .addComponent(dial_pengarang1)
                            .addComponent(dial_kode1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dial_kategori1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(51, 51, 51)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(dial_harga1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(dial_asal1, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dial_tahun1, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dial_kondisi1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(dial_lokasi1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(199, 199, 199)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btn_cancel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn_simpan_edit))
                            .addComponent(dial_stock1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                    .addComponent(dial_kode1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_lokasi1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_judul1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_kondisi1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_jilid1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_tahun1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_asal1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_kategori1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dial_pengarang1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dial_harga1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dial_stock1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_edit.setBackground(new java.awt.Color(255, 227, 130));
        btn_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_editActionPerformed(evt);
            }
        });

        btn_hapus.setBackground(new java.awt.Color(255, 145, 66));
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Search");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1259, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btn_edit, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_search, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
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
//        get index table selected
        int row = JTabel1.getSelectedRow();
        System.out.println("index " + row);
//        cek jika tidak ada row yang diselect
        if (row < 0) {
            JOptionPane.showMessageDialog(jPanel4, "Pilih Dahulu Data Yang Akan Di Ubah");
        }
        try {
            //get data dari table  berdasarkan row index dan colom keberapa
            //0 index colom no buku
            int noBuku = Integer.parseInt((String) JTabel1.getValueAt(row, 0));
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

            dial_kode.setText(kode_buku);
            dial_judul.setText(judul_buku);
            dial_referensi.setText(referensi);
            dial_jilid.setText(jilid);
            dial_kategori.setSelectedItem(kategori);
            dial_pengarang.setText(pengarang);
            dial_lokasi.setText(lokasi);
            dial_kondisi.setSelectedItem(kondisi_buku);
            dial_tahun.setText(String.valueOf(tahunterbit));
            dial_asal.setText(asal_buku);
            dial_harga.setText(String.valueOf(harga));
            dial_stock.setText(String.valueOf(jumlah_stock));
            dial_kode.enable(false);
            noBukuEdit = noBuku;
            Tambah.setVisible(true);
        } catch (Exception e) {
            System.out.println("click" + e.getMessage());
            JOptionPane.showMessageDialog(jPanel4, "Pilih Dahulu Data Yang Akan Di Ubah");
        }
    }//GEN-LAST:event_btn_editActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
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

                    // Buat pernyataan SQL DELETE
                    String sql = "DELETE FROM buku WHERE No_buku = ?";
                    PreparedStatement pst = con.prepareStatement(sql);
                    pst.setInt(1, No_buku);

                    // Eksekusi pernyataan DELETE
                    pst.executeUpdate();

                    // Hapus baris dari model tabel
                    model.removeRow(selectedRowIndex);
                } catch (Exception e) {
                    System.out.println("click" + e);
                    JOptionPane.showMessageDialog(jPanel2, "Pilih Dahulu Data Yang Akan Di Hapus");
                }
            }
        }
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpanActionPerformed
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

        if (kode_buku == null || referensi == null || judul_buku == null || jilid == null || kategori == null || pengarang == null || lokasi == null || kondisi_buku == null || tahun_terbit == -1 || asal_buku == null || harga == -1 || jumlah_stock == -1) {
            JOptionPane.showMessageDialog(Tambah, "Anda Harus Mengisi Semua Data Terlebih Dahulu");
        } else {
            int result = JOptionPane.showConfirmDialog(Tambah, "Apakah Anda Ingin Menyimpan?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    pst = con.prepareStatement("SELECT jumlah_stock FROM buku WHERE kode_buku = ? and referensi = ?"
                            + " and judul_buku = ? and jilid = ? and kategori = ? and pengarang = ? and lokasi = ? "
                            + "and kondisi_buku = ? tahun_terbit = ? and asal_buku = ? and harga = ?");
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
                    rs = pst1.executeQuery();
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
                            addHistory(harga, lokasi);
                        } catch (Exception r) {
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


    }//GEN-LAST:event_btn_simpanActionPerformed

    private void dial_lokasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_lokasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_lokasiActionPerformed

    private void dial_kondisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_kondisiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_kondisiActionPerformed

    private void dial_hargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_hargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_hargaActionPerformed

    private void dial_stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_stockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_stockActionPerformed

    private void dial_asalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_asalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_asalActionPerformed

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        Tambah.dispose();
    }//GEN-LAST:event_btn_cancelActionPerformed

    private void dial_kategoriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_kategoriActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_kategoriActionPerformed

    private void dial_jilidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_jilidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_jilidActionPerformed

    private void JTabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTabel1MouseClicked
        int index = JTabel1.getSelectedRow();
        TableModel model = JTabel1.getModel();
        kode_buku = model.getValueAt(index, 1).toString();
    }//GEN-LAST:event_JTabel1MouseClicked

    private void TambahFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TambahFocusLost
        Tambah.dispose();
    }//GEN-LAST:event_TambahFocusLost

    private void TambahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TambahKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            Tambah.setVisible(true);
        }
    }//GEN-LAST:event_TambahKeyPressed

    private void jPanel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel1AncestorAdded
        try {
            // TODO add your handling code here:
            loadTabel();
            Jdialog();
        } catch (SQLException ex) {
            Logger.getLogger(Buku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jPanel1AncestorAdded

    private void formAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_formAncestorAdded
        try {
            // TODO add your handling code here:
            loadTabel();
        } catch (SQLException ex) {
            Logger.getLogger(Buku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formAncestorAdded

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        Tambah.dispose();
    }//GEN-LAST:event_formMouseClicked

    private void txt_searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_searchKeyReleased
        String keyword = txt_search.getText().trim();
        String sql = "SELECT * FROM buku "
                + "WHERE No_buku LIKE '%" + keyword + "%' OR "
                + "kode_buku LIKE '%" + keyword + "%' OR "
                + "judul_buku LIKE '%" + keyword + "%' OR "
                + "jilid LIKE '%" + keyword + "%' OR "
                + "kategori LIKE '%" + keyword + "%' OR "
                + "pengarang LIKE '%" + keyword + "%' OR "
                + "lokasi LIKE '%" + keyword + "%' OR "
                + "kondisi_buku LIKE '%" + keyword + "%' OR "
                + "tahun_terbit LIKE '%" + keyword + "%' OR "
                + "asal_buku LIKE '%" + keyword + "%' OR "
                + "harga LIKE '%" + keyword + "%' OR "
                + "jumlah_stock LIKE '%" + keyword + "%'";

        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            // Creating a model to store the filtered data
            DefaultTableModel filteredModel = new DefaultTableModel();
            filteredModel.addColumn("No");
            filteredModel.addColumn("Kode Buku");
            filteredModel.addColumn("Judul Buku");
            filteredModel.addColumn("Jilid");
            filteredModel.addColumn("Kategori");
            filteredModel.addColumn("Pengarang");
            filteredModel.addColumn("Lokasi");
            filteredModel.addColumn("Kondisi");
            filteredModel.addColumn("Tahun Terbit");
            filteredModel.addColumn("Asal Buku");
            filteredModel.addColumn("Harga");
            filteredModel.addColumn("Stock");

            while (rs.next()) {
                filteredModel.addRow(new Object[]{
                    rs.getString("No_buku"),
                    rs.getString("kode_buku"),
                    rs.getString("judul_buku"),
                    rs.getString("jilid"),
                    rs.getString("kategori"),
                    rs.getString("pengarang"),
                    rs.getString("lokasi"),
                    rs.getString("kondisi_buku"),
                    rs.getString("tahun_terbit"),
                    rs.getString("asal_buku"),
                    rs.getString("harga"),
                    rs.getString("jumlah_stock")
                });
            }

            // Set the filtered model to the JTable
            JTabel1.setModel(filteredModel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_txt_searchKeyReleased

    private void dial_jilid1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_jilid1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_jilid1ActionPerformed

    private void dial_lokasi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_lokasi1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_lokasi1ActionPerformed

    private void dial_asal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_asal1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_asal1ActionPerformed

    private void dial_harga1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_harga1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_harga1ActionPerformed

    private void dial_kondisi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_kondisi1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_kondisi1ActionPerformed

    private void btn_simpan_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_simpan_editActionPerformed
        // TODO add your handling code here:
        editData();
    }//GEN-LAST:event_btn_simpan_editActionPerformed

    private void dial_stock1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_stock1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_stock1ActionPerformed

    private void btn_cancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancel1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cancel1ActionPerformed

    private void dial_kategori1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dial_kategori1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dial_kategori1ActionPerformed

    private void EditFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_EditFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_EditFocusLost

    private void EditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog Edit;
    private javax.swing.JTable JTabel1;
    private javax.swing.JDialog Tambah;
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_cancel1;
    private javax.swing.JButton btn_edit;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_simpan;
    private javax.swing.JButton btn_simpan_edit;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JTextField dial_asal;
    private javax.swing.JTextField dial_asal1;
    private javax.swing.JTextField dial_harga;
    private javax.swing.JTextField dial_harga1;
    private javax.swing.JTextField dial_jilid;
    private javax.swing.JTextField dial_jilid1;
    private javax.swing.JTextField dial_judul;
    private javax.swing.JTextField dial_judul1;
    private javax.swing.JComboBox<String> dial_kategori;
    private javax.swing.JComboBox<String> dial_kategori1;
    private javax.swing.JTextField dial_kode;
    private javax.swing.JTextField dial_kode1;
    private javax.swing.JComboBox<String> dial_kondisi;
    private javax.swing.JComboBox<String> dial_kondisi1;
    private javax.swing.JTextField dial_lokasi;
    private javax.swing.JTextField dial_lokasi1;
    private javax.swing.JTextField dial_pengarang;
    private javax.swing.JTextField dial_pengarang1;
    private javax.swing.JTextField dial_referensi;
    private javax.swing.JTextField dial_stock;
    private javax.swing.JTextField dial_stock1;
    private javax.swing.JTextField dial_tahun;
    private javax.swing.JTextField dial_tahun1;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txt_search;
    // End of variables declaration//GEN-END:variables
}
