package com.warningrc.test.learnspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

/**
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 *         <b>date</b>: 2015-2-9
 */
@Configuration
public class WebAppVelocityConfig {
    @Bean
    public VelocityViewResolver createVelocityViewResolver() {
        VelocityViewResolver velocityViewResolver = new VelocityViewResolver();
        velocityViewResolver.setSuffix(".vm");
        velocityViewResolver.setCache(false);
        return velocityViewResolver;
    }

    @Bean(name = "velocityConfigurer")
    public VelocityConfigurer createVelocityConfigurer() {
        VelocityConfigurer velocityConfigurer = new VelocityConfigurer();
        velocityConfigurer.setResourceLoaderPath("/WEB-INF/vm/");
        return velocityConfigurer;
    }
}
