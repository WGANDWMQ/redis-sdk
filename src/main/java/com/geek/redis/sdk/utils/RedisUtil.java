package com.geek.redis.sdk.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BulkMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * Redis 命令参考 对应 Java RedisTemplate API
 * @author: wanggang
 * @createDate: 2019/1/17 11:37
 * @version: 1.0
 */
public final class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    //=============================RedisTemplate针对Key操作============================

    /**
     * DEL key
     * 功能描述：删除给定的一个key。不存在的 key 会被忽略。
     * 可用版本：>= 1.0.0
     * 时间复杂度：
     *          O(N)， N 为被删除的 key 的数量。
     *          删除单个字符串类型的 key ，时间复杂度为O(1)。
     *          删除单个列表、集合、有序集合或哈希表类型的 key ，时间复杂度为O(M)， M 为以上数据结构内的元素数量。
     * @param key 键
     * @return True:删除成功，false:删除失败
     */
    public Boolean delByKey(String key){
       return redisTemplate.delete(key);
    }

    /**
     * DEL [key ...]
     * 功能描述：删除给定的一个key。不存在的 key 会被忽略。
     * 可用版本：>= 1.0.0
     * 时间复杂度：
     *          O(N)， N 为被删除的 key 的数量。
     *          删除单个字符串类型的 key ，时间复杂度为O(1)。
     *          删除单个列表、集合、有序集合或哈希表类型的 key ，时间复杂度为O(M)， M 为以上数据结构内的元素数量。
     * @param keys 多个键
     * @return 被删除 key 的数量。
     */
    public Long delByKey(String ... keys){
       return redisTemplate.delete(CollectionUtils.arrayToList(keys));
    }

    /**
     * DUMP key
     * 功能描述：序列化给定 key ，并返回被序列化的值，使用 RESTORE 命令可以将这个值反序列化为 Redis 键。
     *              序列化生成的值有以下几个特点：
     *              它带有 64 位的校验和，用于检测错误， RESTORE 在进行反序列化之前会先检查校验和。
     *              值的编码格式和 RDB 文件保持一致。
     *              RDB 版本会被编码在序列化值当中，如果因为 Redis 的版本不同造成 RDB 格式不兼容，那么 Redis 会拒绝对这个值进行反序列化操作。
     *              序列化的值不包括任何生存时间信息。
     * 可用版本：>= 2.6.0
     * 时间复杂度：
     *          查找给定键的复杂度为 O(1) ，对键进行序列化的复杂度为 O(N*M) ，其中 N 是构成 key 的 Redis 对象的数量，而 M 则是这些对象的平均大小。
     *          如果序列化的对象是比较小的字符串，那么复杂度为 O(1) 。
     * @param key
     * @return 如果 key 不存在，那么返回 nil 。否则，返回序列化之后的值。
     */
    public byte[] dumpByKey(String  key){
       return redisTemplate.dump(key);
    }

    /**
     * EXISTS key
     * 功能描述：检查给定 key 是否存在。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean existsByKey(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * EXPIRE key seconds
     * 功能描述：为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。在 Redis 中，带有生存时间的 key 被称为『易失的』(volatile)。
     *          生存时间可以通过使用 DEL 命令来删除整个 key 来移除，或者被 SET 和 GETSET 命令覆写(overwrite)，这意味着，如果一个命令只是修改(alter)一个带生存时间的 key 的值而不是用一个新的 key 值来代替(replace)它的话，
     *          那么生存时间不会被改变。比如说，对一个 key 执行 INCR 命令，对一个列表进行 LPUSH 命令，或者对一个哈希表执行 HSET 命令，这类操作都不会修改 key 本身的生存时间。
     *          另一方面，如果使用 RENAME 对一个 key 进行改名，那么改名后的 key 的生存时间和改名前一样。
     *          RENAME 命令的另一种可能是，尝试将一个带生存时间的 key 改名成另一个带生存时间的 another_key ，这时旧的 another_key (以及它的生存时间)会被删除，然后旧的 key 会改名为 another_key ，因此，新的 another_key 的生存时间也和原本的 key 一样。
     *          使用 PERSIST 命令可以在不删除 key 的情况下，移除 key 的生存时间，让 key 重新成为一个『持久的』(persistent) key 。
     * 更新生存时间：
     *          可以对一个已经带有生存时间的 key 执行 EXPIRE 命令，新指定的生存时间会取代旧的生存时间。
     * 过期时间的精确度：
     *          在 Redis 2.4 版本中，过期时间的延迟在 1 秒钟之内 —— 也即是，就算 key 已经过期，但它还是可能在过期之后一秒钟之内被访问到，而在新的 Redis 2.6 版本中，延迟被降低到 1 毫秒之内
     * Redis 2.1.3 之前的不同之处：
     *          在 Redis 2.1.3 之前的版本中，修改一个带有生存时间的 key 会导致整个 key 被删除，这一行为是受当时复制(replication)层的限制而作出的，现在这一限制已经被修复。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key 键
     * @param time 时间(秒)
     * @return 设置成功返回 true 。当 key 不存在或者不能为 key 设置生存时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的生存时间)，返回 false 。
     */
    public boolean expireByKey(String key,long time){
       return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * EXPIREAT key timestamp
     * 功能描述：EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
     * 可用版本：>= 1.2.0
     * 时间复杂度：O(1)
     * @param key
     * @param date
     * @return 如果生存时间设置成功，返回 true 。当 key 不存在或没办法设置生存时间，返回 false 。
     */
    public Boolean expireAtByKey(String key, Date date){
        return  redisTemplate.expireAt(key,date);
    }

    /**
     * KEYS pattern
     * 功能描述：查找所有符合给定模式 pattern 的 key 。
     *          KEYS * 匹配数据库中所有 key 。
     *          KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     *          KEYS h*llo 匹配 hllo 和 heeeeello 等。
     *          KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     *          特殊符号用 \ 隔开
     *          KEYS 的速度非常快，但在一个大的数据库中使用它仍然可能造成性能问题，如果你需要从一个数据集中查找特定的 key ，你最好还是用 Redis 的集合结构(set)来代替。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(N)， N 为数据库中 key 的数量。
     * @param pattern
     * @return 符合给定模式的 key 列表。
     */
    public Set<String> keysByKey(String pattern){
        return  redisTemplate.keys(pattern);
    }

    //MIGRATE暂时未找到对应的方法

    /**
     * MOVE key db
     * 功能描述：将当前数据库的 key 移动到给定的数据库 db 当中。
     *           如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果。
     *           因此，也可以利用这一特性，将 MOVE 当作锁(locking)原语(primitive)。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key
     * @param dbIndex
     * @return 移动成功返回 true ，失败则返回 false
     */
    public Boolean moveByKey(String key, int dbIndex){
        return  redisTemplate.move(key,dbIndex);
    }

    //OBJECT暂时未找到对应的方法

    /**
     * PERSIST key
     * 功能描述：移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
     * 可用版本：>= 2.2.0
     * 时间复杂度：O(1)
     * @param key
     * @return 当生存时间移除成功时，返回 true .如果 key 不存在或 key 没有设置生存时间，返回 false 。
     */
    public Boolean persistByKey(String key) {
        return  redisTemplate.persist(key);
    }

    /**
     * PEXPIRE key milliseconds
     * 功能描述：这个命令和 EXPIRE 命令的作用类似，但是它以毫秒为单位设置 key 的生存时间，而不像 EXPIRE 命令那样，以秒为单位。
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(1)
     * @param key
     * @param time
     * @return 设置成功，返回 true ，key 不存在或设置失败，返回 false
     */
    public boolean pexpireByKey(String key,long time){
        return redisTemplate.expire(key, time, TimeUnit.MICROSECONDS);
    }

    /**
     * PEXPIREAT key milliseconds-timestamp
     * 功能描述：这个命令和 EXPIREAT 命令类似，但它以毫秒为单位设置 key 的过期 unix 时间戳，而不是像 EXPIREAT 那样，以秒为单位。
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(1)
     * @param key
     * @param date
     * @return 如果生存时间设置成功，返回 true 。当 key 不存在或没办法设置生存时间时，返回 false 。(查看 EXPIRE 命令获取更多信息)
     */
    public Boolean pexpireAtByKey(String key, Date date){
        return  redisTemplate.expireAt(key,date);
    }


    /**
     * PTTL key
     * 功能描述：这个命令类似于 TTL 命令，但它以毫秒为单位返回 key 的剩余生存时间，而不是像 TTL 命令那样，以秒为单位。
     *           在 Redis 2.8 以前，当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 。
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(1)
     * @param key 键 不能为null
     * @return  当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以毫秒为单位，返回 key 的剩余生存时间。
     */
    public long pttlByKey(String key){
        return redisTemplate.getExpire(key,TimeUnit.MICROSECONDS);
    }


    /**
     * RANDOMKEY
     * 功能描述：从当前数据库中随机返回(不删除)一个 key 。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @return 当数据库不为空时，返回一个 key 。当数据库为空时，返回 nil 。
     */
    public Object randomKeyByKey() {
        return redisTemplate.randomKey();
    }

    /**
     * RENAME key newkey
     * 功能描述：将 key 改名为 newkey 。
     *           当 key 和 newkey 相同，或者 key 不存在时，返回一个错误。
     *           当 newkey 已经存在时， RENAME 命令将覆盖旧值。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param oldKey
     * @param newKey
     */
    public void renameByKey(String oldKey, String newKey) {
         redisTemplate.rename(oldKey,newKey);
    }

    /**
     * RENAMENX key newkey
     * 功能描述：当且仅当 newkey 不存在时，将 key 改名为 newkey 。当 key 不存在时，返回一个错误。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param oldKey
     * @param newKey
     * @return 修改成功时，返回 true 。如果 newkey 已经存在，返回 false 。
     */
    public Boolean renamenxByKey(String oldKey, String newKey) {
       return redisTemplate.renameIfAbsent(oldKey,newKey);
    }

    /**
     * RESTORE key ttl serialized-value
     * 功能描述：反序列化给定的序列化值，并将它和给定的 key 关联。
     *           参数 ttl 以毫秒为单位为 key 设置生存时间；如果 ttl 为 0 ，那么不设置生存时间。
     *           RESTORE 在执行反序列化之前会先对序列化值的 RDB 版本和数据校验和进行检查，如果 RDB 版本不相同或者数据不完整的话，那么 RESTORE 会拒绝进行反序列化，并返回一个错误。
     * 可用版本：>= 2.6.0
     * 时间复杂度：查找给定键的复杂度为 O(1) ，对键进行反序列化的复杂度为 O(N*M) ，其中 N 是构成 key 的 Redis 对象的数量，而 M 则是这些对象的平均大小。
     *             有序集合(sorted set)的反序列化复杂度为 O(N*M*log(N)) ，因为有序集合每次插入的复杂度为 O(log(N)) 。如果反序列化的对象是比较小的字符串，那么复杂度为 O(1) 。
     * @param key
     * @param value
     * @param timeToLive
     * @param unit
     * @param replace
     */
    public void restoreByKey(String key, byte[] value, long timeToLive, TimeUnit unit, boolean replace) {
        redisTemplate.restore(key, value, timeToLive,  unit,  replace);
    }

    /**
     * 返回或保存给定列表、集合、有序集合 key 中经过排序的元素。排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
     * @param query
     * @return
     */
    public List<Object> sortByKey(SortQuery<String> query) {
        return redisTemplate.sort(query);
    }

    /**
     * 返回或保存给定列表、集合、有序集合 key 中经过排序的元素。排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
     * @param query
     * @param resultSerializer
     * @param <T>
     * @return
     */
    public <T> List<T> sortByKey(SortQuery<String> query, @Nullable RedisSerializer<T> resultSerializer) {
        return redisTemplate.sort(query,resultSerializer);
    }

    /**
     * 返回或保存给定列表、集合、有序集合 key 中经过排序的元素。排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
     * @param query
     * @param bulkMapper
     * @param <T>
     * @return
     */
    public <T> List<T> sortByKey(SortQuery<String> query, BulkMapper<T, Object> bulkMapper) {
        return redisTemplate.sort(query,bulkMapper);
    }

    /**
     * 返回或保存给定列表、集合、有序集合 key 中经过排序的元素。排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
     * @param query
     * @param bulkMapper
     * @param resultSerializer
     * @param <T>
     * @param <S>
     * @return
     */
    public <T, S> List<T> sortByKey(SortQuery<String> query, BulkMapper<T, S> bulkMapper,
                               @Nullable RedisSerializer<S> resultSerializer) {
        return redisTemplate.sort(query,bulkMapper,resultSerializer);
    }

    /**
     * 返回或保存给定列表、集合、有序集合 key 中经过排序的元素。排序默认以数字作为对象，值被解释为双精度浮点数，然后进行比较。
     * @param query
     * @param storeKey
     * @return
     */
    public Long sortByKey(SortQuery<String> query, String storeKey) {
        return redisTemplate.sort(query,storeKey);
    }

    /**
     * TTL key
     * 功能描述：以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     *          在 Redis 2.8 以前，当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key 键 不能为null
     * @return  当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以毫秒为单位，返回 key 的剩余生存时间。
     */
    public long ttlByKey(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * TYPE key
     * 功能描述：返回 key 所储存的值的类型。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key
     * @return
     *       1、none (key不存在)
     *       2、string (字符串)
     *      3、list (列表)
     *      4、set (集合)
     *      5、zset (有序集)
     *      6、hash (哈希表)
     */
    public DataType typeByKey(String key) {
        return redisTemplate.type(key);
    }

    //SCAN暂未找到对应的方法

    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, long time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expireByKey(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expireByKey(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param delta 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item,double delta){
        return redisTemplate.opsForHash().increment(key, item, delta);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key,long time,Object...values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if(time>0) expireByKey(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long lGetListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) expireByKey(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expireByKey(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
