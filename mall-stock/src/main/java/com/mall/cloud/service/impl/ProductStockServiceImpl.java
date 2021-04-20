package com.mall.cloud.service.impl;

import com.mall.cloud.dto.ProductStock;
import com.mall.cloud.exception.ServiceException;
import com.mall.cloud.mapper.ProductStockMapper;
import com.mall.cloud.service.ProductStockService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ProductStockServiceImpl implements ProductStockService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ProductStockMapper productStockMapper;

    /**
     * 扣减库存
     *
     * @param productId
     * @param bypPoductStock
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reduceProductStock(Integer productId, Integer bypPoductStock) {

        Map<RLock, Integer> lockSeqMap = new ConcurrentHashMap();

        // 1.查询商品库存是否大于购买库存
        ProductStock productStock = productStockMapper.findById(productId);
        if (productStock == null) {
            return;
        }


        Integer stock01 = productStock.getStock01();
        Integer stock02 = productStock.getStock02();
        Integer stock03 = productStock.getStock03();
        Integer stock04 = productStock.getStock04();
        Integer stock05 = productStock.getStock05();
        Integer stock06 = productStock.getStock06();
        Integer stock07 = productStock.getStock07();
        Integer stock08 = productStock.getStock08();
        Integer stock09 = productStock.getStock09();
        Integer stock10 = productStock.getStock10();

        Integer sumStock = stock01 + stock02 + stock03 + stock04 + stock05 + stock06 + stock07 + stock08 + stock09 + stock10;
        if (bypPoductStock > sumStock) {
            throw new ServiceException("库存数量不足");
        }

        // 2.做一个随机算法,随机获取分段库存

        //获取商品的segment 分段值
        Integer stockSegment = productStock.getStockSegment();
        // 随机一个 segment 区间    1 ～ 5 segment
        Integer seq = getSegmentSeq(stockSegment);
        log.info("随机选择第" + seq + "段库存");

        RLock lock = redissonClient.getLock("product_stock_segment_seq" + seq);
        try {
            lock.lock();
            //查询其他库存标记
            boolean findOthersStock = false;
            //如果此分段库存为0的情况下
            Integer productStockBySegment = getProductStockBySegment(productId, seq);
            if (productStockBySegment == 0) {
                //释放锁
//                lock.unlock();
                if (lock.isLocked()) {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); //两个判断条件是非常必要的
                    }
                }

                /**
                 * 查找下一个segment 段段库存  假设查询第3个分段
                 */
                for (Integer i = 1; i <= stockSegment; i++) {
                    //过滤上一个segment
                    if (i.equals(seq)) {
                        lockSeqMap.put(lock, i);
                        continue;
                    }
                    lock = redissonClient.getLock("product_stock_segment_seq" + i);
                    productStockBySegment = getProductStockBySegment(productId, i);
                    /**
                     * 找到之后直接break；
                     */
                    if (productStockBySegment != 0) {
                        seq = i;
                        findOthersStock = true;
                        lockSeqMap.put(lock, i);
                        break;
                    } else {
                        findOthersStock = false;
                        if (lock.isLocked()) {
                            if (lock.isHeldByCurrentThread()) {
                                lock.unlock();
                            }
                        }

                    }
                }

                /**
                 * 库存不足
                 */
                if (!findOthersStock) {
                    throw new ServiceException("库存不足");
                }

            }


            // 如果库存分段正好大于要购买的数量
            if (productStockBySegment >= bypPoductStock) {
                //扣减库存 segmengt 段段库存
                reduceProductSegmentStock(productId, bypPoductStock, seq);
                log.info("随机选择第" + seq + "段库存扣除" + bypPoductStock + "成功");
                //realse
                if (lock.isLocked()) {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); //两个判断条件是非常必要的
                    }
                }


            } else {
                // 代码走到这里，就证明说，当前这个分段的库存小于要购买的数量，合并分段加锁
                Integer totalStock = productStockBySegment;

                Map<RLock, Integer> otherLocks = new ConcurrentHashMap(seq);

                for (Integer i = 1; i <= stockSegment; i++) {
                    if (i.equals(seq)) {
                        lockSeqMap.put(lock, i);
                        continue;

                    }
                    RLock otherLock = redissonClient.getLock("product_stock_segment_seq" + i);
                    Integer productStockBySegment1 = getProductStockBySegment(productId, i);
                    if (productStockBySegment1 == 0) {
//                        otherLock.unlock();
                        if (otherLock.isLocked()) {
                            if (otherLock.isHeldByCurrentThread()) {
                                otherLock.unlock(); //两个判断条件是非常必要的
                            }
                        }

                    }

                    if (productStockBySegment1 != 0) {
                        totalStock += productStockBySegment1;
                        otherLocks.put(otherLock, productStockBySegment1);
                        //添加
                        lockSeqMap.put(otherLock, i);
                    }

                    if (totalStock >= bypPoductStock) {
                        break;
                    }


                }
                if (totalStock < bypPoductStock) {
                    otherLocks.keySet().forEach(e -> {
                        log.info("释放锁成功");
                        if (e.isLocked()) {
                            if (e.isHeldByCurrentThread()) {
                                e.unlock();
                            }
                        }

                    });
                    throw new ServiceException("所有的库存segment不足");

                }


                /**
                 * productStockBySegment 为第一次
                 */
                Integer remainReducingStock = bypPoductStock - productStockBySegment;
                //把第一个seq 扣减 为0
                reduceProductSegmentStock(productId, getProductStockBySegment(productId, seq), seq);
//                lock.unlock();
                if (lock.isLocked()) {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock(); //两个判断条件是非常必要的
                    }
                }

                // 3 合并分段加锁 (每一segment 小于 购买库存数量)
                for (Map.Entry<RLock, Integer> otherLockEntry : otherLocks.entrySet()) {
                    if (remainReducingStock == 0) {
                        break;
                    }

                    Integer otherLockSeq = lockSeqMap.get(otherLockEntry.getKey());
                    if (otherLockSeq != null) {
                        log.info("otherLockSeq:" + otherLockSeq);
                        Integer otherStock = otherLockEntry.getValue();
                        RLock otherLockEntryKey = otherLockEntry.getKey();
                        //如果还是不足
                        if (remainReducingStock > otherStock) {
                            remainReducingStock -= otherStock;
                            reduceProductSegmentStock(productId, getProductStockBySegment(productId, otherLockSeq), otherLockSeq);

                        } else {
                            //如果是存在
                            reduceProductSegmentStock(productId, remainReducingStock, otherLockSeq);
                            remainReducingStock = 0;
                        }

//                        otherLockEntryKey.unlock();
                        if (otherLockEntryKey.isLocked()) {
                            if (otherLockEntryKey.isHeldByCurrentThread()) {
                                otherLockEntryKey.unlock();
                            }
                        }
                    }

                }

            }
            // 4 释放锁
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            lock.unlock();
        }

    }

    /**
     * 扣减segment库存
     *
     * @param productId
     * @param bypPoductStock
     */
    public void reduceProductSegmentStock(Integer productId, Integer bypPoductStock, Integer segmentIdSeq) {

        ProductStock productStockDb = productStockMapper.findById(productId);


        ProductStock productStock = new ProductStock();
        productStock.setId(productStockDb.getId());

        String fieldName = null;
        if (segmentIdSeq >= 1 && segmentIdSeq <= 9) {
            fieldName = "stock0" + segmentIdSeq;
        } else {
            fieldName = "stock" + segmentIdSeq;
        }

        try {

            Field field = ProductStock.class.getDeclaredField(fieldName);

            field.setAccessible(true);

            //  segment 库存
            Integer prodouctStockSeq = getFieldValueByFieldName(fieldName, productStockDb);

            field.set(productStock, prodouctStockSeq - bypPoductStock);

            //todo update
            productStockMapper.update(productStock);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 根据segment  seq 获取此  segment 段库存
     *
     * @param productId
     * @param segmentId
     * @return
     */

    public Integer getProductStockBySegment(Integer productId, Integer segmentId) {
        //通过 sgementId 获取  fieldName

        ProductStock productStock = productStockMapper.findById(productId);
        if (productStock == null) {
            throw new ServiceException("商品不存在");
        }

        log.info("productStock:" + productStock);
        log.info("segmentId:" + segmentId);
        String fieldName = null;
        if (segmentId >= 1 && segmentId <= 9) {
            fieldName = "stock0" + segmentId;
        } else {
            fieldName = "stock" + segmentId;
        }

        return Integer.valueOf(getFieldValueByFieldName(fieldName, productStock));
    }


    private Integer getSegmentSeq(Integer stockSegment) {
        return (int) (Math.random() * stockSegment + 1);
    }


    /**
     * 根据属性名获取属性值
     *
     * @param fieldName
     * @param object
     * @return
     */
    private Integer getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            //设置对象的访问权限，保证对private的属性的访问

            return (Integer) field.get(object);
        } catch (Exception e) {

            return null;
        }
    }

}
