package com.lazy.msgtx.core.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lazy.msgtx.core.autoconfig.ProjectProperty;
import com.lazy.msgtx.core.common.SerializerType;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * SerializationFactory Definition
 * </p>
 *
 * @author laizhiyuan
 * @since 2018/12/14.
 */
@Slf4j
public final class SerializationFactory {


    private static Serialization SERIALIZATION;

    public static Serialization of() {

        if (SERIALIZATION == null) {
            synchronized (SerializationFactory.class) {
                if (SERIALIZATION == null) {
                    if (ProjectProperty.SELF.getSerializer() == SerializerType.GSON) {
                        SERIALIZATION = new GsonSerialization();
                    }
                    if (ProjectProperty.SELF.getSerializer() == SerializerType.FASTJSON) {
                        JSON.DEFAULT_GENERATE_FEATURE =
                                SerializerFeature.config(JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.SkipTransientField, false);
                        SERIALIZATION = new FastJsonSerialization();
                    }
                }
            }
        }

        return SERIALIZATION;
    }
}
