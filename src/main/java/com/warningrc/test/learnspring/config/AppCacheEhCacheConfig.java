package com.warningrc.test.learnspring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@EnableCaching
public class AppCacheEhCacheConfig {
    @Value("classpath:config/ehcache.xml")
    private Resource configLocation;

    @Bean(name = "cacheManager")
    public CacheManager createCacheManager(net.sf.ehcache.CacheManager ehCacheManager) {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManager);
        return cacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean createEhCacheManager() {
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(configLocation);
        return ehCacheManagerFactoryBean;
    }
}
