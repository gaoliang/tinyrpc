package me.gaoliang.tinyrpc.core.common;

import io.netty.util.concurrent.Promise;
import lombok.Data;

@Data
public class TinyRpcFuture<T> {
    private Promise<T> promise;
    private long timeout;

    public TinyRpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}
