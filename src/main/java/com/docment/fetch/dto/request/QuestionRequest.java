package com.docment.fetch.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

public class QuestionRequest {

    @NotBlank
    private String question;
    private String context;
    private Boolean exactMatch;
    private Integer maxResults = 5;

}