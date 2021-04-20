package com.mall.cloud.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RedissonConfig {

    @Autowired
    private Environment env;
    @Bean
    public RedissonClient redissonClient() throws IOException {
//        String[] profiles = env.getActiveProfiles();
//        String profile = "";
//        if(profiles.length > 0) {
//            profile = "-" + profiles[0];
//        }
//        Config config = Config.fromYAML(new ClassPathResource("redisson-dev.yml").getInputStream());

        Config config = new Config();
        config.useSingleServer().setAddress("redis://172.30.4.43:6379");
        return Redisson.create(config);
    }
}
