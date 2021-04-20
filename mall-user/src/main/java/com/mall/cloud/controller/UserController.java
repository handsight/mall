package com.mall.cloud.controller;

import com.mall.cloud.config.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private TokenDecode tokenDecode;
    /**
     * Authorization Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwi5aKe5by6Ijoi5aKe5by6IGluZm8iLCJuYW1lIjoibWFjcm8iLCJpZCI6MSwiZXhwIjoyMDQ4NTczNTI5LCJhdXRob3JpdGllcyI6WyJnb29kc19saXN0IiwicHJvZHVjdF9saXN0Il0sImp0aSI6ImY0MDU0MGU1LTA0MzAtNDUwMy1hODMwLTAzYTM3YjA3NjBmYiIsImNsaWVudF9pZCI6IjEyMzQ1NiIsInVzZXJuYW1lIjoibWFjcm8ifQ.PoYUDzY5OUL0nnAKv91vcFu4TESJ2LAA3Oq3qy1LbfpMLTphGf9sLFbG5saqNfhbhhAkIFsD31aahv9pVar8qecID0nFJgodUNlFYMHQHYrGCYT8Rc_x7YCmFX5g-MRr2fXxqgb0Ouqw8T3LcjucfP6UKc1fbKhFv2OmSc4yvTqBRa0aOX78xYpfKVdKVbPKTbz6JLBLc8pQDjz4ALC2z_UXTlGSYPBjWS5Wk5mbg6b86KVdly_Y0pempjNKRXuStOHGyCoSpr45_p5VjAZXdiTp5AHXFJwNjFrpCkFJT7dT235So59XASFqXIDb-8NadNPGrLFqLtk0HI8QUwDTJg
     */
    @GetMapping("/getCurrentUser")
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
}
