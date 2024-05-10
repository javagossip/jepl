package io.github.jepl.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/** 扩展注解，作用于扩展点接口的实现类上用来标识是一个扩展点实现 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Extension {
    String key();
}
