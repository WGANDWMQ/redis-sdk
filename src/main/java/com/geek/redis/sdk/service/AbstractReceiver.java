package com.geek.redis.sdk.service;

/**
 * @author: Geek Wang
 * @createDate: 2019/2/12 14:14
 * @version: 1.0
 */
public abstract class AbstractReceiver {
    public abstract void receiveMessage(Object message);
}
