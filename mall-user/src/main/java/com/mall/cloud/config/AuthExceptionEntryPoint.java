package com.mall.cloud.config;

import com.alibaba.fastjson.JSONObject;
import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 无效 token 异常类重写
 */
@Component
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {

        String message = authException.getMessage();
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        Result result = new Result(false, StatusCode.ERROR, message);
        try {
            response.getWriter().write(JSONObject.toJSONString(result));
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
}
