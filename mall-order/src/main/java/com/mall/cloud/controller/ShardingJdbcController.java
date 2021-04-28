package com.mall.cloud.controller;

import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.mapper.OrderMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/shardingJdbc")
@Api(tags = "ShardingJdbc接口")
@Slf4j
public class ShardingJdbcController {

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/add")
    @ApiOperation(value = "测试添加")
    public Result add() {
        for (int i = 1; i < 20; i++) {
            orderMapper.insertOrder(new BigDecimal(i), 4L, "SUCCESS");
        }
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @ApiOperation(value = "测试查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Result query(@RequestBody List<Long> ids ) {
        List<Map> maps = orderMapper.selectOrderbyIds(ids);
        return new Result(true, StatusCode.OK, "查询成功",maps);
    }

}
