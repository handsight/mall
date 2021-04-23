package com.mall.cloud.controller;

import com.alibaba.fastjson.JSON;
import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.exception.ServiceException;
import com.mall.cloud.pojo.vo.SeckillVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/seckill")
@Api(tags = "秒杀接口")
@Slf4j
public class SeckillController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private  RocketMQTemplate rocketMQTemplate;

    /**
     *
     * 假设用户100个并发请求，都会进去mq,执行100次数据库操作
     *
     *
     * 1 利用redis increment扣减
     *      扣减失败 直接返回
     *      扣减成功 查询redis是否已经秒杀到
     *              是  把刚刚扣减的数值加回来
     *              否  发送mq消息
     *
     * 消费者
     *    根据消息查询redis是否已经秒杀到
     *          是 把刚刚扣减的数值加回来 返回
     *          否 基于乐观锁或者悲观锁的方法修改数据库
     *                  成功  创建订单
     *                  失败  把扣减的值加回来
     * 再提供一个接口查询是否秒杀成功
     */
    @GetMapping("/test")
    @ApiOperation(value = "测试秒杀")
    public Result stock(Long userId, Long seckillId) {

        Long userQueueCount = stringRedisTemplate.opsForHash().increment("repeat" + seckillId, userId.toString(), 1);
        if(userQueueCount>1){
            throw new ServiceException("同一个商品重复抢单");
        }

        //2.原子性扣减
        Long stock = stringRedisTemplate.opsForHash().increment("seckill_stock", seckillId.toString(), -1);
        if(stock < 0) {
            return new Result(false, StatusCode.ERROR,"秒杀已经售空");
        }

        //3.判断是否已经秒杀到了
        Object obj = stringRedisTemplate.opsForHash().get("seckill_success"+seckillId, userId);
        if(obj != null) {
            //加回去
            stringRedisTemplate.opsForHash().increment("seckill_stock", seckillId.toString(), 1);
            return new Result(false, StatusCode.ERROR,"不能重复秒杀");
        }

        SeckillVo seckillVo=new SeckillVo();
        seckillVo.setUserId(userId);

        seckillVo.setSeckillId(seckillId);

        //4.异步放入mq中实现修改商品的库存
        rocketMQTemplate.asyncSend("seckill_queue", JSON.toJSONString(seckillVo), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("发送消息到mq--> seckill_queue,发送消息成功到消息服务器");
            }
            @Override
            public void onException(Throwable throwable) {
                log.info("{}发送失败; {}", "seckill_queue", throwable.getMessage());
            }
        });
        return new Result(true, StatusCode.OK,"正在排队中.......");

    }
}
