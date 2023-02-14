package com.example.meituan.exception;

import com.example.meituan.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * @author wangminan
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String ERROR_FROM_REQUEST = "Error {} from Request:{}";

    /**
     * 请求方式不支持
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R handleMethodNotAllowed(Exception e, HttpServletRequest request) {
        log.error(ERROR_FROM_REQUEST, e, request.getRequestURI());
        return R.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "请求方式不支持");
    }


    /**
     * 请求格式不对
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ServletRequestBindingException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public R handleBadRequest(Exception e, HttpServletRequest request) {
        log.error(ERROR_FROM_REQUEST, e, request.getRequestURI());
        return R.error(HttpStatus.BAD_REQUEST.value(), "请求格式错误");
    }

    /**
     * 请求URL有误，无法解析这个URL该对应Controller中哪个方法
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public R handleNotFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error(ERROR_FROM_REQUEST, e, request.getRequestURI());
        return R.error(HttpStatus.NOT_FOUND.value(), "请求URL有误");
    }

    /**
     * 业务异常，可细分为多种情况，可见ResultCodeEnum
     */
    @ExceptionHandler(SearchException.class)
    public R handleBusinessException(SearchException e, HttpServletRequest request) {
        log.error(ERROR_FROM_REQUEST, e, request.getRequestURI());
        return R.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }

    /**
     * 如果前面的处理器都没拦截住，最后兜底
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public R handleException(Exception e, HttpServletRequest request) {
        log.error(ERROR_FROM_REQUEST, e, request.getRequestURI());
        return R.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
    }
}
