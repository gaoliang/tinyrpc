package me.gaoliang.tinyrpc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.gaoliang.tinyrpc.core.common.TinyRpcRequest;
import me.gaoliang.tinyrpc.core.common.TinyRpcResponse;
import me.gaoliang.tinyrpc.protocol.protocol.MsgHeader;
import me.gaoliang.tinyrpc.protocol.protocol.MsgType;
import me.gaoliang.tinyrpc.protocol.protocol.ProtocolConstants;
import me.gaoliang.tinyrpc.protocol.protocol.TinyRpcProtocol;
import me.gaoliang.tinyrpc.protocol.serialization.RpcSerialization;
import me.gaoliang.tinyrpc.protocol.serialization.SerializationFactory;

import java.util.List;

public class TinyRpcDecoder extends ByteToMessageDecoder {

    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
    */
    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex();

        short magic = in.readShort();

        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);


        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);

        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(serializeType);
        switch (msgTypeEnum) {
            case REQUEST:
                TinyRpcRequest request = rpcSerialization.deserialize(data, TinyRpcRequest.class);
                if (request != null) {
                    TinyRpcProtocol<TinyRpcRequest> protocol = new TinyRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                TinyRpcResponse response = rpcSerialization.deserialize(data, TinyRpcResponse.class);
                if (response != null) {
                    TinyRpcProtocol<TinyRpcResponse> protocol = new TinyRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO
                break;
        }
    }
}
