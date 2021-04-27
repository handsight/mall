package com.mall.cloud.api;


import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.pojo.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class FeignServiceFallback implements UserFeignService{
    @Override
    public Result<UserDTO> getUserByUsername(String username) {
        return new Result(false, StatusCode.ERROR,"用户服务不可用");
    }

    @Override
    public Result testToken() {
        return null;
    }
}
