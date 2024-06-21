package com.wuuees.chat.holder.impl;

import com.wuuees.chat.common.constant.RedisKeyPrefix;
import com.wuuees.chat.common.model.RoomInfoDto;
import com.wuuees.chat.holder.IRoomHolder;
import com.wuuees.chat.redis.starter.RedisCacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RoomHolderImpl implements IRoomHolder {

    @Autowired
    private RedisCacheHelper redisCacheHelper;

    @Override
    public RoomInfoDto getRoomInfoById(String roomId) {
        RoomInfoDto roomInfo = redisCacheHelper.getCacheMapValue(RedisKeyPrefix.AQ_ROOM_PREFIX + roomId,
                RedisKeyPrefix.AQ_ROOM_INFO_PREFIX);
        if (null == roomInfo) {
            log.error("房间号{},获取房间信息不存在", roomId);
        }
        return roomInfo;
    }

    @Override
    public void removeRoomInfo(Integer roomNo) {
        String roomId = getRoomId(roomNo);
        redisCacheHelper.deleteObject(RedisKeyPrefix.AQ_ROOM_PREFIX + roomId);
        redisCacheHelper.deleteObject(RedisKeyPrefix.AQ_ROOM_NO_PREFIX + roomNo);
    }

    @Override
    public String getRoomId(Integer roomNo) {
        return redisCacheHelper.getCacheObject(RedisKeyPrefix.AQ_ROOM_NO_PREFIX + roomNo, String.class);
    }
}
