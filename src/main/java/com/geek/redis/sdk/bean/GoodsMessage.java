package com.geek.redis.sdk.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: Geek Wang
 * @createDate: 2019/2/12 14:11
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsMessage extends RedisMessage {
    private String goodsType;
    private String number;
}
