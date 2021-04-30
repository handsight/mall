package com.mall.cloud.controller;

import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.mapper.Order2Mapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/sharding")
@Api(tags = "ShardingJdbc complex接口")
@Slf4j
public class ShardingJdbcController2 {

    @Autowired
    private Order2Mapper order2Mapper;

    @GetMapping("/addOrder")
    @ApiOperation(value = "先按时间分库 再按用户id 订单id水平分表")
    public Result addOrder() {

        Long u1=2L;
        order2Mapper.insertOrder(new BigDecimal("9527"),  u1, "SUCCESS", LocalDateTime.now());
        return new Result(true, StatusCode.OK, "添加成功");
    }


}
