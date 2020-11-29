package com.github.ylfjm.config.web;

import com.github.ylfjm.common.YlfjmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 *
 * @author YLFJM
 * @date 2018/8/6
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 程序出错返回信息处理
     *
     * @param e Exception
     */
    @ExceptionHandler(Throwable.class)
    public Object throwableHandler(Throwable e) {
        log.error("------throwable------");
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("操作失败");
        log.error(e.getMessage(), e);
        return new WebMvcResult<>(50000, "操作失败【code=50002】", null);
    }

    /**
     * 程序出错返回信息处理
     *
     * @param e Exception
     */
    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(Exception e) {
        log.error("------Exception------");
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("操作失败");
        log.error(e.getMessage(), e);
        return new WebMvcResult<>(50000, "操作失败【code=50003】", null);
    }

    /**
     * 程序出错返回信息处理
     *
     * @param e YlfjmException
     */
    @ExceptionHandler(YlfjmException.class)
    public Object ylfjmExceptionHandler(YlfjmException e) {
        log.error("------YlfjmException------");
        log.error(e.getMessage(), e);
        return new WebMvcResult<>(50000, e.getMessage(), null);
    }

}
