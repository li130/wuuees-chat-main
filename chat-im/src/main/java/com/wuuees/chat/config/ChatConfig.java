package com.wuuees.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wuuees-chat")
public class ChatConfig {

    /**
     * 服务端口
     */
    private int websocketPort;

    /**
     * boss线程数
     */
    private int bossThreadSize;

    /**
     * work线程数
     */
    private int workThreadSize;

    /**
     * 心跳时间
     */
    private Long heartBeatTime;
}
