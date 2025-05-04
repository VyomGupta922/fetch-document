package com.docment.fetch.service;

import com.docment.fetch.entity.Document;
import com.docment.fetch.repo.DocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl {

    @Autowired
    private DocumentRepository repository;
    private final Tika tika = new Tika();
    public Document uploadDocument(String title, String author, String type, MultipartFile file) {
        // Validate input
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Verify PDF content type
        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new IllegalArgumentException("Only PDF files are supported. Received: " + contentType);
        }

        String content;
        byte[] fileBytes;

        try {
            // Read file content
            fileBytes = file.getBytes();
            if (fileBytes.length == 0) {
                throw new IllegalArgumentException("Uploaded file is empty");
            }

            // Try extraction with increasing sophistication
            content = tryAllExtractionMethods(fileBytes);

            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("Could not extract any text from PDF. It might be image-based or encrypted.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process PDF file: " + e.getMessage(), e);
        }

        return repository.save(Document.builder()
                .title(title)
                .author(author)
                .type(type)
                .uploadDate(LocalDateTime.now())
                .content(content)
                .build());
    }

    private String tryAllExtractionMethods(byte[] pdfBytes) throws IOException {
        String content = null;

        // 1. First try with Tika
        try {
            content = tika.parseToString(new ByteArrayInputStream(pdfBytes));
            if (!isContentValid(content)) {
                throw new Exception("Tika returned invalid content");
            }
            return content;
        } catch (Exception e) {
            System.out.println("Tika extraction failed: " + e.getMessage());
        }

        // 2. Try with PDFBox
        try {
            content = extractTextWithPDFBox(pdfBytes);
            if (!isContentValid(content)) {
                throw new Exception("PDFBox returned invalid content");
            }
            return content;
        } catch (Exception e) {
            System.out.println("PDFBox extraction failed: " + e.getMessage());
        }

        // 3. Check if PDF is encrypted
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            if (document.isEncrypted()) {
                throw new RuntimeException("PDF is encrypted - text extraction not supported");
            }
        }

        // 4. As last resort, return empty string
        return "";
    }

    private String extractTextWithPDFBox(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private boolean isContentValid(String content) {
        if (content == null) return false;
        String trimmed = content.trim();
        return !trimmed.isEmpty() && trimmed.length() > 20; // At least 20 non-whitespace chars
    }

    public List<Document> searchByKeyword(String keyword) {
        return repository.findByContentContainingIgnoreCase(keyword);
    }


    public List<Document> filterByMetadata(String author, String type) {
        return repository.findAll().stream()
                .filter(d -> (author == null || d.getAuthor().equalsIgnoreCase(author)) &&
                             (type == null || d.getType().equalsIgnoreCase(type)))
                .toList();
    }
}
