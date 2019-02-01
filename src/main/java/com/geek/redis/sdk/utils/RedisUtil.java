package com.geek.redis.sdk.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.BulkMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
    private RedisTemplate<String,Object> redisTemplate;

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

    //=============================RedisTemplate针对String操作============================

    /**
     * APPEND key value
     * 功能描述：如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。
     *           如果 key 不存在， APPEND 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(1)
     * @param key
     * @param value
     * @return 追加 value 之后， key 中字符串的长度。
     */
    public Integer append(String key,String value){
       return redisTemplate.opsForValue().append(key,value);
    }

    /**
     * BITCOUNT key [start] [end]
     * 功能描述：计算给定字符串中，被设置为 1 的比特位的数量。
     *           一般情况下，给定的整个字符串都会被进行计数，通过指定额外的 start 或 end 参数，可以让计数只在特定的位上进行。
     *           start 和 end 参数的设置和 GETRANGE 命令类似，都可以使用负数值：比如 -1 表示最后一个位，而 -2 表示倒数第二个位，以此类推。
     *           不存在的 key 被当成是空字符串来处理，因此对一个不存在的 key 进行 BITCOUNT 操作，结果为 0 。
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(N)
     * 运用：使用 bitmap 实现用户上线次数统计
     * 性能：前面的上线次数统计例子，即使运行 10 年，占用的空间也只是每个用户 10*365 比特位(bit)，也即是每个用户 456 字节。
     *       对于这种大小的数据来说， BITCOUNT 的处理速度就像 GET 和 INCR 这种 O(1) 复杂度的操作一样快。
     *       如果你的 bitmap 数据非常大，那么可以考虑使用以下两种方法：
     *          1、将一个大的 bitmap 分散到不同的 key 中，作为小的 bitmap 来处理。使用 Lua 脚本可以很方便地完成这一工作。
     *          2、使用 BITCOUNT 的 start 和 end 参数，每次只对所需的部分位进行计算，将位的累积工作(accumulating)放到客户端进行，并且对结果进行缓存 (caching)
     * @param key
     * @return 被设置为 1 的位的数量。
     */
    public Long bitCount(final String key) {
        return redisTemplate.execute((RedisConnection connection) ->{
            Long result = connection.bitCount(key.getBytes());
            return result;
        });
    }

    /**
     * 同上，增加计算开始和结束位置
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long bitCount(final String key, long start, long end) {
        return redisTemplate.execute((RedisConnection connection) ->{
            Long result = connection.bitCount(key.getBytes(),start,end);
            return result;
        });
    }

    /**
     * BITOP operation destkey key [key ...]
     * 功能描述：对一个或多个保存二进制位的字符串 key 进行位元操作，并将结果保存到 destkey 上。
     *           operation 可以是 AND 、 OR 、 NOT 、 XOR 这四种操作中的任意一种：
     *              1、BITOP AND destkey key [key ...] ，对一个或多个 key 求逻辑并，并将结果保存到 destkey 。
     *              2、BITOP OR destkey key [key ...] ，对一个或多个 key 求逻辑或，并将结果保存到 destkey 。
     *              3、BITOP XOR destkey key [key ...] ，对一个或多个 key 求逻辑异或，并将结果保存到 destkey 。
     *              4、BITOP NOT destkey key ，对给定 key 求逻辑非，并将结果保存到 destkey 。
     *           除了 NOT 操作之外，其他操作都可以接受一个或多个 key 作为输入。
     *           处理不同长度的字符串
     *           当 BITOP 处理不同长度的字符串时，较短的那个字符串所缺少的部分会被看作 0 。
     *           空的 key 也被看作是包含 0 的字符串序列。
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(N)
     * 注意：BITOP 的复杂度为 O(N) ，当处理大型矩阵(matrix)或者进行大数据量的统计时，最好将任务指派到附属节点(slave)进行，避免阻塞主节点。
     * @param op
     * @param key1
     * @param key2
     * @return 保存到 destkey 的字符串的长度，和输入 key 中最长的字符串长度相等。
     */
    public Long bitOp(RedisStringCommands.BitOperation op, final String key1, final String key2) {
        return redisTemplate.execute((RedisConnection connection) -> {
            Long result = connection.bitOp(op, key1.getBytes(), key2.getBytes());
            return result;
        });
    }

    /**
     * DECR key
     * 功能描述：将 key 中储存的数字值减一。
     *           如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
     *           如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     *           本操作的值限制在 64 位(bit)有符号数字表示之内。
     *           关于递增(increment) / 递减(decrement)操作的更多信息，请参见 INCR 命令。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key
     * @return 执行 DECR 命令之后 key 的值。
     */
    public Long decrement(String key){
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * DECRBY key decrement
     * 功能描述：将 key 所储存的值减去减量 decrement 。
     *           如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。
     *           如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     *           本操作的值限制在 64 位(bit)有符号数字表示之内。
     *           关于更多递增(increment) / 递减(decrement)操作的更多信息，请参见 INCR 命令。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key
     * @param delta 递减数
     * @return 减去 decrement 之后， key 的值。
     */
    public Long decrement(String key,long delta){
        return redisTemplate.opsForValue().decrement(key,delta);
    }

    /**
     * GET key
     * 功能描述：返回 key 所关联的字符串值。
     *           如果 key 不存在那么返回特殊值 nil 。
     *           假如 key 储存的值不是字符串类型，返回一个错误，因为 GET 只能用于处理字符串值。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key 键
     * @return  当 key 不存在时，返回 nil ，否则，返回 key 的值。如果 key 不是字符串类型，那么返回一个错误。
     */
    public Object get(String key){
        return  redisTemplate.opsForValue().get(key);
    }

    /**
     * GETBIT key offset
     * 功能描述：对 key 所储存的字符串值，获取指定偏移量上的位(bit)。
     *           当 offset 比字符串值的长度大，或者 key 不存在时，返回 0 。
     * 可用版本：>= 2.2.0
     * 时间复杂度：O(1)
     * @param key
     * @param offset
     * @return 字符串值指定偏移量上的位(bit)。
     */
    public Boolean getBit(String key, long offset){
        return  redisTemplate.opsForValue().getBit(key,offset);
    }

    /**
     * GETRANGE key start end
     * 功能描述：返回 key 中字符串值的子字符串，字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内)。
     *           负数偏移量表示从字符串最后开始计数， -1 表示最后一个字符， -2 表示倒数第二个，以此类推。
     *           GETRANGE 通过保证子字符串的值域(range)不超过实际字符串的值域来处理超出范围的值域请求。
     * 可用版本：>= 2.4.0
     * 时间复杂度：O(N)， N 为要返回的字符串的长度。复杂度最终由字符串的返回值长度决定，
     *             但因为从已有字符串中取出子字符串的操作非常廉价(cheap)，所以对于长度不大的字符串，该操作的复杂度也可看作O(1)。
     * 注意：在 <= 2.0 的版本里，GETRANGE 被叫作 SUBSTR。
     * @param key
     * @param start
     * @param end
     * @return 截取得出的子字符串。
     */
    public String get(String key, long start, long end){
        return  redisTemplate.opsForValue().get(key,start,end);
    }

    /**
     * GETSET key value
     * 功能描述：将给定 key 的值设为 value ，并返回 key 的旧值(old value)。当 key 存在但不是字符串类型时，返回一个错误。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key
     * @param value
     * @return 返回给定 key 的旧值。当 key 没有旧值时，也即是， key 不存在时，返回 nil 。
     */
    public Object getAndSet(String key, String value){
        return  redisTemplate.opsForValue().getAndSet(key,value);
    }

    /**
     * INCR key
     * 功能描述：将 key 中储存的数字值增一。如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
     *           如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。本操作的值限制在 64 位(bit)有符号数字表示之内。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * 注意：这是一个针对字符串的操作，因为 Redis 没有专用的整数类型，所以 key 内储存的字符串被解释为十进制 64 位有符号整数来执行 INCR 操作。
     * @param key
     * @return 执行 INCR 命令之后 key 的值。
     */
    public Long increment(String key){
        return  redisTemplate.opsForValue().increment(key);
    }

    /**
     * INCRBY key increment
     * 功能描述：将 key 所储存的值加上增量 increment 。如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。
     *           如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。本操作的值限制在 64 位(bit)有符号数字表示之内。
     *           关于递增(increment) / 递减(decrement)操作的更多信息，参见 INCR 命令。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key
     * @param delta
     * @return 加上 increment 之后， key 的值。
     */
    public Long increment(String key, long delta){
        return  redisTemplate.opsForValue().increment(key,delta);
    }

    /**
     * INCRBYFLOAT key increment
     * 功能描述：为 key 中所储存的值加上浮点数增量 increment 。如果 key 不存在，那么 INCRBYFLOAT 会先将 key 的值设为 0 ，再执行加法操作。
     *           如果命令执行成功，那么 key 的值会被更新为（执行加法之后的）新值，并且新值会以字符串的形式返回给调用者。
     *           无论是 key 的值，还是增量 increment ，都可以使用像 2.0e7 、 3e5 、 90e-2 那样的指数符号(exponential notation)来表示，
     *           但是，执行 INCRBYFLOAT 命令之后的值总是以同样的形式储存，也即是，它们总是由一个数字，
     *           一个（可选的）小数点和一个任意位的小数部分组成（比如 3.14 、 69.768 ，诸如此类)，小数部分尾随的 0 会被移除，
     *           如果有需要的话，还会将浮点数改为整数（比如 3.0 会被保存成 3 ）。
     *           除此之外，无论加法计算所得的浮点数的实际精度有多长， INCRBYFLOAT 的计算结果也最多只能表示小数点的后十七位。
     *           当以下任意一个条件发生时，返回一个错误：
     *                  key 的值不是字符串类型(因为 Redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     *                  key 当前的值或者给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number）
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(1)
     * @param key
     * @param delta
     * @return 执行命令之后 key 的值。
     */
    public Double increment(String key, double delta){
        return  redisTemplate.opsForValue().increment(key,delta);
    }

    /**
     * MGET key [key ...]
     * 功能描述：返回所有(一个或多个)给定 key 的值。如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。因此，该命令永不失败。
     * 可用版本：>= 1.0.0
     * 时间复杂度: O(N) , N 为给定 key 的数量。
     * @param keys
     * @return 一个包含所有给定 key 的值的列表。
     */
    public List<Object> multiGet(Collection<String> keys){
        return  redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * MSET key value [key value ...]
     * 功能描述：同时设置一个或多个 key-value 对。
     *           如果某个给定 key 已经存在，那么 MSET 会用新值覆盖原来的旧值，如果这不是你所希望的效果，
     *           请考虑使用 MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
     * 可用版本：>= 1.0.1
     * 时间复杂度：O(N)， N 为要设置的 key 数量。
     * 注意：MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，某些给定 key 被更新而另一些给定 key 没有改变的情况，不可能发生。
     * @param map
     */
    public Boolean multiSet(Map<String,Object> map){
        try {
            redisTemplate.opsForValue().multiSet(map);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * MSETNX key value [key value ...]
     * 功能描述：同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。
     *           即使只有一个给定 key 已存在， MSETNX 也会拒绝执行所有给定 key 的设置操作。
     * 可用版本：>= 1.0.1
     * 时间复杂度：O(N)， N 为要设置的 key 的数量。
     * 注意：MSETNX 是原子性的，因此它可以用作设置多个不同 key 表示不同字段(field)的唯一性逻辑对象(unique logic object)，所有字段要么全被设置，要么全不被设置。
     * @param map
     * @return 当所有 key 都成功设置，返回 true 。如果所有给定 key 都设置失败(至少有一个 key 已经存在)，那么返回 false 。
     */
    public Boolean multiSetIfAbsent(Map<String,Object> map){
       return redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    /**
     * PSETEX key milliseconds value
     * 功能描述：这个命令和 SETEX 命令相似，但它以毫秒为单位设置 key 的生存时间，而不是像 SETEX 命令那样，以秒为单位。
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(1)
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public Boolean pSetEx(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.MILLISECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * SET key value [EX seconds] [PX milliseconds] [NX|XX]
     * 功能描述：将字符串值 value 关联到 key 。如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     *           对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
     * 可选参数：
     *       从 Redis 2.6.12 版本开始， SET 命令的行为可以通过一系列参数来修改：
     *          EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key second value 。
     *          PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX millisecond 效果等同于 PSETEX key millisecond value 。
     *          NX ：只在键不存在时，才对键进行设置操作。 SET key value NX 效果等同于 SETNX key value 。
     *          XX ：只在键已经存在时，才对键进行设置操作。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * 注意：因为 SET 命令可以通过参数来实现和 SETNX 、 SETEX 和 PSETEX 三个命令的效果，所以将来的 Redis 版本可能会废弃并最终移除 SETNX 、 SETEX 和 PSETEX 这三个命令。
     * @param key 键
     * @param value 值
     * @return 在 Redis 2.6.12 版本以前， SET 命令总是返回 OK 。从 Redis 2.6.12 版本开始， SET 在设置操作成功完成时，才返回 OK 。
     *          如果设置了 NX 或者 XX ，但因为条件没达到而造成设置操作未执行，那么命令返回空批量回复（NULL Bulk Reply）。
     */
    public Boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * SETBIT key offset value
     * 功能描述：对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。
     *           位的设置或清除取决于 value 参数，可以是 0 也可以是 1 。
     *           当 key 不存在时，自动生成一个新的字符串值。
     *           字符串会进行伸展(grown)以确保它可以将 value 保存在指定的偏移量上。当字符串值进行伸展时，空白位置以 0 填充。
     *           offset 参数必须大于或等于 0 ，小于 2^32 (bit 映射被限制在 512 MB 之内)。
     * 可用版本：>= 2.2.0
     * 时间复杂度:O(1)
     * 注意：对使用大的 offset 的 SETBIT 操作来说，内存分配可能造成 Redis 服务器被阻塞。具体参考 SETRANGE 命令，warning(警告)部分。
     * @param key
     * @param offset
     * @param value
     * @return true:成功，false:失败
     */
    public Boolean setBit(String key, long offset, boolean value){
        return  redisTemplate.opsForValue().setBit(key, offset,value);
    }

    /**
     * SETEX key seconds value
     * 功能描述：将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)。如果 key 已经存在， SETEX 命令将覆写旧值。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(1)
     * 注意：SETEX 是一个原子性(atomic)操作，关联值和设置生存时间两个动作会在同一时间内完成，该命令在 Redis 用作缓存时，非常实用。
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public Boolean setEx(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * SETNX key value
     * 功能描述：将 key 的值设为 value ，当且仅当 key 不存在。若给定的 key 已经存在，则 SETNX 不做任何动作。
     *           SETNX 是『SET if Not eXists』(如果不存在，则 SET)的简写。
     * 可用版本：>= 1.0.0
     * 时间复杂度：O(1)
     * @param key
     * @param value
     * @return true:成功，false:失败
     */
    public Boolean setIfAbsent(String key, String value){
       return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * SETRANGE key offset value
     * 功能描述：用 value 参数覆写(overwrite)给定 key 所储存的字符串值，从偏移量 offset 开始。不存在的 key 当作空白字符串处理。
     *           SETRANGE 命令会确保字符串足够长以便将 value 设置在指定的偏移量上，如果给定 key 原来储存的字符串长度比偏移量小(比如字符串只有 5 个字符长，
     *           但你设置的 offset 是 10 )，那么原字符和偏移量之间的空白将用零字节(zerobytes, "\x00" )来填充。
     * 可用版本：>= 2.2.0
     * 时间复杂度：对小(small)的字符串，平摊复杂度O(1)。(关于什么字符串是”小”的，请参考 APPEND 命令) 否则为O(M)， M 为 value 参数的长度。
     * 注意：你能使用的最大偏移量是 2^29-1(536870911) ，因为 Redis 字符串的大小被限制在 512 兆(megabytes)以内。如果你需要使用比这更大的空间，你可以使用多个 key 。
     *       当生成一个很长的字符串时，Redis 需要分配内存空间，该操作有时候可能会造成服务器阻塞(block)。在2010年的Macbook Pro上，
     *       设置偏移量为 536870911(512MB 内存分配)，耗费约 300 毫秒， 设置偏移量为 134217728(128MB 内存分配)，耗费约 80 毫秒，
     *       设置偏移量 33554432(32MB 内存分配)，耗费约 30 毫秒，设置偏移量为 8388608(8MB 内存分配)，耗费约 8 毫秒。
     *       注意若首次内存分配成功之后，再对同一个 key 调用 SETRANGE 操作，无须再重新内存。
     * @param key
     * @param value
     * @param offset 偏移量
     * @return true:成功，false:失败
     */
    public Boolean set(String key, String value, long offset){
        try {
            redisTemplate.opsForValue().set(key, value, offset);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * STRLEN key
     * 功能描述：返回 key 所储存的字符串值的长度。当 key 储存的不是字符串值时，返回一个错误。
     * 可用版本：>= 2.2.0
     * 复杂度：O(1)
     * @param key
     * @return 字符串值的长度。当 key 不存在时，返回 0 。
     */
    public Long size(String key){
        return redisTemplate.opsForValue().size(key);
    }

    //================================RedisTemplate针对Hash操作=================================

    /**
     * HDEL key field [field ...]
     * 功能描述：删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     * 可用版本：>= 2.0.0
     * 时间复杂度:O(N)， N 为要删除的域的数量。
     * 注意：在Redis2.4以下的版本里， HDEL 每次只能删除单个域，如果你需要在一个原子时间内删除多个域，请将命令包含在 MULTI / EXEC 块内。
     * @param key 键 不能为null
     * @param field 项 多个field
     * @return 被成功移除的域的数量，不包括被忽略的域。
     */
    public Long hdel(String key, Object... field){
        return redisTemplate.opsForHash().delete(key,field);
    }

    /**
     * HEXISTS key field
     * 功能描述：查看哈希表 key 中，给定域 field 是否存在。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(1)
     * @param key 键 不能为null
     * @param field 域
     * @return true：存在 false：不存在
     */
    public Boolean hExists(String key, String field){
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * HGET key field
     * 功能描述：返回哈希表 key 中给定域 field 的值。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(1)
     * @param key 键 不能为null
     * @param field 项 不能为null
     * @return 给定域的值。当给定域不存在或是给定 key 不存在时，返回 nil 。
     */
    public Object hget(String key,String field){
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * HGETALL key
     * 功能描述：返回哈希表 key 中，所有的域和值。在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(N)， N 为哈希表的大小。
     * @param key 键
     * @return 以键值对形式返回哈希表的域和域的值。若 key 不存在，返回空列表。
     */
    public Map<Object,Object> hGetAll(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HINCRBY key field increment
     * 功能描述：为哈希表 key 中的域 field 的值加上增量 increment 。增量也可以为负数，相当于对给定域进行减法操作。
     *           如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
     *           对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。本操作的值被限制在 64 位(bit)有符号数字表示之内。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(1)
     * @param key 键
     * @param item 项
     * @param delta 要增加几(大于0)
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值。
     */
    public Long hIncrBy(String key, String item,Long delta){
        return redisTemplate.opsForHash().increment(key, item, delta);
    }

    /**
     * HINCRBYFLOAT key field increment
     * 功能描述：为哈希表 key 中的域 field 加上浮点数增量 increment 。
     *           如果哈希表中没有域 field ，那么 HINCRBYFLOAT 会先将域 field 的值设为 0 ，然后再执行加法操作。
     *           如果键 key 不存在，那么 HINCRBYFLOAT 会先创建一个哈希表，再创建域 field ，最后再执行加法操作。
     *           当以下任意一个条件发生时，返回一个错误：
     *              域 field 的值不是字符串类型(因为 redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     *              域 field 当前的值或给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number)
     *           HINCRBYFLOAT 命令的详细功能和 INCRBYFLOAT 命令类似，请查看 INCRBYFLOAT 命令获取更多相关信息。
     * 可用版本：>= 2.6.0
     * 时间复杂度：O(1)
     * @param key 键
     * @param item 项
     * @param delta 要增加几(大于0)
     * @return 执行加法操作之后 field 域的值。
     */
    public double hIncrBy(String key, String item,double delta){
        return redisTemplate.opsForHash().increment(key, item, delta);
    }

    /**
     * HKEYS key
     * 功能描述：返回哈希表 key 中的所有域。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(N)， N 为哈希表的大小。
     * @param key
     * @return 一个包含哈希表中所有域的表。当 key 不存在时，返回一个空表。
     */
    public Set<Object> hKeys(String key){
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * HLEN key
     * 功能描述：返回哈希表 key 中域的数量。
     * 时间复杂度：O(1)
     * @param key
     * @return 哈希表中域的数量。当 key 不存在时，返回 0 。
     */
    public Long hLen(String key){
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * HMGET key field [field ...]
     * 功能描述：返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。
     *           因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(N)， N 为给定域的数量。
     * @param key
     * @param hashKeys
     * @return 一个包含多个给定域的关联值的表，表值的排列顺序和给定域参数的请求顺序一样。
     */
    public List<Object> hMGet(String key, Collection<Object> hashKeys){
        return redisTemplate.opsForHash().multiGet(key,hashKeys);
    }

    /**
     * HMSET key field value [field value ...]
     * 功能描述：同时将多个 field-value (域-值)对设置到哈希表 key 中。此命令会覆盖哈希表中已存在的域。如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(N)， N 为 field-value 对的数量。
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public Boolean hMSet(String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * HSET key field value
     * 功能描述：将哈希表 key 中的域 field 的值设为 value 。如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。如果域 field 已经存在于哈希表中，旧值将被覆盖。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(1)
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
            return false;
        }
    }

    /**
     * HSETNX key field value
     * 功能描述：将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。若域 field 已经存在，该操作无效。
     *           如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(1)
     * @param key
     * @param hashKey
     * @param value
     * @return true:成功，false:失败
     */
    public Boolean hSetNX(String key, String hashKey, String value){
        return  redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * HVALS key
     * 功能描述：返回哈希表 key 中所有域的值。
     * 可用版本：>= 2.0.0
     * 时间复杂度：O(N)， N 为哈希表的大小。
     * @param key
     * @return 一个包含哈希表中所有值的表。当 key 不存在时，返回一个空表。
     */
    public List<Object> hVals(String key){
        return  redisTemplate.opsForHash().values(key);
    }

    //HSCAN后期更新

    /**
     * HSTRLEN key field
     * 功能描述：返回哈希表 key 中， 与给定域 field 相关联的值的字符串长度（string length）。如果给定的键或者域不存在， 那么命令返回 0 。
     * 可用版本：>= 3.2.0
     * 时间复杂度：O(1)
     * @param key
     * @param hashKey
     * @return 一个整数
     */
    public Long hStrLen(String key, Object hashKey) {
        return  redisTemplate.opsForHash().lengthOfValue(key,hashKey);
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
