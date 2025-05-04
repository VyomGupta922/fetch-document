package com.docment.fetch.contextlogging.models;


import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private Context context;
    private Map<String, String> headers;

    private Request request;

    private int statusCode;
    private String body;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Collection<String> headerNames, HttpServletResponse httpServletResponse) {
        this.headers = new HashMap<>();
        for(String headerName : headerNames){
            this.headers.put(headerName, httpServletResponse.getHeader(headerName));
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "Response{" +
                "context=" + context +
                ", headers=" + headers +
                ", request=" + request +
                ", statusCode=" + statusCode +
                ", body='" + body + '\'' +
                '}';
    }
}
