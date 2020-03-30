package com.smart.future.common.config;

import com.smart.future.common.util.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    public IdGenerator idGenerator(){
        return new IdGenerator();
    }
}
