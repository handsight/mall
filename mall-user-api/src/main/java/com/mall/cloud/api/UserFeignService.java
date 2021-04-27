package com.mall.cloud.api;

import com.mall.cloud.common.Result;
import com.mall.cloud.pojo.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-user",fallback = FeignServiceFallback.class)
public interface  UserFeignService {

    @GetMapping("/user/getUserByUsername/{username}")
    Result<UserDTO> getUserByUsername(@PathVariable("username") String username);


    @GetMapping("/user/testToken")
    Result testToken();

}
