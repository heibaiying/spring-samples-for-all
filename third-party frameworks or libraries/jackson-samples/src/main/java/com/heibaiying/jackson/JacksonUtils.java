package com.heibaiying.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : heibaiying
 * @description : jackson 的使用
 */
public class JacksonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    /***
     * spring 对象转换为json
     */
    public static String objectToJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /***
     * spring json 转换为对象
     */
    public static <T> T jsonToBean(String json, Class<T> valueType) throws IOException {
        return mapper.readValue(json, valueType);
    }

    /***
     * spring json 转换为List
     */
    public static <T> List<T> jsonToList(String json, Class<T> valueType) throws IOException {
        List<Map<String, Object>> list = mapper.readValue(json, new TypeReference<List<T>>() {
        });
        return list.stream().map(value -> mapToBean(value, valueType)).collect(Collectors.toList());
    }

    /***
     * spring json 转换为Map (map的value为基本类型)
     */
    public static Map jsonToMap(String json) throws IOException {
        return mapper.readValue(json, Map.class);
    }

    /***
     * spring json 转换为Map (map的value为bean)
     */
    public static <T> Map<String, T> jsonToMap(String json, Class<T> clazz) throws IOException {
        Map<String, Map<String, Object>> map = mapper.readValue(json,
                new TypeReference<Map<String, T>>() {
                });
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), mapToBean(entry.getValue(), clazz));
        }
        return result;
    }

    /***
     *  map 转换为 bean
     */
    public static <T> T mapToBean(Map map, Class<T> valueType) {
        return mapper.convertValue(map, valueType);
    }
}
