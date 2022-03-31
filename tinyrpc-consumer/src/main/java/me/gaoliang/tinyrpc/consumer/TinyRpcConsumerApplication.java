package me.gaoliang.tinyrpc.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class TinyRpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyRpcConsumerApplication.class, args);
    }
}
