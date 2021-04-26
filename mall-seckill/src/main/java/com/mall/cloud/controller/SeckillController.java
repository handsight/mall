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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/seckill")
@Api(tags = "秒杀接口")
@Slf4j
@RefreshScope
public class SeckillController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private  RocketMQTemplate rocketMQTemplate;


    @Value("${rsa.publicKey}")
    public String publicKey;

    @GetMapping("/test")
    @ApiOperation(value = "测试秒杀")
    public Result stock(Long userId, Long seckillId,Integer num) {

        //1.过滤重复抢单
        Long userQueueCount = stringRedisTemplate.opsForValue().increment("repeat"+seckillId+"-"+userId,1);
        if(userQueueCount>1){
            stringRedisTemplate.expire("repeat"+seckillId+"-"+userId,1000, TimeUnit.SECONDS);
            throw new ServiceException("同一个商品重复抢单");
        }

        //2.原子性扣减
        Long stock = stringRedisTemplate.opsForHash().increment("seckill_stock", seckillId.toString(), -num);
        if(stock < 0) {
            return new Result(false, StatusCode.ERROR,"秒杀已经售空");
        }

        //3.判断是否已经秒杀到了
        Object obj = stringRedisTemplate.opsForHash().get("seckill_success"+seckillId, userId.toString());
        if(obj != null) {
            return new Result(false, StatusCode.ERROR,"不能重复秒杀");
        }

        SeckillVo seckillVo=new SeckillVo();
        seckillVo.setUserId(userId);
        seckillVo.setSeckillId(seckillId);
        seckillVo.setNum(num);

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


    @GetMapping("/query")
    @ApiOperation(value = "秒杀结果查询")
    public Result query(Long userId, Long seckillId) {

        Object obj = stringRedisTemplate.opsForHash().get("seckill_success"+seckillId, userId.toString());
        if (obj == null) {
            return new Result(false, StatusCode.ERROR,"秒杀失败");
        }
        return new Result(true, StatusCode.OK,"秒杀成功");
    }


    @GetMapping("/publicKey")
    @ApiOperation(value = "测试nacos-config")
    public Result getPublicKey(){
        return new Result(true, StatusCode.OK,publicKey);
    }
}
