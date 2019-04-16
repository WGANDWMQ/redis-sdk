package com.geek.redis.sdk.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: Geek Wang
 * @createDate: 2019/2/12 14:12
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserMessage extends RedisMessage {
    private String userId;
    private String username;
    private String password;
}
