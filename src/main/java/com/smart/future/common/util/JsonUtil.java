package com.smart.future.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.future.common.exception.SmartApplicationException;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {
    private static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, true);
    }

    public static String toString(Object object){
        String s = null;
        try {
            s = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static <T> T readValue(String src, Class<T> valueType) throws SmartApplicationException {
        try {
            return mapper.readValue(src, valueType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SmartApplicationException(12,"");
        }
    }

    public static <T> T readValueAsStream(InputStream src, Class<T> valueType) throws SmartApplicationException {
        try {
            return mapper.readValue(src, valueType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SmartApplicationException(12,"");
        }
    }

}
