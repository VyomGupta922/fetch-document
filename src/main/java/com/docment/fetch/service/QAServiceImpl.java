package com.docment.fetch.service;


import com.docment.fetch.dto.request.QuestionRequest;
import com.docment.fetch.dto.response.AnswerResponse;
import com.docment.fetch.entity.Document;
import com.docment.fetch.repo.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QAServiceImpl  {

    @Autowired
    private DocumentRepository documentRepository;

    public AnswerResponse processQuestion(QuestionRequest questionRequest) {
        List<Document> relevantDocs = documentRepository
            .findByContentContainingIgnoreCase(questionRequest.getQuestion());

        String answer = "Based on the documents, I found the following information:\n" +
            relevantDocs.stream()
                .limit(3)
                .map(doc -> "- " + doc.getTitle() + ": " + 
                    doc.getContent().substring(0, Math.min(200, doc.getContent().length())) + "...")
                .collect(Collectors.joining("\n"));

        return new AnswerResponse(answer, relevantDocs.stream()
            .map(Document::getId)
            .collect(Collectors.toList()));
    }


}