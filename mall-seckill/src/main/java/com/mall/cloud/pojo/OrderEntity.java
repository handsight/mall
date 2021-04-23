package com.mall.cloud.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderEntity {

    /**
     * 库存id
     *
     */
    private Long seckillId;
    /**
     * 用户手机号码
     */
    private Long userId;
    /**
     * 订单状态
     */
    private Long state;
    /**
     * 创建时间
     */
    private Date createTime;
}
