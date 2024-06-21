package com.wuuees.chat.common.model;

import lombok.Data;

import java.util.List;

/**
 * 房间缓存信息
 */
@Data
public class RoomInfoDto {

    private String roomId;

    private Integer roomNo;

    private String roomName;

    // 是否支持历史消息 （0-不支持，1-支持）
    private int history;

    private List<UserGlobalInfoDto> roomMemberList;

}
