package com.docment.fetch.dto.response;

import java.util.List;

public class AnswerResponse {
    private String answer;
    private List<Long> documentIds;

    public AnswerResponse(String answer, List<Long> documentIds) {
        this.answer = answer;
        this.documentIds = documentIds;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Long> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<Long> documentIds) {
        this.documentIds = documentIds;
    }
}
