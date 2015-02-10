package com.warningrc.test.learnspring.config;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.MediaType;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * 应用主配置信息类.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 *         <b>date</b>: 2015-2-9
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"com.warningrc.test.learnspring.module"}, useDefaultFilters = false,
        scopedProxy = ScopedProxyMode.TARGET_CLASS,
        includeFilters = {@Filter({Controller.class, ControllerAdvice.class})})
@EnableWebMvc
@Import({WebAppVelocityConfig.class})
public class WebAppMainConfig extends WebMvcConfigurerAdapter {
    @Value("UTF-8")
    private Charset utf8;

    @Value("application/json;charset=UTF-8")
    private MediaType applicationJson;
    @Value("text/plain")
    private MediaType testPlain;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(createMappingJackson2HttpMessageConverter());
    }

    @Bean(name = "org.springframework.http.converter.BufferedImageHttpMessageConverter")
    public BufferedImageHttpMessageConverter createBufferedImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    @Bean(name = "org.springframework.http.converter.StringHttpMessageConverter")
    public StringHttpMessageConverter createStringHttpMessageConverter() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(utf8);
        stringHttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(new MediaType("*", "*", utf8)));
        return stringHttpMessageConverter;
    }

    @Bean(name = "jsonHttpMessageConverter")
    public MappingJackson2HttpMessageConverter createMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(createObjectMapper());
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(applicationJson, testPlain));
        return mappingJackson2HttpMessageConverter;
    }

    @Bean(name = "jsonObjectMapper")
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
