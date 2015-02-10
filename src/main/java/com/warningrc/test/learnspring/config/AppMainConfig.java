package com.warningrc.test.learnspring.config;

import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 应用主配置信息类.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 *         <b>date</b>: 2015-2-9
 */
@Configuration
@ComponentScan(basePackages = {"com.warningrc.test.learnspring.module"},
        nameGenerator = AnnotationBeanNameGenerator.class, scopedProxy = ScopedProxyMode.INTERFACES,
        excludeFilters = {@Filter({Controller.class, ControllerAdvice.class})})
@EnableAspectJAutoProxy
@EnableScheduling
@Import(value = {AppImportConfig.class})
public class AppMainConfig {}
