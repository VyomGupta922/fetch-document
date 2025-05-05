package com.docment.fetch.controller;

import com.docment.fetch.dto.request.QuestionRequest;
import com.docment.fetch.dto.response.AnswerResponse;
import com.docment.fetch.service.QAServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qa")
public class QAController {

    @Autowired
    private QAServiceImpl qaService;

    @PostMapping("/ask")
    @PreAuthorize("hasRole('VIEWER') or hasRole('EDITOR') or hasRole('ADMIN')")
    public ResponseEntity<?> askQuestion(
            @RequestBody QuestionRequest questionRequest) {

        AnswerResponse response = qaService.processQuestion(questionRequest);
        return ResponseEntity.ok(response);
    }
}