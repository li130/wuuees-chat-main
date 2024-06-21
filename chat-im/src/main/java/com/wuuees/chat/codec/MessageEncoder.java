package com.wuuees.chat.codec;

import com.google.protobuf.GeneratedMessageV3;
import com.wuuees.chat.message.MessageRecognizer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToMessageEncoder<GeneratedMessageV3> {

    @Autowired
    private MessageRecognizer messageRecognizer;

    @Override
    protected void encode(ChannelHandlerContext ctx, GeneratedMessageV3 msg,
                          List<Object> list) throws Exception {
        int msgCommand = messageRecognizer.getMsgCommandByMsgClazz(msg.getClass());
        if (msgCommand < 0) {
            log.error("无法识别的消息, msgClazz = {}", msg.getClass().getSimpleName());
            return;
        }
        log.info("返回消息编码器: msgClazz = {}, msgCommand = {}", msg.getClass().getSimpleName(), msgCommand);
        byte[] msgBody = msg.toByteArray();
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeInt(msgBody.length);
        byteBuf.writeShort((short) msgCommand);
        byteBuf.writeBytes(msgBody);
        list.add(new BinaryWebSocketFrame(byteBuf));
    }
}
