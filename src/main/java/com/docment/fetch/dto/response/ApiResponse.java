package com.docment.fetch.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private int status;
    private String message;
    private List<String> errors;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date timestamp;
    private String path;
    private T body;
    private PaginationDetails paginationDetails;

    // Default constructor
    public ApiResponse() {
        this.timestamp = new Date();
    }

    // Parameterized constructor with JsonCreator and JsonProperty annotations
    @JsonCreator
    public ApiResponse(
            @JsonProperty("status") int status,
            @JsonProperty("message") String message,
            @JsonProperty("errors") List<String> errors,
            @JsonProperty("path") String path,
            @JsonProperty("body") T body,
            @JsonProperty("paginationDetails") PaginationDetails paginationDetails) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = new Date();
        this.path = path;
        this.body = body;
        this.paginationDetails = paginationDetails;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public PaginationDetails getPaginationDetails() {
        return paginationDetails;
    }

    public void setPaginationDetails(PaginationDetails paginationDetails) {
        this.paginationDetails = paginationDetails;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", body=" + body +
                ", paginationDetails=" + paginationDetails +
                '}';
    }
}