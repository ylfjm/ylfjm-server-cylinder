package com.github.ylfjm.config.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 描述：全局返回值统一封装
 *
 * @author YLFJM
 * @Date 2020/10/21
 */
@Configuration
@Slf4j
public class GlobalReturnConfig {

    @RestControllerAdvice
    static class ResultResponseAdvice implements ResponseBodyAdvice<Object> {

        private static ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
            if (Objects.equals("error", methodParameter.getExecutable().getName())) {
                return false;
            }
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
            if (body instanceof WebMvcResult) {
                log.info("------body instanceof WebMvcResult is true------");
                return body;
            }
            log.info("------body instanceof WebMvcResult is false------");
            WebMvcResult<Object> result = new WebMvcResult<>(20000, "请求成功", body);
            if (body instanceof String) {
                //如果接口返回类型是String类型这里要做转换，或者重写StringHttpMessageConverter，
                //因为StringHttpMessageConverter的泛型指定为String类型，返回WebMvcResult类型会报强制类型转换错误异常
                try {
                    return objectMapper.writeValueAsString(result);
                } catch (JsonProcessingException ex) {
                }
            }
            return result;
        }
    }

}
