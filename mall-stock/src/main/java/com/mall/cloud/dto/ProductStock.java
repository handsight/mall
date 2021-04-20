package com.mall.cloud.dto;


import lombok.Data;

@Data
public class ProductStock {

    private Integer id;

    private Integer productId;

    private Integer stock01;

    private Integer stock02;

    private Integer stock03;

    private Integer stock04;

    private Integer stock05;

    private Integer stock06;

    private Integer stock07;

    private Integer stock08;

    private Integer stock09;

    private Integer stock10;

    /**
     * 库存分段
     */
    private Integer stockSegment;

}
