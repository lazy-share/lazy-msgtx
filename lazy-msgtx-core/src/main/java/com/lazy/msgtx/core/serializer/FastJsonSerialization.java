package com.lazy.msgtx.core.serializer;

import com.alibaba.fastjson.JSON;

/**
 * <p>
 * fastjson
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
public class FastJsonSerialization implements Serialization {

    @Override
    public String serialize(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public Object deserialize(String json, Class<?> clazz) {
        return JSON.parseObject(json, clazz);
    }
}
