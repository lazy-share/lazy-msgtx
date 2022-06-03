package com.lazy.msgtx.core.serializer;

import java.io.IOException;

/**
 * <p>
 * Serialization
 * This serializer component is referred from dubbo
 * </p>
 *
 * @author laizhiyuan
 * @since 2018/12/12.
 */
public interface Serialization {

    /**
     * 序列化
     *
     * @param obj
     * @return
     * @throws IOException
     */
    String serialize(Object obj);

    /**
     * 反序列化
     *
     * @param json
     * @param clazz
     * @return
     * @throws IOException
     */
    Object deserialize(String json, Class<?> clazz);
}
