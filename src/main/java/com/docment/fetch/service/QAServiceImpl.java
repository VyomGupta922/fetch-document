package com.docment.fetch.service;

import com.docment.fetch.dto.request.QuestionRequest;
import com.docment.fetch.dto.response.AnswerResponse;
import com.docment.fetch.entity.Document;
import com.docment.fetch.repo.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QAServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(QAServiceImpl.class);

    @Autowired
    private DocumentRepository documentRepository;

    public AnswerResponse processQuestion(QuestionRequest questionRequest) {
        String question = questionRequest.getQuestion();
        logger.info("Processing question: {}", question);

        List<Document> relevantDocs = documentRepository.findByContentContainingIgnoreCase(question);
        logger.info("Found {} relevant documents for question: {}", relevantDocs.size(), question);

        String answer = "Based on the documents, I found the following information:\n" +
                relevantDocs.stream()
                        .limit(3)
                        .map(doc -> "- " + doc.getTitle() + ": " +
                                doc.getContent().substring(0, Math.min(200, doc.getContent().length())) + "...")
                        .collect(Collectors.joining("\n"));

        List<Long> docIds = relevantDocs.stream()
                .map(Document::getId)
                .collect(Collectors.toList());

        logger.debug("Returning document IDs in response: {}", docIds);
        return new AnswerResponse(answer, docIds);
    }
}
