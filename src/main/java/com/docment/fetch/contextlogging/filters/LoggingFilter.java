package com.docment.fetch.contextlogging.filters;

import com.docment.fetch.contextlogging.models.Context;
import com.docment.fetch.contextlogging.models.Request;
import com.docment.fetch.contextlogging.models.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String CONTEXT_ID = "context_id";


    @Value("${log.request:true}")
    private boolean logRequest;
    @Value("${log.request.headers:true}")
    private boolean logRequestHeaders;
    @Value("${log.request.parameters:true}")
    private boolean logRequestParameters;
    @Value("${log.request.cookies:true}")
    private boolean logRequestCookies;

    @Value("${log.response:true}")
    private boolean logResponse;
    @Value("${log.response.body:false}")
    private boolean logResponseBody;
    @Value("${log.response.headers:true}")
    private boolean logResponseHeaders;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        MDC.clear();
        if (servletRequest instanceof HttpServletRequest
                && servletResponse instanceof HttpServletResponse && (logRequest || logResponse)) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            ContentCachingRequestWrapper requestToCache = new ContentCachingRequestWrapper(httpServletRequest);
            ContentCachingResponseWrapper responseToCache = new ContentCachingResponseWrapper(httpServletResponse);
            Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS").create();
            Request request = null;
            MDC.put("demoId", UUID.randomUUID().toString());
            request = getRequestObject(httpServletRequest);
            if(logRequest) {
                logger.info("Request:" + gson.toJson(request));
            }
            chain.doFilter(requestToCache, responseToCache);
            if(logResponse) {
                logger.info("Response:" + gson.toJson(getResponseObject(responseToCache, request)));
            } else{
                responseToCache.copyBodyToResponse();
            }
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    private Response getResponseObject(ContentCachingResponseWrapper contentCachingResponseWrapper, Request request) throws IOException {
        Response response = new Response();
        response.setContext(request.getContext());
        response.setStatusCode(contentCachingResponseWrapper.getStatus());
        if(logResponseHeaders)
            response.setHeaders(contentCachingResponseWrapper.getHeaderNames(), contentCachingResponseWrapper);
        String payload = null;
        if(logResponseBody) {
            ContentCachingResponseWrapper wrapper =
                    WebUtils.getNativeResponse(contentCachingResponseWrapper, ContentCachingResponseWrapper.class);
            if (wrapper != null) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                    wrapper.copyBodyToResponse();
                }
            }
            response.setBody(payload);
        } else{
            contentCachingResponseWrapper.copyBodyToResponse();
        }

        return response;
    }

    private Request getRequestObject(HttpServletRequest httpServletRequest) {
        Request request = new Request();
        if(logRequestHeaders)
            request.setHeaders(getHeadersInfo(httpServletRequest));
        request.setHttpMethod(httpServletRequest.getMethod());
        request.setUrl(httpServletRequest.getRequestURL().toString());
        if(logRequestParameters)
            request.setParameters(httpServletRequest.getParameterMap());
        request.setCookies(httpServletRequest.getCookies());

        Context context = new Context();
        Cookie contextCookie = request.getCookies() != null ? request.getCookies().get(CONTEXT_ID) : null;
        if(!logRequestCookies)
            request.setCookies(null);
        String contextId = MDC.get("demoId");
        context.setContextId(contextId);
        context.setLocale(httpServletRequest.getLocale() != null ? httpServletRequest.getLocale().getLanguage(): "");
        context.setSourceIp(httpServletRequest.getRemoteAddr());
        request.setContext(context);
        return request;
    }
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }


    @Override
    public void destroy() {
        System.out.println("");
    }
}