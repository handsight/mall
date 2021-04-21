package com.mall.cloud.mapper;



import com.mall.cloud.pojo.dto.UserDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface UserMapper {

    /**
     * 查询用户信息
 	 * @param userName
     * @return
     */
    @Select(" select * from sys_user where username = #{userName}")
    UserDTO findByUsername(@Param("userName") String userName);

}
