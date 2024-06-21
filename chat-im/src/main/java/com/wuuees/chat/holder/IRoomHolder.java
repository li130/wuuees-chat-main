package com.wuuees.chat.holder;

import com.wuuees.chat.common.model.RoomInfoDto;

public interface IRoomHolder {

    /**
     * 根据房间号获取房间信息
     *
     * @param roomId 房间id
     * @return 房间信息
     */
    RoomInfoDto getRoomInfoById(String roomId);

    /**
     * 删除房间信息
     *
     * @param roomNo 房间号
     */
    void removeRoomInfo(Integer roomNo);
}
