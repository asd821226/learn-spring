package com.warningrc.test.learnspring.module.beans.factory;

import java.util.Date;

import org.springframework.context.annotation.Bean;

public class ConfigurationClassEnhancerTest {
    @Bean
    public Date a() {
        System.out.println(1);
        return new Date();
    }

    public void b() {
        a();
    }

    public void c() {
        a();
    }


    public void d() {
        b();
        c();
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
        Class<?> clz = enhancer.enhance(ConfigurationClassEnhancerTest.class);
        ConfigurationClassEnhancerTest test = (ConfigurationClassEnhancerTest) clz.newInstance();
        test.d();
    }
}
