package com.wuuees.chat.message;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.wuuees.chat.common.enums.MsgCommandEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
@Component
@Slf4j
public class MessageRecognizer implements InitializingBean {

    /**
     * 指令编号和消息体字典
     */
    private final Map<Integer, GeneratedMessageV3> msgCommandAndMsgBodyMap = new HashMap<>();

    /**
     * 消息类型和消息指令编号字典
     */
    private final Map<Class<?>, Integer> msgClazzAndMsgCommandMap = new HashMap<>();

    /**
     * 初始化
     */
    public void init() {
        // 获取所有的内部类
        Class<?>[] innerClazzArray = ChatMsgProtocol.class.getDeclaredClasses();
        for (Class<?> innerClazz : innerClazzArray) {
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                // 如果不是消息,
                continue;
            }
            // 获取类名称并转成小写
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();
            // 接下来遍历 MsgCode 枚举
            for (MsgCommandEnum msgCommand : MsgCommandEnum.values()) {
                String strMsgCode = msgCommand.getName();
                strMsgCode = strMsgCode.replaceAll("_", "");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.equals(clazzName)) {
                    continue;
                }
                try {
                    // 调用 XxxCmd  的 getDefaultInstance 静态方法,
                    // 目的是返回默认实例
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    log.info("recognizer Initialization relation::class:{} =>command:{}", innerClazz.getName(),
                            msgCommand.getCode());
                    // 关联消息编号与消息体
                    msgCommandAndMsgBodyMap.put(Integer.valueOf(msgCommand.getCode()), (GeneratedMessageV3) returnObj);
                    // 关联消息类与消息编号
                    msgClazzAndMsgCommandMap.put(innerClazz, Integer.valueOf(msgCommand.getCode()));
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * 根据消息体获取消息指令
     *
     * @param msgCommand 消息指令
     * @return 消息Builder
     */
    public Message.Builder getMsgBuilderByMsgCommand(int msgCommand) {
        if (msgCommand < 0) {
            return null;
        }
        GeneratedMessageV3 msg = msgCommandAndMsgBodyMap.get(msgCommand);
        if (msg == null) {
            return null;
        }
        return msg.newBuilderForType();
    }

    /**
     * 根据消息体获取消息指令
     *
     * @param msgClazz 消息类型
     * @return 消息指令
     */
    public int getMsgCommandByMsgClazz(Class<?> msgClazz) {
        if (msgClazz == null) {
            return -1;
        }
        Integer command = msgClazzAndMsgCommandMap.get(msgClazz);
        if (command == null) {
            return -1;
        }
        return command;
    }

    @Override

    public void afterPropertiesSet() throws Exception {
        init();
    }
}
