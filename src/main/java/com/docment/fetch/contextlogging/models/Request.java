package com.docment.fetch.contextlogging.models;

import jakarta.servlet.http.Cookie;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private Context context;
    private String httpMethod;
    private String url;
    private Map<String, String> headers;
    private Map<String, String[]> parameters;
    private Map<String, Cookie> cookies;



    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String[]> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String[]> parameters) {
        this.parameters = parameters;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Context getContext() {
        context.setDate(new Date());
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookiesArray) {
        if(cookiesArray != null) {
            this.cookies = new HashMap<>();
            for (Cookie cookie : cookiesArray)
                cookies.put(cookie.getName(), cookie);
        } else{
            this.cookies = null;
        }
    }
}