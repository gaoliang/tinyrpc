package me.gaoliang.tinyrpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class TinyRpcRequest implements Serializable {
    private String serviceVersion;
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
}
