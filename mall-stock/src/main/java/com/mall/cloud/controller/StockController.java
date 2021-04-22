package com.mall.cloud.controller;

import com.mall.cloud.api.UserFeignService;
import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import com.mall.cloud.service.ProductStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/stock")
@Api(tags = "库存接口")
public class StockController {

    @Autowired
    private ProductStockService productStockService;


    @Autowired
    private UserFeignService userFeignService;


    @GetMapping("/test")
    @ApiOperation(value = "测试分布式锁分断加锁库存")
    public Result stock(Integer productId, Integer bypPoductStock) {
//            productStockService.reduceProductStock(productId,bypPoductStock);
        return new Result(true, StatusCode.OK,"购买成功");
    }



    @GetMapping("/user")
    @ApiOperation(value = "测试微服务之间调用携带token")
    public Result user() {
        userFeignService.testToken();

        return new Result(true, StatusCode.OK,"调用成功");
    }
}
