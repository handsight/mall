package com.mall.cloud.dto;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
public class UserJwt implements UserDetails {


    private Integer id;
    private String username;
    private String realname;
    private String password;
    private Date createDate;
    private Date lastLoginTime;
    /**
     * 用户已被禁用
     */
    private boolean enabled=true;

    /**
     * 账号是否过期
     *
     */
    private boolean accountNonExpired=true;
    /**
     * 账号已被锁定
     */
    private boolean accountNonLocked=true;
    /**
     * 凭证已过期
     */
    private boolean credentialsNonExpired=true;
    /**
     *  用户所有权限
     */
    private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
