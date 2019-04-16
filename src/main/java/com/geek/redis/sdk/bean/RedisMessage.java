package com.geek.redis.sdk.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: Geek Wang
 * @createDate: 2019/2/12 14:10
 * @version: 1.0
 */
@Data
public class RedisMessage implements Serializable {
    public String msgId;
    public long createStamp;
}
