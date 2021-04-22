package com.mall.cloud.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.filter *
 * @since 1.0
 */
public class UrlFilter {

    private static  final String nointerceterurl="/api/mall-auth/auth/login,/api/mall-auth/auth/refresh";
    /**
     * 用来判断 如果 当前的请求 在 放行的请求中存在,(不需要拦截 :true,否则需要拦截:false)
     * @return
     */
    public static boolean hasAutorize(ServerHttpRequest request){
        String path = request.getURI().getPath();

        String[] split = nointerceterurl.split(",");

        for (String s : split) {
            if(s.equals(path)){
                //不需要拦截
                return true;
            }
        }

        //要拦截
        return false;
    }
}
