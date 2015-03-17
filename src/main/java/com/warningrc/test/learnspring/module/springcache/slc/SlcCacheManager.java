package com.warningrc.test.learnspring.module.springcache.slc;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

/**
 * 二级缓存管理器.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 *         <b>date</b>: 2015-3-3
 */
public class SlcCacheManager extends AbstractTransactionSupportingCacheManager {

    /** slf4j Logger for SlcCacheManager. */
    private static final Logger logger = LoggerFactory.getLogger(SlcCacheManager.class);

    private CacheManager firstCacheManager;
    private CacheManager secondCacheManager;

    public SlcCacheManager(CacheManager firstCacheManager, CacheManager secondCacheManager) {
        Assert.notNull(firstCacheManager);
        Assert.notNull(secondCacheManager);
        this.firstCacheManager = firstCacheManager;
        this.secondCacheManager = secondCacheManager;
        logger.debug("初始化二级缓存管理器[一级缓存:{{}},二级缓存:{{}}]", firstCacheManager, secondCacheManager);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        Collection<String> cacheNames = firstCacheManager.getCacheNames();
        Collection<SlcCache> caches = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(cacheNames)) {
            for (String cacheName : cacheNames) {
                Cache firstCache = firstCacheManager.getCache(cacheName);
                Assert.notNull(firstCache, "Don't find first cache name for " + cacheName);
                Cache secondCache = secondCacheManager.getCache(cacheName);
                Assert.notNull(secondCache, "Don't find second cache name for " + cacheName);
                caches.add(new SlcCache(cacheName, firstCache, secondCache));
            }
        }
        return caches;
    }

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = super.getCache(cacheName);
        if (cache == null) {
            Cache firstCache = firstCacheManager.getCache(cacheName);
            Assert.notNull(firstCache, "Don't find first cache name for " + cacheName);
            Cache secondCache = secondCacheManager.getCache(cacheName);
            Assert.notNull(secondCache, "Don't find second cache name for " + cacheName);
            addCache(new SlcCache(cacheName, firstCache, secondCache));
            cache = super.getCache(cacheName);
        }
        return cache;
    }
}
