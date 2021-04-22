package com.mall.cloud.config;


import com.mall.cloud.api.UserFeignService;
import com.mall.cloud.common.Result;
import com.mall.cloud.dto.UserJwt;
import com.mall.cloud.pojo.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
public class MyUserDetailsService implements UserDetailsService {


    @Autowired
    private UserFeignService userFeignService;

    @Autowired
    private ClientDetailsService clientDetailsService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if (authentication == null) {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if (clientDetails != null) {
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //数据库查找方式 oauth_client_details
                return new User(username, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }

        if (StringUtils.isEmpty(username)) {
            return null;
        }
//        UserJwt userJwt = userMapper.findByUsername(username);
//        if(userJwt==null){
//            return null;
//        }

        Result<UserDTO> result = userFeignService.getUserByUsername(username);
        if(result.getData()==null){
            return null;
        }
        UserDTO userDTO = result.getData();
        //对密码进行编码
        String pwd = new BCryptPasswordEncoder().encode(userDTO.getPassword());

        UserJwt userJwt=new UserJwt();
        userJwt.setPassword(pwd);
        // 给用户设置权限  从数据库查询 暂时写死
        String permissions = "user_add,user_update";
        List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(permissions);
        userJwt.setAuthorities(authorityList);
        return userJwt;
    }

}
