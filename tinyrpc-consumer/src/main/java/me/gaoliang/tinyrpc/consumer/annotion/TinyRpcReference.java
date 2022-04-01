package me.gaoliang.tinyrpc.consumer.annotion;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gaoliang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
// 组合注解，用于代理类的自动注入
@Autowired
public @interface TinyRpcReference {
    String serviceVersion() default "1.0";
    String registryType() default "ZOOKEEPER";
    String registryAddress() default "127.0.0.1:2181";
    long timeout() default 5000;
}
