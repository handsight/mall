package com.mall.cloud.controller;

import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.mapper.DictMapper;
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
    @Autowired
    private DictMapper dictMapper;

    @GetMapping("/addOrder")
    @ApiOperation(value = "测试添加分库分表订单")
    public Result addOrder() {
        for (int i = 1; i < 20; i++) {
            orderMapper.insertOrder(new BigDecimal(i),  Long.parseLong(String.valueOf(i)), "SUCCESS");
        }
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @ApiOperation(value = "测试查询分库分表订单")
    @RequestMapping(value = "/queryOrder", method = RequestMethod.POST)
    public Result queryOrder(@RequestBody List<Long> ids ) {
        List<Map> maps = orderMapper.selectOrderbyIds(ids);
        return new Result(true, StatusCode.OK, "查询成功",maps);
    }

    @GetMapping("/addDict")
    @ApiOperation(value = "测试添加公共表字典")
    public Result addDict() {
        dictMapper.insertDict(1L,"user_type","0","管理员");
        dictMapper.insertDict(2L,"user_type","1","操作员");
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @GetMapping("/deleteDict")
    @ApiOperation(value = "测试删除公共表字典")
    public Result deleteDict() {
        dictMapper.deleteDict(1L);
        dictMapper.deleteDict(2L);
        return new Result(true, StatusCode.OK, "测试删除公共表字典");
    }
}
