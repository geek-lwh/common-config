package com.aha.tech.anotation;

import java.lang.annotation.*;

/**
 * @Author: luweihong
 * @Date: 2019/6/10
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Authentication {
}
