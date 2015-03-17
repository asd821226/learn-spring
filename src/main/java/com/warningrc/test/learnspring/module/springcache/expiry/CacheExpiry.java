package com.warningrc.test.learnspring.module.springcache.expiry;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 缓存数据失效设定.
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br>
 *         <b>date</b>: 2015-3-10
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheExpiry {

    /**
     * 缓存数据失效时间.
     *
     * @return the long
     */
    long value();

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
