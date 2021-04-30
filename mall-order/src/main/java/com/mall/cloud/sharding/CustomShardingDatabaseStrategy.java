package com.mall.cloud.sharding;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomShardingDatabaseStrategy implements ComplexKeysShardingAlgorithm <String>{

    /**
     *
     * @param availableTargetNames  所有的库 ds1 ds2
     * @param shardingValue create_time,
     * @return
     */

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {
        for (String availableTargetName : availableTargetNames) {
            System.out.println("Database collection:"+availableTargetName);
        }

        Collection<String> shardingSuffix = new ArrayList<>();
        Map columnNameAndShardingValuesMap = shardingValue.getColumnNameAndShardingValuesMap();
        Collection<LocalDateTime> createTime = (Collection<LocalDateTime>) columnNameAndShardingValuesMap.get("create_time");
        for (LocalDateTime date : createTime) {
            Integer year = date.getYear();

            //todo 这样可以改成按年份 再按月份 数据库设计 order_db_2021_1 order_db_2021_2 ... order_db_2022_1 order_db_2022_2

            //按照年份取模
            Integer num = year % 2 + 1;
            //ds1 ds2
            for (String target : availableTargetNames) {
                if (target.endsWith(num.toString())) {
                    shardingSuffix.add(target);
                }
            }
        }
        System.out.println("最终选择的数据库："+shardingSuffix.toString());
        return shardingSuffix;
    }
}
