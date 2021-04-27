package com.mall.cloud.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import org.springframework.web.bind.annotation.PathVariable;

public class CustomerBlockHandler {

    public static  Result handlerException(@PathVariable("id") String id, BlockException e){
        return new Result(false, StatusCode.ERROR,"返回自定义限流提示"+id);
    }
}
