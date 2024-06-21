package com.wuuees.chat.codec;

import com.google.protobuf.Message;
import com.wuuees.chat.message.MessageRecognizer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 解码器
 * 消息长度（2位）+消息指令（2位）+消息内容
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class MessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

    @Autowired
    private MessageRecognizer messageRecognizer;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame msg,
                          List<Object> list) throws Exception {
        ByteBuf byteBuf = msg.content();
        if (byteBuf.readableBytes() < 4) {
            // 消息长度不够
            return;
        }
        // 在当前阅读位置设置标记
        byteBuf.markReaderIndex();
        int msgLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < msgLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        // 根据消息体获取消息指令
        short command = byteBuf.readShort();
        Message.Builder builder = messageRecognizer.getMsgBuilderByMsgCommand(command);
        if (builder == null) {
            log.error("无法识别的消息, command = {}", command);
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        builder.clear();
        builder.mergeFrom(msgBody);

        Message message = builder.build();
        if (message != null) {
            list.add(message);
        }
    }
}
