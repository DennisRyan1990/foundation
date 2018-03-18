package com.zchi.common.conf;

import com.zchi.common.redis.JedisLock;
import com.zchi.common.redis.RedisMessageListenerRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.Jedis;

import java.util.Map;

@SuppressWarnings({"unused","rawtypes"})
@Configuration @ConditionalOnClass({JedisConnection.class, RedisOperations.class, Jedis.class})
public class RedisConfig {
    @Autowired ApplicationContext context;
    @Autowired private Environment env;

    /**
     * @param connectionFactory 由spring boot 管理生成
     * @return
     * @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
     */
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        Map<String, RedisMessageListenerRegistration> redisMessageListenerRegistrationMap =
            context.getBeansOfType(RedisMessageListenerRegistration.class);
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        redisMessageListenerRegistrationMap.forEach(
            (a, b) -> container.addMessageListener(b.getListenerAdapter(), b.getChannelTopic()));
        return container;
    }

	@Bean public RedisScript setAndExpireScript() {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<Boolean>();
        redisScript.setScriptSource(
            new ResourceScriptSource(context.getResource("classpath:script/set_and_expire.lua")));
        redisScript.setResultType(null);
        return redisScript;
    }

    @Bean public RedisScript getAndDel() {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(
            new ResourceScriptSource(context.getResource("classpath:script/get_and_del.lua")));
        redisScript.setResultType(String.class);
        return redisScript;
    }

    @Bean public RedisScript setnxAndExpire() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
        redisScript.setScriptSource(
            new ResourceScriptSource(context.getResource("classpath:script/setnx_and_expire.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    @Bean @Scope("prototype") public JedisLock jedisLock() {
        JedisLock lock = new JedisLock();
        return lock;
    }

}
