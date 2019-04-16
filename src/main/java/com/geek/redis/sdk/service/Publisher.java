package com.geek.redis.sdk.service;

import com.geek.redis.sdk.bean.RedisMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息发布者
 * @author: Geek Wang
 * @createDate: 2019/2/12 14:13
 * @version: 1.0
 */
@Service
public class Publisher {
    private final RedisTemplate<String, Object> redisMessageTemplate;

    @Autowired
    public Publisher(RedisTemplate<String, Object> redisMessageTemplate) {
        this.redisMessageTemplate = redisMessageTemplate;
    }

    public void pushMessage(String topic, RedisMessage message) {
        redisMessageTemplate.convertAndSend(topic,message);
    }
}
