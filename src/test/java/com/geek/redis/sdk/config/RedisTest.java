package com.geek.redis.sdk.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wanggang
 * @createDate: 2019/1/17 14:31
 * @version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testListDelete(){
        List<String> test = new ArrayList<>();
        test.add("1");
        test.add("2");
        test.add("3");
        test.add("4");

        redisTemplate.opsForList().rightPushAll("test", test);
        System.out.println(redisTemplate.opsForList().range("test", 0, -1)); // [1, 2, 3, 4]
        redisTemplate.delete("test");
        System.out.println(redisTemplate.opsForList().range("test", 0, -1)); // []

    }

}
