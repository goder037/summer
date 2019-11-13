package com.rocket.summer.framework.data.redis.connection.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.data.redis.core.types.RedisClientInfo;
import com.rocket.summer.framework.data.redis.core.types.RedisClientInfo.RedisClientInfoBuilder;

/**
 * {@link Converter} implementation to create one {@link RedisClientInfo} per line entry in given {@link String} array.
 *
 * <pre>
 * ## sample of single line
 * addr=127.0.0.1:60311 fd=6 name= age=4059 idle=0 flags=N db=0 sub=0 psub=0 multi=-1 qbuf=0 qbuf-free=32768 obl=0 oll=0 omem=0 events=r cmd=client
 * </pre>
 *
 * @author Christoph Strobl
 * @since 1.3
 */
public class StringToRedisClientInfoConverter implements Converter<String[], List<RedisClientInfo>> {

    @Override
    public List<RedisClientInfo> convert(String[] lines) {

        if (lines == null) {
            return Collections.emptyList();
        }
        List<RedisClientInfo> infos = new ArrayList<RedisClientInfo>(lines.length);
        for (String line : lines) {
            infos.add(RedisClientInfoBuilder.fromString(line));
        }
        return infos;
    }

}

