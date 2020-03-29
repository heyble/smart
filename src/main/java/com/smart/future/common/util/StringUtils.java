package com.smart.future.common.util;

public class StringUtils {

    public static boolean isNullOrEmpty(String str){
        return str == null || str.isEmpty();
    }

    public static boolean nonNullOrEmpty(String str){
        return str != null && !str.isEmpty();
    }
}
