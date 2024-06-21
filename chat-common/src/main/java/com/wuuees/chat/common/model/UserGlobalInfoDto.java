package com.wuuees.chat.common.model;

import lombok.Data;

import java.util.Date;

/**
 * 用户全局信息
 */
@Data
public class UserGlobalInfoDto {

    private String userId;

    private String userName;

    private String userAvatar;

    private String roomId;

    // 加入房间时间
    private Date joinRoomTime;
}
