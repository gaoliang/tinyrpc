package me.gaoliang.tinyrpc.core.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求 holder，调用和返回是异步的，需要存储下 请求和响应 Future 的关系
 */
public class TinyRpcRequestHolder {

    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static final Map<Long, TinyRpcFuture<TinyRpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
