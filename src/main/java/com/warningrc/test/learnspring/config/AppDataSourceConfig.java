package com.warningrc.test.learnspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath*:config/application-datasource.xml"})
public class AppDataSourceConfig {}
