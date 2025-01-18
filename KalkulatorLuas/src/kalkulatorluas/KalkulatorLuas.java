/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package kalkulatorluas;

/**
 *
 * @author amlab
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KalkulatorLuas extends JFrame {
    private JTextField sisiField, jariField;
    private JLabel hasilLabel;
    
    public KalkulatorLuas() {
        // Setup frame
        setTitle("Kalkulator Luas");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));
        
        // Panel untuk persegi
        JPanel persegiPanel = new JPanel();
        persegiPanel.setBorder(BorderFactory.createTitledBorder("Persegi"));
        persegiPanel.setLayout(new FlowLayout());
        
        JLabel sisiLabel = new JLabel("Sisi:");
        sisiField = new JTextField(10);
        JButton hitungPersegiButton = new JButton("Hitung Luas Persegi");
        
        persegiPanel.add(sisiLabel);
        persegiPanel.add(sisiField);
        persegiPanel.add(hitungPersegiButton);
        
        // Panel untuk lingkaran
        JPanel lingkaranPanel = new JPanel();
        lingkaranPanel.setBorder(BorderFactory.createTitledBorder("Lingkaran"));
        lingkaranPanel.setLayout(new FlowLayout());
        
        JLabel jariLabel = new JLabel("Jari-jari:");
        jariField = new JTextField(10);
        JButton hitungLingkaranButton = new JButton("Hitung Luas Lingkaran");
        
        lingkaranPanel.add(jariLabel);
        lingkaranPanel.add(jariField);
        lingkaranPanel.add(hitungLingkaranButton);
        
        // Panel untuk hasil
        JPanel hasilPanel = new JPanel();
        hasilLabel = new JLabel("Hasil akan ditampilkan di sini");
        hasilPanel.add(hasilLabel);
        
        // Menambahkan panel ke frame
        add(persegiPanel);
        add(lingkaranPanel);
        add(hasilPanel);
        
        // Menambahkan action listener untuk tombol persegi
        hitungPersegiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitungLuasPersegi();
            }
        });
        
        // Menambahkan action listener untuk tombol lingkaran
        hitungLingkaranButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitungLuasLingkaran();
            }
        });
    }
    
    private void hitungLuasPersegi() {
        try {
            double sisi = Double.parseDouble(sisiField.getText());
            double luas = sisi * sisi;
            hasilLabel.setText(String.format("Luas Persegi: %.2f", luas));
        } catch (NumberFormatException e) {
            hasilLabel.setText("Masukkan angka yang valid!");
        }
    }
    
    private void hitungLuasLingkaran() {
        try {
            double jari = Double.parseDouble(jariField.getText());
            double luas = Math.PI * jari * jari;
            hasilLabel.setText(String.format("Luas Lingkaran: %.2f", luas));
        } catch (NumberFormatException e) {
            hasilLabel.setText("Masukkan angka yang valid!");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KalkulatorLuas().setVisible(true);
            }
        });
    }
}
