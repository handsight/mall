package com.mall.cloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.mall.cloud.api.UserFeignService;
import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.exception.ServiceException;
import com.mall.cloud.handler.CustomerBlockHandler;
import com.mall.cloud.pojo.dto.UserDTO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 参考
 * https://juejin.cn/post/6850418120327184391
 * https://github.com/RingoTangs/LearningNote/blob/master/SpringCloudAlibaba/SpringCloudAlibaba.md
 *
 *
 */
@RestController
@RequestMapping(value = "/sentinel")
@Api(tags = "限流接口")
@Slf4j
public class SentinelController {

    @Autowired
    private UserFeignService userFeignService;
    /**
     * 用户自定义限流处理逻辑
     * 限流的时候会去全局兜底的类找指定的方法
     */
    @GetMapping("/rateLimit/{id}")
    @SentinelResource(value = "customerBlockHandler",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "handlerException")
    public Result customerBlockHandler(@PathVariable("id") String id) {
        String username="macro";
        Result<UserDTO> result = userFeignService.getUserByUsername(username);
        if(result.getCode()!=200){
            throw new ServiceException(result.getMessage());
        }
        return new Result(true, StatusCode.OK,"正常访问");
    }

}
