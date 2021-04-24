package com.mall.cloud.listener;

import com.alibaba.fastjson.JSONObject;
import com.mall.cloud.mapper.OrderMapper;
import com.mall.cloud.mapper.SeckillMapper;
import com.mall.cloud.pojo.OrderEntity;
import com.mall.cloud.pojo.SeckillEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * @author Administrator
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "seckill_queue", consumerGroup = "mall-seckill_queue")
public class ProductConsumer implements RocketMQListener<String> {


    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        Long seckillId = jsonObject.getLong("seckillId");
        Long userId = jsonObject.getLong("userId");
        Integer num = jsonObject.getInteger("num");

        //1.判断是否已经秒杀到了
        Object obj = stringRedisTemplate.opsForHash().get("seckill_success"+seckillId, userId.toString());
        if(obj != null) {
            //加回去
            stringRedisTemplate.opsForHash().increment("seckill_stock", seckillId.toString(), num);
            log.info("不能重复秒杀");
            return;
        }

        // 2.获取秒杀id
        SeckillEntity seckillEntity = seckillMapper.findBySeckillId(seckillId);
        if (seckillEntity == null) {
            log.info("seckillId:{},商品信息不存在!", seckillId);
            return;
        }
        if(seckillEntity.getInventory() <= 0) {
            return;
        }

        Long version = seckillEntity.getVersion();
        //基于乐观锁防止超卖   悲观锁方式只适用于秒杀一个商品 update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0
        int inventoryDeduction = seckillMapper.inventoryDeduction(seckillId,num, version);
        if (!toDaoResult(inventoryDeduction)) {
            log.info(">>>seckillId:{}修改库存失败>>>>inventoryDeduction返回为{} 秒杀失败！", seckillId, inventoryDeduction);
            //余额不足的话、将本次购买量加回到库存里
            stringRedisTemplate.opsForHash().increment("seckill_stock", seckillId.toString(), num);
            return;
        }
        // 3.添加秒杀订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setSeckillId(seckillId);
        orderEntity.setState(1L);
        int insertOrder = orderMapper.insertOrder(orderEntity);
        if (!toDaoResult(insertOrder)) {
            return;
        }
        stringRedisTemplate.opsForHash().put("seckill_success"+seckillId, userId.toString(), LocalDateTime.now().toString());
        log.info(">>>修改库存成功seckillId:{}>>>>inventoryDeduction返回为{} 秒杀成功", seckillId, inventoryDeduction);
    }

    // 调用数据库层判断
    public Boolean toDaoResult(int result) {
        return result > 0 ? true : false;
    }
}
