package me.gaoliang.tinyrpc.consumer.controller;

import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.consumer.annotion.TinyRpcReference;
import me.gaoliang.tinyrpc.facade.IHelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloController {
    @TinyRpcReference
    private IHelloService helloService;

    @GetMapping(value = "/hello")
    public String sayHello() {
        return helloService.hello("tinyrpc");
    }
}
