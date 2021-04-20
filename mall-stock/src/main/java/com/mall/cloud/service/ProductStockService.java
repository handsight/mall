package com.mall.cloud.service;

public interface ProductStockService {
    void reduceProductStock(Integer productId, Integer bypPoductStock);
}
