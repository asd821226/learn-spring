package com.warningrc.test.learnspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AppPropertiesConfig.class, AppDataSourceConfig.class, AppCacheEhCacheConfig.class})
public class AppImportConfig {}
