package com.mall.cloud.mapper;



import com.mall.cloud.dto.UserJwt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface UserMapper {

    /**
     * 查询用户信息
 	 * @param userName
     * @return
     */
    @Select(" select * from sys_user where username = #{userName}")
    UserJwt findByUsername(@Param("userName") String userName);

}
