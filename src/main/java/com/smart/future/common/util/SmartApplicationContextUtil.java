package com.smart.future.common.util;

import org.springframework.context.ApplicationContext;

public class SmartApplicationContextUtil {
    private static ApplicationContext CONTEXT;

    public static void setCONTEXT(ApplicationContext context){
        CONTEXT = context;
    }

    public static  <T> T getBean(Class<T> c){
        return CONTEXT.getBean(c);
    }
}
