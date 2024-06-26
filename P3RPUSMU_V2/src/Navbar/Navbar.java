/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Navbar;

import Admin.Admin;
import Anggota.Anggota;
import Buku.Buku;
import Buku.BukuMain;
import Login.users;
import Laporan.Psatu;
import Transaksi.Transaksi;
//import Buku.Utama;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import dashboard.Dashboard1;
import denda.Denda;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author Septian Galoh P
 */
public class Navbar extends javax.swing.JFrame {

    /**
     * Creates new form Navbar
     */
    private CardLayout card;

    public Navbar() throws SQLException {
        initComponents();

//        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        screenRes();
        lokasi();
        setJOptione();
        main.putClientProperty(FlatClientProperties.STYLE, "arc:30");
        card = new CardLayout();
        main.setLayout(card);
        icon();

        main.add(new Anggota(), "anggota");
        main.add(new BukuMain(), "buku");
        main.add(new Denda(), "denda");
        main.add(new Admin(), "admin");
        main.add(new Psatu(), "laporan");
        main.add(new Transaksi(), "Transaksi");
        main.add(new Dashboard1(), "Dashboard");

        card.show(main, "Dashboard");
    }

    private void lokasi() {
        Dimension layar = Toolkit.getDefaultToolkit().getScreenSize();
        int x = layar.width / 2 - this.getSize().width / 2;
        int y = layar.height / 2 - this.getSize().height / 2;
        this.setLocation(x, y);
        siz();
    }

