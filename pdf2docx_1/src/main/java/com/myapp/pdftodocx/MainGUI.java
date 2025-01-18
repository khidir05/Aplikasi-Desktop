package com.myapp.pdftodocx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainGUI extends JFrame {
    private JTextField inputPathField;
    private JTextField outputPathField;
    private JPasswordField passwordField;
    private JButton inputBrowseButton;
    private JButton outputBrowseButton;
    private JButton convertButton;
    private JProgressBar progressBar;
    private JCheckBox isProtectedCheckbox;
    
    public MainGUI() {
        setTitle("PDF ke DOCX Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Input file section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(new JLabel("File PDF:"), gbc);
        
        inputPathField = new JTextField(30);
        gbc.gridx = 1;
        add(inputPathField, gbc);
        
        inputBrowseButton = new JButton("Browse");
        gbc.gridx = 2;
        add(inputBrowseButton, gbc);
        
        // Password protected checkbox
        isProtectedCheckbox = new JCheckBox("PDF Dilindungi Password");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(isProtectedCheckbox, gbc);
        
        // Password field (initially invisible)
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        passwordField.setEnabled(false);
        passwordPanel.add(passwordField);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(passwordPanel, gbc);
        
        // Output file section
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(new JLabel("Simpan ke:"), gbc);
        
        outputPathField = new JTextField(30);
        gbc.gridx = 1;
        add(outputPathField, gbc);
        
        outputBrowseButton = new JButton("Browse");
        gbc.gridx = 2;
        add(outputBrowseButton, gbc);
        
        // Convert button
        convertButton = new JButton("Konversi");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        add(convertButton, gbc);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, gbc);
        
        // Add button listeners
        inputBrowseButton.addActionListener(e -> browsePDFFile());
        outputBrowseButton.addActionListener(e -> browseOutputFile());
        convertButton.addActionListener(e -> startConversion());
        isProtectedCheckbox.addActionListener(e -> passwordField.setEnabled(isProtectedCheckbox.isSelected()));
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void browsePDFFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".pdf") || f.isDirectory();
            }
            public String getDescription() {
                return "PDF Files (*.pdf)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            inputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void browseOutputFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".docx") || f.isDirectory();
            }
            public String getDescription() {
                return "Word Files (*.docx)";
            }
        });
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".docx")) {
                path += ".docx";
            }
            outputPathField.setText(path);
        }
    }
    
    private void startConversion() {
        if (inputPathField.getText().isEmpty() || outputPathField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Mohon pilih file PDF dan lokasi penyimpanan terlebih dahulu.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (isProtectedCheckbox.isSelected() && passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this,
                "Mohon masukkan password untuk PDF yang dilindungi.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Nonaktifkan tombol selama proses konversi
        convertButton.setEnabled(false);
        progressBar.setIndeterminate(true);
        
        // Mulai proses konversi di thread terpisah
        new Thread(() -> {
            try {
                PDFConverter converter = new PDFConverter();
                String password = isProtectedCheckbox.isSelected() ? 
                    new String(passwordField.getPassword()) : null;
                converter.convert(inputPathField.getText(), outputPathField.getText(), password);
                
                SwingUtilities.invokeLater(() -> {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    JOptionPane.showMessageDialog(this,
                        "Konversi selesai!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    convertButton.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);
                });
            }
        }).start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainGUI().setVisible(true);
        });
    }
}