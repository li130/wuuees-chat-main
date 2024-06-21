package com.wuuees.chat.handler;

import com.wuuees.chat.common.constant.BusinessConstant;
import com.wuuees.chat.config.ChatConfig;
import com.wuuees.chat.holder.GlobalChannelHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 心跳处理器
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class HearBeatHandler extends ChannelInboundHandlerAdapter implements InitializingBean {

    /**
     * 心跳间隔时间
     */
    private Long heartBeatTime = 5000L;

    @Autowired
    private ChatConfig catConfig;

    @Autowired
    private GlobalChannelHolder globalChannelHolder;

    private void init() {
        heartBeatTime = catConfig.getHeartBeatTime();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            if (event.state().equals(IdleState.ALL_IDLE)) {
                Long lastReadTime =
                        (Long) ctx.channel().attr(AttributeKey.valueOf(BusinessConstant.HEART_BEAT_TIME)).get();
                long now = System.currentTimeMillis();
                if (lastReadTime == null || now - lastReadTime > heartBeatTime) {
                    String userId = (String) ctx.channel().attr(AttributeKey.valueOf(BusinessConstant.USER_ID)).get();
                    if (userId == null) {
                        return;
                    }
                    // 下线
                    log.info("用户{}心跳超时，发送离线消息", userId);
                    // todo 离线消息

                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
        log.info("HearBeatHandler init Success, heartBeatTime: {}ms", heartBeatTime);
    }
}
