package com.geek.redis.sdk.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: Geek Wang
 * @createDate: 2019/2/12 14:14
 * @version: 1.0
 */
@Slf4j
public class UserReceiver extends AbstractReceiver {
    @Override
    public void receiveMessage(Object message) {
        System.out.println("接收到用户消息：" + JSON.toJSONString(message));
    }
}
