package com.mall.cloud.mapper;

import com.mall.cloud.pojo.OrderEntity;
import org.apache.ibatis.annotations.Insert;

public interface OrderMapper {

    @Insert("INSERT INTO `m_order` VALUES (#{seckillId},#{userId},#{state}, now());")
    int insertOrder(OrderEntity orderEntity);

}
