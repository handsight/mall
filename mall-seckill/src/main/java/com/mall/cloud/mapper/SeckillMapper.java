package com.mall.cloud.mapper;

import com.mall.cloud.pojo.SeckillEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SeckillMapper {


    @Select("SELECT seckillId,name as name,inventory as inventory, startTime, endTime, createTime,version as version from m_seckill_stock where seckillId=#{seckillId}")
    SeckillEntity findBySeckillId(@Param("seckillId") Long seckillId);


    @Update("update m_seckill_stock set inventory=inventory-1, version=version+1 where  seckillId=#{seckillId} and inventory>0  and version=#{version}")
    int inventoryDeduction(@Param("seckillId") Long seckillId, @Param("version") Long version);

}
