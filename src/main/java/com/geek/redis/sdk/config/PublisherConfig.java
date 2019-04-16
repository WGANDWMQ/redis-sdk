package com.geek.redis.sdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 发布者配置
 * @author: Geek Wang
 * @createDate: 2019/2/12 14:07
 * @version: 1.0
 */
@Configuration
public class PublisherConfig {
    @Bean
    public RedisTemplate<String, Object> redisMessageTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new FastJsonRedisSerializer<>(Object.class));
        return template;
    }
}
