package com.mall.cloud.controller;


import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.dto.AuthToken;
import com.mall.cloud.exception.ServiceException;
import com.mall.cloud.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@Api(tags = "认证接口")
public class AuthController {

    //客户端ID
    @Value("${auth.clientId}")
    private String clientId;

    //秘钥
    @Value("${auth.clientSecret}")
    private String clientSecret;


    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    @ApiOperation(value = "密码模式-创建token")
    public Result login(String username, String password) {
        if(StringUtils.isEmpty(username)){
            throw new ServiceException("用户名不允许为空");
        }
        if(StringUtils.isEmpty(password)){
            throw new ServiceException("密码不允许为空");
        }
        //申请令牌
        AuthToken authToken =  authService.login(username,password,clientId,clientSecret);

        return new Result(true, StatusCode.OK,"登录成功！",authToken);
    }


    @GetMapping("/refresh")
    @ApiOperation(value = "刷新token")
    public Result refresh(String refreshToken) {

        //申请令牌
        AuthToken authToken =  authService.refresh(refreshToken,clientId,clientSecret);
        return new Result(true, StatusCode.OK,"刷新成功！",authToken);
    }

}
