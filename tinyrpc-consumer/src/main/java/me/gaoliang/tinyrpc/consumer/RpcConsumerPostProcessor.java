package me.gaoliang.tinyrpc.consumer;

import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.consumer.annotion.TinyRpcReference;
import me.gaoliang.tinyrpc.core.RpcConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gaoliang
 */
@Slf4j
@Component
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {
    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> rpcRefBeanDefinitions = new LinkedHashMap<>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 从 beanFactory 中获取所有 Bean 的定义信息，然后分别对每个 Bean 的所有 field 进行检测。
     * 如果 field 被声明了 @RpcReference 注解，通过 BeanDefinitionBuilder 构造 TinyRpcProxyFactoryBean 的定义,
     * 并为 TinyRpcProxyFactoryBean 的成员变量赋值，包括服务类型 interfaceClass、服务版本 serviceVersion、
     * 注册中心类型 registryType、注册中心地址 registryAddress 以及超时时间 timeout。
     *
     * 构造完 TinyRpcProxyFactoryBean 的定义之后，将 TinyRpcProxyFactoryBean 的 BeanDefinition 重新注册到 Spring 容器中。
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 获取所有的 bean definition，遍历
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassname = beanDefinition.getBeanClassName();
            if (beanClassname != null) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassname, this.classLoader);
                // 处理其中所有标记了  RpcReference 的字段，加入到 rpcRefBeanDefinitions map 中。
                ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
            }
        }
        // 所有的 @Ref 的注解字段都已经处理成 TinyRpcReferenceBean 放到 map 中了。
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;

        // 把这些 Bean Definition 放到 IoC 容器中
        this.rpcRefBeanDefinitions.forEach((beanName, beanDefinition) -> {
            if (applicationContext.containsBean(beanName)) {
                throw new IllegalStateException("spring context already has a bean named" + beanName);
            }
            beanDefinitionRegistry.registerBeanDefinition(beanName, rpcRefBeanDefinitions.get(beanName));
            log.info("[tinyrpc] Registered RpcReference Bean {} success", beanName);
        });
    }

    private void parseRpcReference(Field field) {
        TinyRpcReference annotation = AnnotationUtils.getAnnotation(field, TinyRpcReference.class);
        if (annotation == null) {
            return;
        }

        log.info("[tinyrpc] Found rpc reference {} ", field.getName());
        BeanDefinition beanDefinition =
                // 这里注入的是 factory bean，通过 factory bean 来生成具体的代理对象。
                BeanDefinitionBuilder.genericBeanDefinition(TinyRpcProxyFactoryBean.class)
                        .setInitMethodName(RpcConstants.INIT_METHOD_NAME)
                        .addPropertyValue("interfaceClass", field.getType())
                        .addPropertyValue("serviceVersion", annotation.serviceVersion())
                        .addPropertyValue("registryType", annotation.registryType())
                        .addPropertyValue("registryAddress", annotation.registryAddress())
                        .addPropertyValue("timeout", annotation.timeout())
                        .getBeanDefinition();

        rpcRefBeanDefinitions.put(field.getName(), beanDefinition);
    }
}
