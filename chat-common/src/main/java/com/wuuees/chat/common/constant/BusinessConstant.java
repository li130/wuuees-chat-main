package com.wuuees.chat.common.constant;

public interface BusinessConstant {

    /**
     * userId
     */
    String USER_ID = "userId";

    /**
     * 心跳时间
     */
    String HEART_BEAT_TIME = "heartBeatTime";

    /**
     * 用户信息缓存时长 单位秒 24小时
     */
    long USER_INFO_CACHE_TIME = 60 * 60 * 24;
}
