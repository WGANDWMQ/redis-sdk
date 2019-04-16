package com.geek.redis.sdk.config;

import com.geek.redis.sdk.service.Publisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.DecimalFormat;

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

    @Autowired
    private Publisher publisher;

    @Test
    public void testListDelete(){

        //System.out.println(redisTemplate.opsForValue().increment("wg_incrByFloat2",Double.parseDouble("999999999999999.99")));
        //System.out.println(redisTemplate.opsForValue().increment("wg_incrByFloat",Double.parseDouble("-999999999999999")));
       // System.out.println(redisTemplate.opsForValue().get("wg_incrByFloat2"));
        System.out.println("科学计数法数字");
        double num1 = redisTemplate.opsForValue().increment("wg_incrByFloat7",Double.parseDouble("999999999999999.99"));
        System.out.println(new DecimalFormat("0.00").format(num1));
        BigDecimal bd1 = new BigDecimal(num1);
        System.out.println(bd1.toPlainString());
        System.out.println(bd1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        System.out.println("普通数字");
        double num2 = 50123.12;
        System.out.println(num2);
        BigDecimal bd2 = new BigDecimal(num2);
        System.out.println(bd2.toPlainString());
        System.out.println(bd2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
    }

}
