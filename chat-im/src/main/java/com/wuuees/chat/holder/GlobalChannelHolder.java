package com.wuuees.chat.holder;

import com.wuuees.chat.common.model.RoomInfoDto;
import com.wuuees.chat.message.MessageBroadcaster;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局channel持有者 管理和连接相关的channel和数据
 */
@Component
@Slf4j
public class GlobalChannelHolder {

    private final Map<String, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    @Autowired
    private MessageBroadcaster messageBroadcaster;

    @Autowired
    private IRoomHolder roomHolder;

    public void put(String userId, NioSocketChannel channel) {
        CHANNEL_MAP.put(userId, channel);
    }

    public NioSocketChannel getUserChannel(String userId) {
        return CHANNEL_MAP.get(userId);
    }

    public void remove(String userId) {
        CHANNEL_MAP.remove(userId);
    }

    /**
     * 获取用户所在的房间号
     *
     * @param userId 用户id
     * @return 房间号
     */
    public String getRoomId(String userId) {
        return messageBroadcaster.getUserRoom(userId);
    }

    public RoomInfoDto getRoomInfo(String roomId) {
        return roomHolder.getRoomInfoById(roomId);
    }

    /**
     * 退出
     *
     * @param userId 用户id
     */
    public void logout(String userId) {
        NioSocketChannel nioSocketChannel = getUserChannel(userId);
        String roomId = messageBroadcaster.leaveRoom(userId, nioSocketChannel);
        dissolveTheRoomByLogout(roomId);
        remove(userId);
    }

    /**
     * 离线
     *
     * @param userId           用户id
     * @param nioSocketChannel channel
     */
    public void offline(String userId, NioSocketChannel nioSocketChannel) {
        remove(userId);
        messageBroadcaster.removeChannel(userId, nioSocketChannel);
    }

    /**
     * 创建房间
     *
     * @param roomId 房间id
     */
    public void createChannelGroup(String roomId) {
        DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        messageBroadcaster.addChannelGroup(roomId, channelGroup);
    }

    public void joinRoom(String roomId, String userId, Channel channel) {
        messageBroadcaster.joinRoom(roomId, userId, (NioSocketChannel) channel);
        // 添加用户所在的房间信息
        
    }

    public void dissolveTheRoomByLogout(String roomId) {
        // 获取房间信息
        RoomInfoDto roomInfo = getRoomInfo(roomId);
        if (roomInfo == null) {
            return;
        }
        isOrNoDissolveTheRoom(roomId, roomInfo.getRoomNo());
    }

    public void isOrNoDissolveTheRoom(String roomId, Integer roomNo) {
        ChannelGroup channelGroup = messageBroadcaster.getChannelGroup(roomId);
        if (!messageBroadcaster.isTheRoomEmpty(roomId) && (null == channelGroup || channelGroup.isEmpty())) {
            roomHolder.removeRoomInfo(roomNo);
            // 解散房间
            messageBroadcaster.removeChannelGroup(roomId);
        }
    }
}
