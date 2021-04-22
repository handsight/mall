package com.mall.cloud.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局过滤器 :用于鉴权(获取令牌 解析 判断)
 *
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1.获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //2.获取响应对象
        ServerHttpResponse response = exchange.getResponse();

        //3.判断 是否为登录的URL 如果是 放行
        if(UrlFilter.hasAutorize(request)){
            return chain.filter(exchange);
        }
        //4.判断 是否为登录的URL 如果不是      权限校验

        //4.1 从头header中获取令牌数据
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);

        if(StringUtils.isEmpty(token)){
            //4.4. 如果没有登录,要重定向到登录到页面
            return chain.filter(exchange);
        }

        //5 把原本的解析令牌 放到各个微服务 mall-security包下的公钥解析
//        try {
//            //Claims claims = JwtUtil.parseJWT(token);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //解析失败
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return response.setComplete();
//        }

        //添加头信息 传递给 各个微服务()
        request.mutate().header(AUTHORIZE_TOKEN,"Bearer "+ token);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
