package com.docment.fetch.service;

import com.docment.fetch.entity.Document;
import com.docment.fetch.repo.DocumentRepository;
import jakarta.persistence.criteria.Predicate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Autowired
    private DocumentRepository repository;
    private final Tika tika = new Tika();

    public Document uploadDocument(String title, String author, String type, MultipartFile file) {
        logger.info("Starting upload for document: title={}, author={}, type={}", title, author, type);

        if (file == null || file.isEmpty()) {
            logger.warn("Upload failed: file is null or empty");
            throw new IllegalArgumentException("File cannot be empty");
        }

        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            logger.warn("Upload failed: unsupported content type '{}'", contentType);
            throw new IllegalArgumentException("Only PDF files are supported. Received: " + contentType);
        }

        String content;
        byte[] fileBytes;

        try {
            fileBytes = file.getBytes();
            if (fileBytes.length == 0) {
                logger.warn("Upload failed: file content is empty");
                throw new IllegalArgumentException("Uploaded file is empty");
            }

            content = tryAllExtractionMethods(fileBytes);

            if (content == null || content.trim().isEmpty()) {
                logger.error("No text could be extracted from the uploaded PDF.");
                throw new RuntimeException("Could not extract any text from PDF. It might be image-based or encrypted.");
            }

        } catch (Exception e) {
            logger.error("Failed to process uploaded PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process PDF file: " + e.getMessage(), e);
        }

        Document savedDoc = repository.save(Document.builder()
                .title(title)
                .author(author)
                .type(type)
                .uploadDate(LocalDateTime.now())
                .content(content)
                .build());

        logger.info("Document uploaded and saved successfully: id={}", savedDoc.getId());
        return savedDoc;
    }

    private String tryAllExtractionMethods(byte[] pdfBytes) throws IOException {
        String content = null;

        // 1. Tika
        try {
            logger.debug("Trying Tika for text extraction");
            content = tika.parseToString(new ByteArrayInputStream(pdfBytes));
            if (!isContentValid(content)) {
                throw new Exception("Tika returned invalid content");
            }
            logger.debug("Tika extraction successful");
            return content;
        } catch (Exception e) {
            logger.warn("Tika extraction failed: {}", e.getMessage());
        }

        // 2. PDFBox
        try {
            logger.debug("Trying PDFBox for text extraction");
            content = extractTextWithPDFBox(pdfBytes);
            if (!isContentValid(content)) {
                throw new Exception("PDFBox returned invalid content");
            }
            logger.debug("PDFBox extraction successful");
            return content;
        } catch (Exception e) {
            logger.warn("PDFBox extraction failed: {}", e.getMessage());
        }

        // 3. Check encryption
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            if (document.isEncrypted()) {
                logger.error("PDF is encrypted. Cannot extract text.");
                throw new RuntimeException("PDF is encrypted - text extraction not supported");
            }
        }

        logger.warn("All extraction methods failed. Returning empty string.");
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
        return !trimmed.isEmpty() && trimmed.length() > 20;
    }

    public Page<Document> searchByKeyword(String keyword, Pageable pageable) {
        logger.info("Searching documents by keyword: '{}'", keyword);
        return repository.findByContentContainingIgnoreCase(keyword, pageable);
    }

    public Page<Document> filterByMetadata(String author, String type, Pageable pageable) {
        logger.info("Filtering documents by author='{}', type='{}'", author, type);
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (author != null) {
                predicates.add(cb.equal(cb.lower(root.get("author")), author.toLowerCase()));
            }
            if (type != null) {
                predicates.add(cb.equal(cb.lower(root.get("type")), type.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
