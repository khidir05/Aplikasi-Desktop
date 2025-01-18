import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.FileOutputStream;

public class PDFtoDocxConverter extends JFrame {
    private JTextField pdfPathField;
    private JTextField docxPathField;
    private JButton pdfBrowseButton;
    private JButton docxBrowseButton;
    private JButton convertButton;
    
    public PDFtoDocxConverter() {
        setTitle("PDF to DOCX Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Initialize components
        JLabel pdfLabel = new JLabel("PDF File:");
        pdfPathField = new JTextField(30);
        pdfBrowseButton = new JButton("Browse");
        
        JLabel docxLabel = new JLabel("Save DOCX as:");
        docxPathField = new JTextField(30);
        docxBrowseButton = new JButton("Browse");
        
        convertButton = new JButton("Convert");
        
        // Layout components
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(pdfLabel, gbc);
        
        gbc.gridx = 1;
        add(pdfPathField, gbc);
        
        gbc.gridx = 2;
        add(pdfBrowseButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(docxLabel, gbc);
        
        gbc.gridx = 1;
        add(docxPathField, gbc);
        
        gbc.gridx = 2;
        add(docxBrowseButton, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(convertButton, gbc);
        
        // Add action listeners
        final JFrame frame = this;
        
        pdfBrowseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".pdf") || f.isDirectory();
                }
                public String getDescription() {
                    return "PDF Files (*.pdf)";
                }
            });
            
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                pdfPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        docxBrowseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".docx") || f.isDirectory();
                }
                public String getDescription() {
                    return "DOCX Files (*.docx)";
                }
            });
            
            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                if (!path.toLowerCase().endsWith(".docx")) {
                    path += ".docx";
                }
                docxPathField.setText(path);
            }
        });
        
        convertButton.addActionListener(e -> {
            try {
                String pdfPath = pdfPathField.getText();
                String docxPath = docxPathField.getText();
                
                if (pdfPath.isEmpty() || docxPath.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, 
                        "Please select both input PDF and output DOCX files.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                convertPDFtoDOCX(pdfPath, docxPath);
                
                JOptionPane.showMessageDialog(frame,
                    "Conversion completed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "Error during conversion: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void convertPDFtoDOCX(String pdfPath, String docxPath) throws Exception {
        // Load PDF document
        PDDocument pdfDocument = PDDocument.load(new File(pdfPath));
        
        // Create PDF text stripper
        PDFTextStripper pdfStripper = new PDFTextStripper();
        
        // Extract text from PDF
        String text = pdfStripper.getText(pdfDocument);
        
        // Create new DOCX document
        XWPFDocument docxDocument = new XWPFDocument();
        
        // Create paragraph
        XWPFParagraph paragraph = docxDocument.createParagraph();
        XWPFRun run = paragraph.createRun();
        
        // Add text to DOCX
        run.setText(text);
        
        // Save DOCX file
        try (FileOutputStream out = new FileOutputStream(docxPath)) {
            docxDocument.write(out);
        }
        
        // Close PDF document
        pdfDocument.close();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PDFtoDocxConverter().setVisible(true);
        });
    }
}