    private void icon() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/img_10/icon.png"));
        this.setIconImage(icon.getImage());
    }

    private void siz() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
        setVisible(true);
    }

    private void setJOptione() {
        UIManager.put("OptionPane.minimumSize", new Dimension(200, 100));
        UIManager.put("OptionPane.messageFont", new FontUIResource(new Font(
                "Arial", Font.BOLD, 14)));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));
        UIManager.put("OptionPane.yesButtonText", "Ya");
        UIManager.put("OptionPane.noButtonText", "Tidak");
    }

    public void setIconText(String text) {
        icon.setText(text);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        main = new javax.swing.JPanel();
        navbar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dashboard = new javax.swing.JButton();
        transaksi = new javax.swing.JButton();
        laporan = new javax.swing.JButton();
        buku = new javax.swing.JButton();
        anggota = new javax.swing.JButton();
        admin = new javax.swing.JButton();
        denda = new javax.swing.JButton();
        Logout = new javax.swing.JButton();
        icon = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(153, 153, 153));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setUndecorated(true);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
        );

        navbar.setBackground(new java.awt.Color(63, 148, 105));
        navbar.setPreferredSize(new java.awt.Dimension(1438, 67));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/18-w9-NYWIuY-transformed 2.png"))); // NOI18N

        dashboard.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        dashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/216242_home_icon 48.png"))); // NOI18N
        dashboard.setText("Dashboard");
        dashboard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dashboard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        dashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboardActionPerformed(evt);
            }
        });

        transaksi.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        transaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/icons8-order-30 45.png"))); // NOI18N
        transaksi.setText("Transaksi");
        transaksi.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transaksi.setPreferredSize(new java.awt.Dimension(107, 47));
        transaksi.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transaksiActionPerformed(evt);
            }
        });

        laporan.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        laporan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/Vector1.png"))); // NOI18N
        laporan.setText("Laporan");
        laporan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        laporan.setPreferredSize(new java.awt.Dimension(107, 47));
        laporan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        laporan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                laporanActionPerformed(evt);
            }
        });

        buku.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        buku.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/Vector-1.png"))); // NOI18N
        buku.setText("Buku");
        buku.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buku.setPreferredSize(new java.awt.Dimension(107, 47));
        buku.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        buku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bukuActionPerformed(evt);
            }
        });

        anggota.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        anggota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/Vector1-1.png"))); // NOI18N
        anggota.setText("Anggota");
        anggota.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        anggota.setPreferredSize(new java.awt.Dimension(107, 47));
        anggota.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        anggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anggotaActionPerformed(evt);
            }
        });

        admin.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        admin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/Vector.png"))); // NOI18N
        admin.setText("Admin");
        admin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        admin.setPreferredSize(new java.awt.Dimension(107, 47));
        admin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        admin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminActionPerformed(evt);
            }
        });

        denda.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        denda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/money-icon-transparent-background-7 1 (1).png"))); // NOI18N
        denda.setText("Denda");
        denda.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        denda.setPreferredSize(new java.awt.Dimension(107, 47));
        denda.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        denda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dendaActionPerformed(evt);
            }
        });

        Logout.setFont(new java.awt.Font("Arial Black", 1, 11)); // NOI18N
        Logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/logout-1024 (1) 1.png"))); // NOI18N
        Logout.setText("Logout");
        Logout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Logout.setPreferredSize(new java.awt.Dimension(107, 47));
        Logout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutActionPerformed(evt);
            }
        });

        icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img_15/icons1.png"))); // NOI18N
        icon.setText("Admin");
        icon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        icon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout navbarLayout = new javax.swing.GroupLayout(navbar);
        navbar.setLayout(navbarLayout);
        navbarLayout.setHorizontalGroup(
            navbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navbarLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(dashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(transaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(laporan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(buku, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(anggota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(denda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(Logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addComponent(icon, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addGap(26, 26, 26))
        );
        navbarLayout.setVerticalGroup(
            navbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, navbarLayout.createSequentialGroup()
                .addGroup(navbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(navbarLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(icon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, navbarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(navbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(transaksi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(laporan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buku, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(anggota, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(denda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Logout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
            .addComponent(navbar, javax.swing.GroupLayout.DEFAULT_SIZE, 1217, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(navbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void dashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dashboardActionPerformed
        // TODO add your handling code here:
        Dashboard1.resetcomponen();
        card.show(main, "Dashboard");
    }//GEN-LAST:event_dashboardActionPerformed

    private void transaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transaksiActionPerformed
        // TODO add your handling code here:
        card.show(main, "Transaksi");
    }//GEN-LAST:event_transaksiActionPerformed

    private void laporanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_laporanActionPerformed
        // TODO add your handling code here:
        card.show(main, "laporan");
    }//GEN-LAST:event_laporanActionPerformed

    private void bukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bukuActionPerformed
        // TODO add your handling code here:
        card.show(main, "buku");
    }//GEN-LAST:event_bukuActionPerformed

    private void anggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anggotaActionPerformed
        // TODO add your handling code here:
        card.show(main, "anggota");
    }//GEN-LAST:event_anggotaActionPerformed

    private void adminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminActionPerformed
        // TODO add your handling code here:
        card.show(main, "admin");
    }//GEN-LAST:event_adminActionPerformed

    private void dendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dendaActionPerformed
        // TODO add your handling code here:
        card.show(main, "denda");
    }//GEN-LAST:event_dendaActionPerformed

    private void LogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutActionPerformed
        // TODO add your handling code here:
        int dialogResult = JOptionPane.showConfirmDialog(null, "Apakah Anda Ingin Keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (dialogResult == JOptionPane.YES_OPTION) {
            this.dispose();
        }

    }//GEN-LAST:event_LogoutActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

    }//GEN-LAST:event_formMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Navbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Navbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Navbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Navbar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
        }

        UIManager.put("Button.arc", 15);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //    System.setProperty("sun.java2d.dpiaware", "false");
                    new Navbar().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(Navbar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Logout;
    public static javax.swing.JButton admin;
    private javax.swing.JButton anggota;
    private javax.swing.JButton buku;
    private javax.swing.JButton dashboard;
    private javax.swing.JButton denda;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton laporan;
    private javax.swing.JPanel main;
    private javax.swing.JPanel navbar;
    private javax.swing.JButton transaksi;
    // End of variables declaration//GEN-END:variables
}
