package com.aha.tech.annotation;

import java.lang.annotation.*;

/**
 * @Author: luweihong
 * @Date: 2020/11/11
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trace {
}
