package com.proxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxy.db.config.Config;
import com.proxy.db.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan({"com.proxy"})
@PropertySource({"classpath:ui.properties", "classpath:db.properties"})
@Import({Config.class})
public class Context {
    @Autowired
    ProxyService proxyService;

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    @PostConstruct
    boolean cleaner() {
        proxyService.clearProcessedTable();
        return true;
    }

}
