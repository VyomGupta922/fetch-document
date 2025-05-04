package com.docment.fetch.controller;

import com.docment.fetch.entity.Document;
import com.docment.fetch.service.DocumentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    @Autowired
    private DocumentServiceImpl documentService;

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("type") String type,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(documentService.uploadDocument(title, author, type, file));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Document>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending());

        return ResponseEntity.ok(documentService.searchByKeyword(keyword, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Document>> filterDocuments(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(documentService.filterByMetadata(author, type, pageable));
    }
}
