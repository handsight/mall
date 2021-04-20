package com.mall.cloud.service;


import com.mall.cloud.dto.AuthToken;


public interface AuthService {

    /***
     * 授权认证方法
     */
    AuthToken login(String username, String password, String clientId, String clientSecret);

    AuthToken refresh(String refreshToken, String clientId, String clientSecret);
}
