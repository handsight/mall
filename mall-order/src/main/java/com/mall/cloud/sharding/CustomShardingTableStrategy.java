package com.mall.cloud.sharding;


import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *自定义表的复合分片策略
 *
 *参考地址
 * https://blog.csdn.net/duduyingya/article/details/106120404?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-4&spm=1001.2101.3001.4242
 *
 */
public class CustomShardingTableStrategy implements ComplexKeysShardingAlgorithm <String>{

    /**
     *  t_order_u1_o1 t_order_u1_o2 t_order_u2_o1 t_order_u2_o2
     *
     * @param availableTargetNames  所有的表  比如 2021_userid取模_orderid取模
     * @param shardingValue create_time,user_id,order_id
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {
        for (String availableTargetName : availableTargetNames) {
            System.out.println("table collection:"+availableTargetName);
        }
        Collection<String> shardingSuffix = new ArrayList<>();
        Map columnNameAndShardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();
        Collection<Long> userIds = (Collection<Long>) columnNameAndShardingValuesMap.get("user_id");
        Collection<Long> orderIds = (Collection<Long>) columnNameAndShardingValuesMap.get("order_id");

        //todo 这样可以改成按月份划分 数据库设计 t_order_1_u1_o1 t_order_2_u1_o1 ... t_order_12_u1_o1
        //t_order_u1_o1,t_order_u1_o2,t_order_u2_o1,t_order_u2_o2
        for (Long uid : userIds) {
            Long userId = uid % 2 + 1;
            for (Long oid : orderIds) {
                Long orderId = oid % 2 + 1;
                for (String target : availableTargetNames) {
                    String value = "_u" + userId + "_o" + orderId;
                    if (target.endsWith(value)) {
                        shardingSuffix.add(target);
                        break;
                    }
                }
            }
        }
        System.out.println("最终选择的表："+shardingSuffix.toString());
        return shardingSuffix;
    }

}
