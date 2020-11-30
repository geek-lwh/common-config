package com.aha.tech.exception;


/**
 * 说明:异常类父类,用于抛出异常动态转型, 此abstract异常类不可构造使用
 *
 */
public interface GlobalException {

     /**
      * code
      * @return
      */
     Integer code();

     String message();
}
