package com.rocket.summer.framework.boot.autoconfigure.data.redis;

import redis.clients.jedis.Jedis;

import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureAfter;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.annotation.Import;
import com.rocket.summer.framework.data.redis.repository.configuration.EnableRedisRepositories;
import com.rocket.summer.framework.data.redis.repository.support.RedisRepositoryFactoryBean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Redis
 * Repositories.
 *
 * @author Eddú Meléndez
 * @see EnableRedisRepositories
 * @since 1.4.0
 */
@Configuration
@ConditionalOnClass({ Jedis.class, EnableRedisRepositories.class })
@ConditionalOnProperty(prefix = "spring.data.redis.repositories", name = "enabled",
        havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(RedisRepositoryFactoryBean.class)
@Import(RedisRepositoriesAutoConfigureRegistrar.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisRepositoriesAutoConfiguration {

}

