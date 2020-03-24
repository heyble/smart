package com.smart.future;

import com.smart.future.common.util.SmartApplicationContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.smart.future.dao")
public class FutureApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(FutureApplication.class, args);
        SmartApplicationContextUtil.setCONTEXT(applicationContext);
    }

}
