package me.gaoliang.tinyrpc.provider.annotion;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
// 本质上是一个 Spring 管理的 Bean
@Component
public @interface TinyRpcService {
    Class<?> serviceInterface() default Object.class;

    String serviceVersion() default "1.0";
}
