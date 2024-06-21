package com.wuuees.chat.message;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息广播器
 */
@Component
public class MessageBroadcaster {

    /**
     * 用户对应的房间
     */
    private final Map<String, String> userRoomMap = new ConcurrentHashMap<>();

    /**
     * 房间号 -> channelGroup
     * 管理房间中的所有channel
     */
    private final Map<String, ChannelGroup> channelGroupMap = new ConcurrentHashMap<>();

    public void addChannelGroup(String roomId, ChannelGroup channelGroup) {
        channelGroupMap.put(roomId, channelGroup);
    }

    public ChannelGroup getChannelGroup(String roomId) {
        return channelGroupMap.get(roomId);
    }

    public void removeChannelGroup(String roomId) {
        channelGroupMap.remove(roomId);
    }

    /**
     * 获取用户所在的房间号
     *
     * @param userId 用户id
     * @return 房间号
     */
    public String getUserRoom(String userId) {
        return userRoomMap.get(userId);
    }

    /**
     * 加入房间
     */
    public void joinRoom(String roomId, String userId, NioSocketChannel channel) {
        if (roomId == null || userId == null || channel == null) {
            return;
        }
        userRoomMap.put(userId, roomId);
        ChannelGroup channelGroup = channelGroupMap.get(roomId);
        if (channelGroup == null) {
            return;
        }
        channelGroup.add(channel);
    }

    /**
     * 离开房间
     */
    public String leaveRoom(String userId, NioSocketChannel channel) {
        if (userId == null || channel == null) {
            return null;
        }
        String roomId = userRoomMap.get(userId);
        if (roomId == null) {
            return null;
        }
        ChannelGroup channelGroup = channelGroupMap.get(roomId);
        if (channelGroup == null) {
            return null;
        }
        channelGroup.remove(channel);
        return roomId;
    }

    /**
     * 广播消息
     */
    public <T extends GeneratedMessageV3> void broadcast(String roomId, T msg) {
        ChannelGroup channelGroup = channelGroupMap.get(roomId);
        if (channelGroup == null) {
            return;
        }
        channelGroup.writeAndFlush(msg);
    }

    public void removeChannel(String userId, NioSocketChannel channel) {
        if (channel == null) {
            return;
        }
        String roomId = userRoomMap.get(userId);
        if (roomId == null) {
            return;
        }
        ChannelGroup channelGroup = channelGroupMap.get(roomId);
        if (channelGroup == null) {
            return;
        }
        channelGroup.remove(channel);
    }

    public boolean isTheRoomEmpty(String roomId) {
        return userRoomMap.containsKey(roomId);
    }

    public void remove(String userId) {
        userRoomMap.remove(userId);
    }
}
