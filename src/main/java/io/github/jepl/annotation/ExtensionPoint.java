package io.github.jepl.annotation;

import java.lang.annotation.*;

/**
 * 扩展点注解，作用于接口上标识这个接口是一个扩展点
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExtensionPoint {
}
