package com.mall.cloud.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Mapper
@Component
public interface Order2Mapper {

    /**
     * 插入订单
     * @param price
     * @param userId
     * @param status
     * @return
     */
    @Insert("insert into t_order(price,user_id,status,create_time)values(#{price},#{userId},#{status},#{createTime})")
    int insertOrder(@Param("price") BigDecimal price, @Param("userId")Long userId, @Param("status")String status,@Param("createTime") LocalDateTime createTime);

}
