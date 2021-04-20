package com.mall.cloud.service.impl;


import com.mall.cloud.dto.AuthToken;
import com.mall.cloud.exception.ServiceException;
import com.mall.cloud.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;


    @Autowired
    private RestTemplate restTemplate;

    /***
     * 授权认证方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //申请令牌
        AuthToken authToken = applyToken(username,password,clientId, clientSecret);
        if(authToken == null){
            throw new ServiceException("申请令牌失败");
        }
        return authToken;
    }


    /**
     * 刷新token
     * @param refreshToken
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public AuthToken refresh(String refreshToken, String clientId, String clientSecret) {
        AuthToken refreshToken1 = getAuthToken(clientId, clientSecret, "refresh_token", false,refreshToken);

        return refreshToken1;
    }


    /****
     * 认证方法
     * @param username:用户登录名字
     * @param password：用户密码
     * @param clientId：配置文件中的客户端ID
     * @param clientSecret：配置文件中的秘钥
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        AuthToken authToken = getAuthToken(clientId, clientSecret,"password",true,null);
        return authToken;
    }

    private AuthToken getAuthToken(String clientId, String clientSecret,String grantType,Boolean flag,String refreshToken) {
        //选中认证服务的地址
//        ServiceInstance serviceInstance = this.loadBalancerClient.choose("oauth2-server");
//        if (serviceInstance == null) {
//            throw new ServiceException("找不到对应的服务");
//        }
//        获取令牌的url
//        String path = serviceInstance.getInstanceId() + "/oauth/token";
         String path ="http://mall-oauth2-server/oauth/token";
        //定义body
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        //授权方式
        formData.add("grant_type", grantType);

        if(flag){
            //账号,密码
            formData.add("username", "macro");
            formData.add("password", "123456");
            formData.add("scop", "app");
        }else {
            formData.add("refresh_token", refreshToken);
        }

        //定义头
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", httpbasic(clientId, clientSecret));
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        Map map = null;
        try {
            //http请求spring security的申请令牌接口
            ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<>(formData, header), Map.class);
            //获取响应数据
            map = mapResponseEntity.getBody();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        if(map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null) {
            //jti是jwt令牌的唯一标识作为用户身份令牌
            if(map==null){
                throw new ServiceException("创建令牌失败！");
            }else {
                Object description = map.get("error_description");
                throw new ServiceException(description.toString());
            }
        }

        //将响应数据封装成AuthToken对象
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String accessToken = (String) map.get("access_token");
        //jti，作为用户的身份标识
        String jwtToken= (String) map.get("jti");
        authToken.setJti(jwtToken);
        authToken.setAccessToken(accessToken);
        authToken.setRefreshToken((String) map.get("refresh_token"));
        return authToken;
    }


    /***
     * base64编码
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }
}
