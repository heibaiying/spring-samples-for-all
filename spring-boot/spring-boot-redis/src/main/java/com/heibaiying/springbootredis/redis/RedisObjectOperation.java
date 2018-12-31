package com.heibaiying.springbootredis.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author : heibaiying
 * @description : redis 基本操作
 */

@Component
public class RedisObjectOperation {

    @Autowired
    private RedisTemplate<Object, Object> objectRedisTemplate;

    /***
     * 操作对象
     */
    public void ObjectSet(Object key, Object value) {
        ValueOperations<Object, Object> valueOperations = objectRedisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    /***
     * 操作元素为对象列表
     */
    public void ListSet(Object key, List<Object> values) {
        ListOperations<Object, Object> listOperations = objectRedisTemplate.opsForList();
        values.forEach(value -> listOperations.leftPush(key, value));
    }

    /***
     * 操作元素为对象集合
     */
    public void SetSet(Object key, Set<Object> values) {
        SetOperations<Object, Object> setOperations = objectRedisTemplate.opsForSet();
        values.forEach(value -> setOperations.add(key, value));
    }

    /***
     * 获取对象
     */
    public Object ObjectGet(Object key) {
        ValueOperations<Object, Object> valueOperations = objectRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    /***
     * 列表弹出元素
     */
    public Object ListLeftPop(Object key) {
        ListOperations<Object, Object> listOperations = objectRedisTemplate.opsForList();
        return listOperations.leftPop(key, 2, TimeUnit.SECONDS);
    }

    /***
     * 集合弹出元素
     */
    public Object SetPop(Object key) {
        SetOperations<Object, Object> setOperations = objectRedisTemplate.opsForSet();
        return setOperations.pop(key);
    }

}
