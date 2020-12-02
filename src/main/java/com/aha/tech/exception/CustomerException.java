package com.aha.tech.exception;


/**
 * 自定义异常
 *
 */
public interface CustomerException {

     /**
      * code
      * @return
      */
     Integer replaceCode(int code);

     String message();
}
