package com.myapp.pdftodocx;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import net.sourceforge.tess4j.Tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class PDFConverter {
    private static final String DEFAULT_FONT = "Calibri";
    private final ArabicOCRProcessor arabicOCR;
    private static final Pattern ARABIC_PATTERN = Pattern.compile("[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF]+");
    private final Tesseract tesseract;

    // Kelas untuk menyimpan informasi teks
    private static class TextInfo {
        String text;
        double x;
        double y;
        float fontSize;
        String fontFamily;
        boolean isBold;
        boolean isItalic;

        TextInfo(String text, double x, double y, float fontSize, String fontFamily, 
                boolean isBold, boolean isItalic) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.fontSize = fontSize;
            this.fontFamily = fontFamily;
            this.isBold = isBold;
            this.isItalic = isItalic;
        }
    }

    // Custom TextStripper untuk mendapatkan informasi formatting
    private static class CustomTextStripper extends PDFTextStripper {
        private final List<TextInfo> textInfoList = new ArrayList<>();

        public CustomTextStripper() throws IOException {
            super();
        }

        public List<TextInfo> getTextInfo(PDDocument document, int pageIndex) throws IOException {
            textInfoList.clear();
            setStartPage(pageIndex + 1);
            setEndPage(pageIndex + 1);
            getText(document);
            return new ArrayList<>(textInfoList);
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) {
            if (textPositions.isEmpty()) return;

            TextPosition firstPosition = textPositions.get(0);
            String fontName = firstPosition.getFont().getName();
            float fontSize = firstPosition.getFontSize();

            textInfoList.add(new TextInfo(
                text,
                firstPosition.getXDirAdj(),
                firstPosition.getYDirAdj(),
                fontSize,
                fontName,
                fontName.toLowerCase().contains("bold"),
                fontName.toLowerCase().contains("italic") || fontName.toLowerCase().contains("oblique")
            ));
        }
    }

    public PDFConverter() {
        tesseract = new Tesseract();
        arabicOCR = new ArabicOCRProcessor();
        try {
            tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
            tesseract.setLanguage("eng+ind");
            tesseract.setPageSegMode(1);
            tesseract.setOcrEngineMode(1);
        } catch (Exception e) {
            logger.error("Error initializing Tesseract: ", e);
            throw new RuntimeException("Failed to initialize Tesseract", e);
        }
    }

    private boolean containsArabic(String text) {
        return ARABIC_PATTERN.matcher(text).find();
    }

    public void convert(String inputPath, String outputPath, String password) throws Exception {
        try (PDDocument document = loadPdfDocument(inputPath, password)) {
            XWPFDocument docx = new XWPFDocument();
            setupDocumentMargins(docx);
            
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            CustomTextStripper textStripper = new CustomTextStripper();
            
            processDocument(document, pdfRenderer, docx, textStripper);
            
            saveDocument(docx, outputPath);
        }
    }

    private PDDocument loadPdfDocument(String inputPath, String password) throws Exception {
        File inputFile = new File(inputPath);
        try {
            return password != null && !password.isEmpty() 
                ? PDDocument.load(inputFile, password)
                : PDDocument.load(inputFile);
        } catch (InvalidPasswordException e) {
            throw new Exception(password == null || password.isEmpty()
                ? "PDF ini dilindungi password. Mohon masukkan password."
                : "Password PDF salah. Silakan cek kembali password Anda.");
        }
    }

    private void setupDocumentMargins(XWPFDocument docx) {
        CTSectPr sectPr = docx.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        
        // Set margins 1 inch (1440 twips)
        long margin = 1440L;
        pageMar.setLeft(BigInteger.valueOf(margin));
        pageMar.setRight(BigInteger.valueOf(margin));
        pageMar.setTop(BigInteger.valueOf(margin));
        pageMar.setBottom(BigInteger.valueOf(margin));
    }

    private void processDocument(PDDocument document, PDFRenderer pdfRenderer, 
                               XWPFDocument docx, CustomTextStripper textStripper) {
        try {
            int pageCount = document.getNumberOfPages();
            for (int pageNum = 0; pageNum < pageCount; pageNum++) {
                processPage(document, pdfRenderer, docx, textStripper, pageNum, pageCount);
            }
        } catch (IOException e) {
            logger.error("Error processing document: ", e);
            throw new RuntimeException("Failed to process document", e);
        }
    }

    private void processPage(PDDocument document, PDFRenderer pdfRenderer, XWPFDocument docx,
                           CustomTextStripper textStripper, int pageNum, int pageCount) 
                           throws IOException {
        PDPage pdfPage = document.getPage(pageNum);
        PDRectangle pageSize = pdfPage.getMediaBox();
        BufferedImage image = pdfRenderer.renderImageWithDPI(pageNum, 300);
        
        List<TextInfo> textInfoList = textStripper.getTextInfo(document, pageNum);
        String ocrText = performOCR(image);
        
        processPageContent(docx, textInfoList, ocrText, pageSize);
        
        if (pageNum < pageCount - 1) {
            addPageBreak(docx);
        }
    }

    private String performOCR(BufferedImage image) {
        try {
            return tesseract.doOCR(image);
        } catch (Exception e) {
            logger.error("OCR failed: ", e);
            return "";
        }
    }

    private void processPageContent(XWPFDocument docx, List<TextInfo> textInfoList, 
                                  String ocrText, PDRectangle pageSize) {
        // Sort berdasarkan posisi Y dan X untuk menjaga urutan teks
        Collections.sort(textInfoList, (a, b) -> {
            int yCompare = Double.compare(a.y, b.y);
            return yCompare != 0 ? yCompare : Double.compare(a.x, b.x);
        });
        
        double currentY = -1;
        XWPFParagraph currentParagraph = null;
        boolean isList = false;
        
        for (TextInfo textInfo : textInfoList) {
            // Deteksi apakah ini adalah list item
            isList = detectListItem(textInfo.text);
            
            // Buat paragraf baru jika:
            // 1. Beda baris (y position berbeda)
            // 2. Atau ini adalah list item
            // 3. Atau ada jarak signifikan pada x position
            if (currentY == -1 || 
                Math.abs(textInfo.y - currentY) > 5 ||
                isList ||
                (currentParagraph != null && Math.abs(textInfo.x - getLastXPosition(currentParagraph)) > 50)) {
                
                currentParagraph = createNewParagraph(docx, textInfo, pageSize);
                currentY = textInfo.y;
                
                if (isList) {
                    formatAsList(currentParagraph, textInfo);
                }
            }
            
            if (currentParagraph != null) {
                addFormattedText(currentParagraph, textInfo);
            }
        }

        // Proses hasil OCR jika ada
        if (!ocrText.trim().isEmpty()) {
            processOCRText(docx, ocrText);
        }
    }

    private boolean detectListItem(String text) {
        return text.matches("^[\\d•\\-\\*].*") || // Bullet points atau nomor
               text.matches("^[a-zA-Z]\\).*") ||  // a) b) format
               text.matches("^[\\d]+\\..*");      // 1. 2. format
    }

    private void formatAsList(XWPFParagraph paragraph, TextInfo textInfo) {
        paragraph.setIndentationLeft(720); // 0.5 inch
        paragraph.setIndentationHanging(360); // 0.25 inch
        // Tambahkan bullet atau nomor sesuai dengan jenis list
        if (textInfo.text.matches("^\\d+\\..*")) {
            paragraph.setNumID(getNumberingId(paragraph.getDocument()));
        } else {
            paragraph.setIndentationLeft(720);
            paragraph.setIndentationHanging(360);
        }
    }

    private XWPFParagraph createNewParagraph(XWPFDocument docx, TextInfo textInfo, PDRectangle pageSize) {
        XWPFParagraph paragraph = docx.createParagraph();
        
        // Set alignment based on x position
        if (textInfo.x < pageSize.getWidth() * 0.25) {
            paragraph.setAlignment(ParagraphAlignment.LEFT);
        } else if (textInfo.x > pageSize.getWidth() * 0.75) {
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
        } else {
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        }
        
        return paragraph;
    }

    private void addFormattedText(XWPFParagraph paragraph, TextInfo textInfo) {
        XWPFRun run = paragraph.createRun();
        run.setText(textInfo.text);
        run.setFontFamily(DEFAULT_FONT);
        run.setFontSize((int)(textInfo.fontSize * FONT_SIZE_SCALE));
        run.setBold(textInfo.isBold);
        run.setItalic(textInfo.isItalic);
        
        // Jika text mengandung Arab, atur alignment ke kanan
        if (containsArabic(textInfo.text)) {
            paragraph.setAlignment(ParagraphAlignment.RIGHT);
            // Tambahkan RTL marking untuk text arab
            run.setText("\u200F" + textInfo.text);
        }
    }

    private void addOCRText(XWPFDocument docx, String ocrText) {
        XWPFParagraph paragraph = docx.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(ocrText);
    }

    private void addPageBreak(XWPFDocument docx) {
        XWPFParagraph paragraph = docx.createParagraph();
        paragraph.setPageBreak(true);
    }

    private void saveDocument(XWPFDocument docx, String outputPath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            docx.write(out);
        }
    }
    
    private void processOCRText(XWPFDocument docx, String ocrText) {
        String[] paragraphs = ocrText.split("\\n\\s*\\n");
        for (String para : paragraphs) {
            if (!para.trim().isEmpty()) {
                XWPFParagraph paragraph = docx.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(para.trim());
                run.setFontFamily(DEFAULT_FONT);
                
                if (containsArabic(para)) {
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);
                    run.setText("\u200F" + para.trim());
                }
            }
        }
    }
    
}