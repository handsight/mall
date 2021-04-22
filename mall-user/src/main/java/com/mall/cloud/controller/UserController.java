package com.mall.cloud.controller;

import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.config.TokenDecode;
import com.mall.cloud.mapper.UserMapper;
import com.mall.cloud.pojo.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(tags = "用户接口")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenDecode tokenDecode;
    /**
     * Authorization Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwi5aKe5by6Ijoi5aKe5by6IGluZm8iLCJuYW1lIjoibWFjcm8iLCJpZCI6MSwiZXhwIjoyMDQ4NTczNTI5LCJhdXRob3JpdGllcyI6WyJnb29kc19saXN0IiwicHJvZHVjdF9saXN0Il0sImp0aSI6ImY0MDU0MGU1LTA0MzAtNDUwMy1hODMwLTAzYTM3YjA3NjBmYiIsImNsaWVudF9pZCI6IjEyMzQ1NiIsInVzZXJuYW1lIjoibWFjcm8ifQ.PoYUDzY5OUL0nnAKv91vcFu4TESJ2LAA3Oq3qy1LbfpMLTphGf9sLFbG5saqNfhbhhAkIFsD31aahv9pVar8qecID0nFJgodUNlFYMHQHYrGCYT8Rc_x7YCmFX5g-MRr2fXxqgb0Ouqw8T3LcjucfP6UKc1fbKhFv2OmSc4yvTqBRa0aOX78xYpfKVdKVbPKTbz6JLBLc8pQDjz4ALC2z_UXTlGSYPBjWS5Wk5mbg6b86KVdly_Y0pempjNKRXuStOHGyCoSpr45_p5VjAZXdiTp5AHXFJwNjFrpCkFJT7dT235So59XASFqXIDb-8NadNPGrLFqLtk0HI8QUwDTJg
     */
    @GetMapping("/getCurrentUser")
    @ApiOperation(value = "携带token 访问受保护的资源")
    public void getCurrentUser(Authentication authentication, HttpServletRequest request) {
        System.out.println("携带token 访问受保护的资源");

        String token = tokenDecode.getToken();
        System.out.println(token);
        tokenDecode.getUserInfo();
        Map<String, ?> map = tokenDecode.getUserInfo();
        Integer id = (Integer)map.get("id");
        String username = (String) map.get("username");
        System.out.println(id+username);
    }

    @GetMapping("/list")
    @ApiOperation(value = "携带token 访问受保护的资源 必须拥有user_list的权限才能访问")
    @PreAuthorize(value = "hasAuthority('user_list')")
    public void list( HttpServletRequest request) {
        System.out.println("头信息为:" + request.getHeader("Authorization"));
        System.out.println("携带token 访问受保护的资源 必须拥有user_list的权限才能访问");
    }


    @GetMapping("/testToken")
    @ApiOperation(value = "测试微服务之间调用携带token")
    public Result testToken( HttpServletRequest request) {
        System.out.println("头信息为:" + request.getHeader("Authorization"));
        return new Result(true, StatusCode.OK,null,null);
    }


    @GetMapping("/getUserByUsername/{username}")
    @ApiOperation(value = "根据用户名查询用户")
    public Result<UserDTO> getCurrentUser(@PathVariable("username") String username) {
        UserDTO userDTO=userMapper.findByUsername(username);
        return new Result(true, StatusCode.OK,null,userDTO);
    }


}
