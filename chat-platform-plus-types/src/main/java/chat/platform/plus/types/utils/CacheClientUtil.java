package chat.platform.plus.types.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 缓存工具类
 */
@Component
@Slf4j
public class CacheClientUtil {

    @Resource
    private RedissonClient redissonClient;

    public <T> void cacheStr(String key, T value) {
        cacheStrWithTTL(key, value, null, null);
    }

    public <T> void cacheStrWithTTL(String key, T value, Long time, TimeUnit unit) {
        String jsonStr = JSONUtil.toJsonStr(value);
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (time != null && unit != null) {
            bucket.set(jsonStr, time, unit);
        } else {
            bucket.set(jsonStr);
        }
    }

    public <T> T getStr(String key, Class<T> type, Supplier<T> dbFallback) {
        return getStrWithTTL(key, type, dbFallback, null, null);
    }

    public <T> T getStrWithTTL(String key, Class<T> type, Supplier<T> dbFallback, Long time, TimeUnit unit) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        String jsonStr = bucket.get();
        // 缓存存在 - 反序列化后返回
        if (StrUtil.isNotBlank(jsonStr)) {
            return JSONUtil.toBean(jsonStr, type);
        }
        // 不存在则查询数据库
        T result = dbFallback.get();
        if (result == null) {
            return null;
        }
        if (time != null && unit != null) {
            cacheStrWithTTL(key, result, time, unit);
        } else {
            cacheStr(key, result);
        }
        return result;
    }

    public <T> void cacheList(String key, List<T> value) {
        cacheListWithTTL(key, value, null, null);
    }

    public <T> void cacheListWithTTL(String key, List<T> value, Long time, TimeUnit unit) {
        RList<String> list = redissonClient.getList(key);
        list.clear();
        for (T t : value) {
            list.add(JSONUtil.toJsonStr(t));
        }
        if (time != null && unit != null) {
            list.expire(time, unit);
        }
    }

    public <T> List<T> getList(String key, Class<T> type, Supplier<List<T>> dbFallback) {
        return getListWithTTL(key, type, dbFallback, null, null);
    }

    public <T> List<T> getListWithTTL(String key, Class<T> type, Supplier<List<T>> dbFallback, Long time, TimeUnit unit) {
        RList<String> list = redissonClient.getList(key);
        if (list != null && !list.isEmpty()) {
            return list.stream()
                    .map(jsonStr -> JSONUtil.toBean(jsonStr, type))
                    .toList();
        }
        List<T> resultList = dbFallback.get();
        if (resultList == null) {
            return null;
        }
        if (time != null && unit != null) {
            cacheListWithTTL(key, resultList, time, unit);
        } else {
            cacheList(key, resultList);
        }
        return resultList;
    }

    public <T> void cacheHash(String key, String hashKey, T value) {
        cacheHashWithTTL(key, hashKey, value, null, null);
    }

    public <T> void cacheHashWithTTL(String key, String hashKey, T value, Long time, TimeUnit unit) {
        String jsonStr = JSONUtil.toJsonStr(value);
        RMap<String, String> map = redissonClient.getMap(key);
        map.put(hashKey, jsonStr);
        if (time != null && unit != null) {
            map.expire(time, unit);
        }
    }

    public <T> Map<String, T> getHash(String key, Class<T> type, Supplier<Map<String, T>> dbFallback) {
        return getHashWithTTL(key, type, dbFallback, null, null);
    }

    public <T> Map<String, T> getHashWithTTL(String key, Class<T> type, Supplier<Map<String, T>> dbFallback, Long time, TimeUnit unit) {
        RMap<String, String> map = redissonClient.getMap(key);
        if (map != null && !map.isEmpty()) {
            Map<String, T> hashmap = new HashMap<>();
            map.forEach((hashKey, value) ->
                    hashmap.put(hashKey, JSONUtil.toBean(value, type))
            );
            return hashmap;
        }
        Map<String, T> resultMap = dbFallback.get();
        if (resultMap == null) {
            return null;
        }
        Map<String, String> jsonMap = new HashMap<>();
        resultMap.forEach((k, v) -> jsonMap.put(k, JSONUtil.toJsonStr(v)));
        map.putAll(jsonMap);
        if (time != null && unit != null) {
            map.expire(time, unit);
        }
        return resultMap;
    }

    // 缓存雪崩


    // 缓存穿透



    // 缓存击穿



}
