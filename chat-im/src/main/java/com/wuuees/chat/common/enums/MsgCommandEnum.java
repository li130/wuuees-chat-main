package com.wuuees.chat.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息命令枚举
 */
@Getter
@AllArgsConstructor
public enum MsgCommandEnum implements StringEnum {

    // 登录
    USER_LOGIN_CMD("1", "USER_LOGIN_CMD"),
    // 登录响应
    USER_LOGIN_ACK("2", "USER_LOGIN_ACK"),
    // 心跳
    HEART_BEAT_CMD("3", "HEART_BEAT_CMD"),
    // 心跳响应
    HEART_BEAT_ACK("4", "HEART_BEAT_ACK"),
    ;

    private final String code;

    private final String name;

    @Override
    public String toString() {
        return code;
    }
}

