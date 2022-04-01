package me.gaoliang.tinyrpc.provider.impl;

import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.facade.IHelloService;
import me.gaoliang.tinyrpc.provider.annotion.TinyRpcService;

@Slf4j
@TinyRpcService(serviceInterface = IHelloService.class, serviceVersion = "1.0")
public class HelloServiceImpl implements IHelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
