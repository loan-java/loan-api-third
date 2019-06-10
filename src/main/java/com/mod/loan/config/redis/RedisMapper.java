package com.mod.loan.config.redis;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisMapper {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    }

    @Autowired
    StringRedisTemplate redisTemplate;

    public void expire(String key, long exp) {
        redisTemplate.expire(key, exp, TimeUnit.SECONDS);
    }


    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }


    public void remove(String key) {
        redisTemplate.delete(key);
    }

    public void removeKeyPrev(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        redisTemplate.delete(keys);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, serializer(value));
    }

    public void set(String key, Object value, long exp) {
        redisTemplate.opsForValue().set(key, serializer(value), exp, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void lpush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, serializer(value));
    }

    public String lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public <T> T lpop(String key, TypeReference<T> t) {
        return deserializer(redisTemplate.opsForList().leftPop(key), t);
    }

    public <T> T get(String key, TypeReference<T> t) {
        return deserializer(redisTemplate.opsForValue().get(key), t);
    }

    public boolean lock(String key, long expire) {
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(key, key);
        if (lock) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return lock;
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }

    private String serializer(Object value) {
        try {
            if (value instanceof String) {
                return (String) value;
            }
            return mapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new RuntimeException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    public long increment(String key, long delta, long exp) {
        Long current = redisTemplate.opsForValue().increment(key, delta);
        redisTemplate.expire(key, exp, TimeUnit.SECONDS);
        return current;
    }

    private <T> T deserializer(String value, TypeReference<T> t) {
        try {
            return value == null ? null : mapper.readValue(value, t);
        } catch (Exception e) {
            throw new RuntimeException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    public String getOrderUserKey(String orderNo, String source) {
       return "ORDER_USER:" +orderNo + source;
    }

}
