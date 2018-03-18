package com.zchi.common.redis;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by zchi on 2016/5/13.
 */
public interface MessageDelegate {
    void handleMessage(String message);
    void handleMessage(Map message); void handleMessage(byte[] message);
    void handleMessage(Serializable message);
    void handleMessage(Serializable message, String channel);
}
