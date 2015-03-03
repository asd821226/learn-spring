package com.warningrc.test.learnspring.module.springcache.slc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import com.google.common.collect.Lists;

/**
 * 二级缓存.
 * <p>
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 *         <b>date</b>: 2015-3-3
 */
public class SlcCache implements Cache {

    /** slf4j Logger for SlcCache. */
    private static final Logger logger = LoggerFactory.getLogger(SlcCache.class);

    private Cache firstCache, secondCache;
    private String name;

    public SlcCache(String name, Cache fCache, Cache sCache) {
        this.name = name;
        this.firstCache = fCache;
        this.secondCache = sCache;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return Lists.newArrayList(firstCache, secondCache);
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = firstCache.get(key);
        if (valueWrapper == null) {
            valueWrapper = secondCache.get(key);
            if (valueWrapper != null) {
                logger.debug("从二级缓存中获取对象[key:{}]", key);
                firstCache.put(key, valueWrapper.get());
                logger.debug("将对象[key:{}]放入一级缓存中", key);
            }
        } else {
            logger.debug("从一级缓存中获取对象[key:{}]", key);
        }
        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = firstCache.get(key, type);
        if (value == null) {
            value = secondCache.get(key, type);
            if (value != null) {
                logger.debug("从二级缓存中获取对象[key:{}]", key);
                firstCache.put(key, value);
                logger.debug("将对象[key:{}]放入一级缓存中", key);
            }
        } else {
            logger.debug("从一级缓存中获取对象[key:{}]", key);
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        secondCache.put(key, value);
        logger.debug("将对象[key:{},value:{}]放入二级缓存中", key, value);
        firstCache.put(key, value);
        logger.debug("将对象[key:{},value:{}]放入一级缓存中", key, value);
    }

    @Override
    public void evict(Object key) {
        firstCache.evict(key);
        logger.debug("将对象[key:{}]从一级缓存中删除", key);
        secondCache.evict(key);
        logger.debug("将对象[key:{}]从二级缓存中删除", key);
    }

    @Override
    public void clear() {
        firstCache.clear();
        logger.debug("清空一级缓存");
        secondCache.clear();
        logger.debug("清空二级缓存");
    }
}
