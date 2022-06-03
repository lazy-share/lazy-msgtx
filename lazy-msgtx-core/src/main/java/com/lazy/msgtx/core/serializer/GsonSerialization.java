package com.lazy.msgtx.core.serializer;

import com.google.gson.Gson;

/**
 * <p>
 * gson
 * </p>
 *
 * @author lzy
 * @since 2022/6/3.
 */
public class GsonSerialization implements Serialization {

    @Override
    public String serialize(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    @Override
    public Object deserialize(String json, Class<?> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }
}
