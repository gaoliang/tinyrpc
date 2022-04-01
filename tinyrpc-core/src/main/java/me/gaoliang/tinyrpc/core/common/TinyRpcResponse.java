package me.gaoliang.tinyrpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class TinyRpcResponse implements Serializable {
    private Object data;
    private String message;
}
