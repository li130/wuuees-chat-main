package com.wuuees.chat.common.constant;

public interface RedisKeyPrefix {

    String SYS_NAME = "wuueesChat:";

    /**
     * 用户登录信息前缀
     */
    String AQ_USER_INFO_PREFIX = SYS_NAME + ":userInfo:";

    /**
     * 房间缓存前缀
     */
    String AQ_ROOM_PREFIX = SYS_NAME + ":room:roomData:";

    /**
     * 房间信息缓存前缀
     */
    String AQ_ROOM_INFO_PREFIX = "info";

    /**
     * 房间号缓存前缀
     */
    String AQ_ROOM_NO_PREFIX = SYS_NAME + ":room:roomNo:";
}
