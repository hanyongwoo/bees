package com.automation.abi.bees.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:global.properties")
public class ConfigUtil {
    private final Environment environment;

    public String getProperty(String key) {
        return environment.getProperty(key);
    }
}
