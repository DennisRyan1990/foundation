package com.zchi.common.web.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Created by zchi on 16/5/11.
 */
@ControllerAdvice
public class CommonResponseBodyAdvice implements ResponseBodyAdvice{
    @Autowired
    private ResponseHolder responseHolder;
    @Override public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override public Object beforeBodyWrite(Object body, MethodParameter returnType,
        MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {
        responseHolder.set(body);
        return body;
    }
}
