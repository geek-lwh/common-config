package com.aha.tech.anotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author: luweihong
 * @Date: 2019/11/28
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XEnv {
    String value() default "";
}
