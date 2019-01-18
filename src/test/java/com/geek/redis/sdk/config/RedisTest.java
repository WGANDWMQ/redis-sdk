package com.geek.redis.sdk.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void testString(){
        //操作String类型的数据
        ValueOperations<String, String> valueStr = redisTemplate.opsForValue();
        //存储一条数据
        valueStr.set("set:geek","天安门广场");
//        //获取一条数据并输出
        String goodsName = valueStr.get("set:geek");
       System.out.println(goodsName);

//        //存储多条数据
//        Map<String,String> map = new HashMap<>();
//        map.put("goodsName","福特汽车");
//        map.put("goodsPrice","88888");
//        map.put("goodsId","88");
//
//        valueStr.multiSet(map);
//        //获取多条数据
//        System.out.println("========================================");
//        List<String> list = new ArrayList<>();
//        list.add("goodsName");
//        list.add("goodsPrice");
//        list.add("goodsId");
//        list.add("goodsProdu");
//
//        List<String> listKeys = valueStr.multiGet(list);
//        for (String key : listKeys) {
//            System.out.println(key);
//        }
    }

}
