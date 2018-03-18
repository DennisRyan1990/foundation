package com.zchi.common.redis;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Created by zchi on 2016/5/13.
 */
public class RedisMessageListenerRegistration {
    MessageListenerAdapter listenerAdapter ;
    ChannelTopic channelTopic;

    public MessageListenerAdapter getListenerAdapter() {
        return listenerAdapter;
    }

    public void setListenerAdapter(MessageListenerAdapter listenerAdapter) {
        this.listenerAdapter = listenerAdapter;
    }

    public ChannelTopic getChannelTopic() {
        return channelTopic;
    }

    public void setChannelTopic(ChannelTopic channelTopic) {
        this.channelTopic = channelTopic;
    }

    public RedisMessageListenerRegistration(MessageListenerAdapter listenerAdapter,
        ChannelTopic channelTopic) {
        this.listenerAdapter = listenerAdapter;
        this.channelTopic = channelTopic;
    }


    public RedisMessageListenerRegistration() {
    }
}
