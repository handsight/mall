package com.mall.cloud.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 描述  所有头文件数据再次加入到Feign请求的微服务头文件中
 *
 */
@Component
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            //1.获取请求对象
            HttpServletRequest request = requestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                //2.获取请求对象中的所有的头信息(网关传递过来的)
                while (headerNames.hasMoreElements()) {
                    //头的名称  头名称对应的值
                    String name = headerNames.nextElement();
                    String value = request.getHeader(name);
                    System.out.println("name:" + name + "::::::::value:" + value);
                    //3.将头信息传递给fegin (restTemplate)
                    requestTemplate.header(name,value);
                }
            }
        }


    }
}
