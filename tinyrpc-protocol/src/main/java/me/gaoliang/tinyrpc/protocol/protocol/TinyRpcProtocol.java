package me.gaoliang.tinyrpc.protocol.protocol;

import lombok.Data;
import me.gaoliang.tinyrpc.protocol.protocol.MsgHeader;

import java.io.Serializable;

/**
 * +---------------------------------------------------------------+
 *
 * | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
 *
 * +---------------------------------------------------------------+
 *
 * | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
 *
 * +---------------------------------------------------------------+
 *
 * |                   数据内容 （长度不定）                          |
 *
 * +---------------------------------------------------------------+
 * @param <T>
 * @author gaoliang
 */
@Data
public class TinyRpcProtocol<T> implements Serializable {

    private MsgHeader header; // 协议头

    private T body; // 协议体
}