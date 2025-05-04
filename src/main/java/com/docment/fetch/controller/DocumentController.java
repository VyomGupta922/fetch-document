package com.docment.fetch.controller;

import com.docment.fetch.entity.Document;
import com.docment.fetch.service.DocumentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Document>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(documentService.searchByKeyword(keyword));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Document>> filterDocuments(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type) {

        return ResponseEntity.ok(documentService.filterByMetadata(author, type));
    }
}
