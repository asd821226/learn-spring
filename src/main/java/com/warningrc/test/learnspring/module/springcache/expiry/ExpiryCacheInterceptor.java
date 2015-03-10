package com.warningrc.test.learnspring.module.springcache.expiry;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.cache.interceptor.CompositeCacheOperationSource;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.MethodCacheKey;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class ExpiryCacheInterceptor implements MethodInterceptor, InitializingBean, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Logger for ExpiryCacheInterceptor
     */
    private static final Logger logger = LoggerFactory.getLogger(ExpiryCacheInterceptor.class);

    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    private CacheManager cacheManager;

    private CacheOperationSource cacheOperationSource;

    private KeyGenerator keyGenerator = new SimpleKeyGenerator();

    private boolean initialized = false;



    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Invoker aopAllianceInvoker = new Invoker() {
            @Override
            public Object invoke() {
                try {
                    return invocation.proceed();
                } catch (Throwable ex) {
                    throw new ThrowableWrapper(ex);
                }
            }
        };

        try {
            return execute(aopAllianceInvoker, invocation.getThis(), invocation.getMethod(), invocation.getArguments());
        } catch (ThrowableWrapper th) {
            throw th.original;
        }
    }

    private CacheExpiry getExpiry(Method method, Class<?> targetClass) {
        CacheExpiry expiry = method.getAnnotation(CacheExpiry.class);
        if (expiry == null) {
            try {
                method = targetClass.getMethod(method.getName(), method.getParameterTypes());
                expiry = method.getAnnotation(CacheExpiry.class);
            } catch (Exception e) {
            }
        }
        return expiry;
    }

    private static class ThrowableWrapper extends RuntimeException {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private final Throwable original;

        ThrowableWrapper(Throwable original) {
            this.original = original;
        }
    }

    /**
     * Set the CacheManager that this cache aspect should delegate to.
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Return the CacheManager that this cache aspect delegates to.
     */
    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    /**
     * Set one or more cache operation sources which are used to find the cache attributes. If more
     * than one source is provided, they will be aggregated using a
     * {@link CompositeCacheOperationSource}.
     * 
     * @param cacheOperationSources must not be {@code null}
     */
    public void setCacheOperationSources(CacheOperationSource... cacheOperationSources) {
        Assert.notEmpty(cacheOperationSources, "At least 1 CacheOperationSource needs to be specified");
        this.cacheOperationSource =
                (cacheOperationSources.length > 1 ? new CompositeCacheOperationSource(cacheOperationSources)
                        : cacheOperationSources[0]);
    }

    /**
     * Return the CacheOperationSource for this cache aspect.
     */
    public CacheOperationSource getCacheOperationSource() {
        return this.cacheOperationSource;
    }

    /**
     * Set the KeyGenerator for this cache aspect. The default is a {@link SimpleKeyGenerator}.
     */
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    /**
     * Return the KeyGenerator for this cache aspect,
     */
    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }

    public void afterPropertiesSet() {
        Assert.state(this.cacheManager != null, "Property 'cacheManager' is required");
        Assert.state(this.cacheOperationSource != null, "Property 'cacheOperationSources' is required: "
                + "If there are no cacheable methods, then don't use a cache aspect.");
        this.initialized = true;
    }


    /**
     * Convenience method to return a String representation of this Method for use in logging. Can
     * be overridden in subclasses to provide a different identifier for the given method.
     * 
     * @param method the method we're interested in
     * @param targetClass class the method is on
     * @return log message identifying this method
     * @see org.springframework.util.ClassUtils#getQualifiedMethodName
     */
    protected String methodIdentification(Method method, Class<?> targetClass) {
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        return ClassUtils.getQualifiedMethodName(specificMethod);
    }

    protected Collection<? extends Cache> getCaches(CacheOperation operation) {
        Set<String> cacheNames = operation.getCacheNames();
        Collection<Cache> caches = new ArrayList<Cache>(cacheNames.size());
        for (String cacheName : cacheNames) {
            Cache cache = this.cacheManager.getCache(cacheName);
            if (cache == null) {
                throw new IllegalArgumentException("Cannot find cache named '" + cacheName + "' for " + operation);
            }
            caches.add(cache);
        }
        return caches;
    }

    protected CacheOperationContext getOperationContext(CacheOperation operation, Method method, Object[] args,
            Object target, Class<?> targetClass) {

        return new CacheOperationContext(operation, method, args, target, targetClass);
    }

    protected Object execute(Invoker invoker, Object target, Method method, Object[] args) {
        // check whether aspect is enabled
        // to cope with cases where the AJ is pulled in automatically
        if (this.initialized) {
            Class<?> targetClass = getTargetClass(target);
            Collection<CacheOperation> operations = getCacheOperationSource().getCacheOperations(method, targetClass);
            if (!CollectionUtils.isEmpty(operations)) {
                return execute(invoker, new CacheOperationContexts(operations, method, args, target, targetClass));
            }
        }

        return invoker.invoke();
    }

    private Class<?> getTargetClass(Object target) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        if (targetClass == null && target != null) {
            targetClass = target.getClass();
        }
        return targetClass;
    }

    private Object execute(Invoker invoker, CacheOperationContexts contexts) {
        // Process any early evictions
        processCacheEvicts(contexts.get(CacheEvictOperation.class), true, ExpressionEvaluator.NO_RESULT);

        // Check if we have a cached item matching the conditions
        Cache.ValueWrapper cacheHit = findCachedItem(contexts.get(CacheableOperation.class));

        // Collect puts from any @Cacheable miss, if no cached item is found
        List<CachePutRequest> cachePutRequests = new LinkedList<CachePutRequest>();

        Object value = null;
        // If there are no put requests, just use the cache hit
        if (cachePutRequests.isEmpty() && contexts.get(CachePutOperation.class).isEmpty() && cacheHit != null) {
            value = cacheHit.get();
            if (value != null) {
                if (value instanceof ExpiryWrapper) {
                    ExpiryWrapper wrapper = (ExpiryWrapper) value;
                    if ((wrapper.getSaveTime() + wrapper.getExpiryTime()) <= System.currentTimeMillis()) {
                        logger.debug("find data from cache,but cache data has failure.[{}]", wrapper.getValue());
                        value = cacheHit = null;
                    } else {
                        logger.debug("find data from cache.[{}]", wrapper.getValue());
                    }
                } else {
                    logger.debug("find data from cache.[{}]", value);
                }
            }
        }
        if (cacheHit == null) {
            collectPutRequests(contexts.get(CacheableOperation.class), ExpressionEvaluator.NO_RESULT, cachePutRequests);
        }

        // Invoke the method if don't have a cache hit
        if (value == null) {
            value = invoker.invoke();
            if (value != null) {
                CacheExpiry expiry = getExpiry(contexts.getMethod(), contexts.getTargetClass());
                value =
                        new ExpiryWrapper(System.currentTimeMillis(), expiry.timeUnit().toMillis(expiry.value()), value);
            }
        }

        // Collect any explicit @CachePuts
        collectPutRequests(contexts.get(CachePutOperation.class), value, cachePutRequests);

        // Process any collected put requests, either from @CachePut or a @Cacheable miss
        for (CachePutRequest cachePutRequest : cachePutRequests) {
            cachePutRequest.apply(value);
        }

        // Process any late evictions
        processCacheEvicts(contexts.get(CacheEvictOperation.class), false, value);

        if (value != null && value instanceof ExpiryWrapper) {
            value = ((ExpiryWrapper) value).getValue();
        }
        return value;
    }

    private void processCacheEvicts(Collection<CacheOperationContext> contexts, boolean beforeInvocation, Object result) {
        for (CacheOperationContext context : contexts) {
            CacheEvictOperation operation = (CacheEvictOperation) context.operation;
            if (beforeInvocation == operation.isBeforeInvocation() && isConditionPassing(context, result)) {
                performCacheEvict(context, operation, result);
            }
        }
    }

    private void performCacheEvict(CacheOperationContext context, CacheEvictOperation operation, Object result) {
        Object key = null;
        for (Cache cache : context.getCaches()) {
            if (operation.isCacheWide()) {
                cache.clear();
            } else {
                if (key == null) {
                    key = context.generateKey(result);
                }
                cache.evict(key);
            }
        }
    }

    /**
     * Find a cached item only for {@link CacheableOperation} that passes the condition.
     * 
     * @param contexts the cacheable operations
     * @return a {@link Cache.ValueWrapper} holding the cached item, or {@code null} if none is
     *         found
     */
    private Cache.ValueWrapper findCachedItem(Collection<CacheOperationContext> contexts) {
        Object result = ExpressionEvaluator.NO_RESULT;
        for (CacheOperationContext context : contexts) {
            if (isConditionPassing(context, result)) {
                Object key = generateKey(context, result);
                Cache.ValueWrapper cached = findInCaches(context, key);
                if (cached != null) {
                    return cached;
                }
            }
        }
        return null;
    }

    /**
     * Collect the {@link CachePutRequest} for all {@link CacheOperation} using the specified result
     * item.
     * 
     * @param contexts the contexts to handle
     * @param result the result item (never {@code null})
     * @param putRequests the collection to update
     */
    private void collectPutRequests(Collection<CacheOperationContext> contexts, Object result,
            Collection<CachePutRequest> putRequests) {

        for (CacheOperationContext context : contexts) {
            if (isConditionPassing(context, result)) {
                Object key = generateKey(context, result);
                putRequests.add(new CachePutRequest(context, key));
            }
        }
    }

    private Cache.ValueWrapper findInCaches(CacheOperationContext context, Object key) {
        for (Cache cache : context.getCaches()) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                return wrapper;
            }
        }
        return null;
    }

    private boolean isConditionPassing(CacheOperationContext context, Object result) {
        boolean passing = context.isConditionPassing(result);
        if (!passing && logger.isTraceEnabled()) {
            logger.trace("Cache condition failed on method " + context.method + " for operation " + context.operation);
        }
        return passing;
    }

    private Object generateKey(CacheOperationContext context, Object result) {
        Object key = context.generateKey(result);
        Assert.notNull(key, "Null key returned for cache operation (maybe you are using named params "
                + "on classes without debug info?) " + context.operation);
        if (logger.isTraceEnabled()) {
            logger.trace("Computed cache key " + key + " for operation " + context.operation);
        }
        return key;
    }


    public interface Invoker {

        Object invoke();
    }


    private class CacheOperationContexts {

        private final MultiValueMap<Class<? extends CacheOperation>, CacheOperationContext> contexts =
                new LinkedMultiValueMap<Class<? extends CacheOperation>, CacheOperationContext>();
        @Getter
        private final Class<?> targetClass;
        @Getter
        private final Object target;
        @Getter
        private final Method method;

        public CacheOperationContexts(Collection<? extends CacheOperation> operations, Method method, Object[] args,
                Object target, Class<?> targetClass) {
            this.target = target;
            this.targetClass = targetClass;
            this.method = method;
            for (CacheOperation operation : operations) {
                this.contexts.add(operation.getClass(),
                        getOperationContext(operation, method, args, target, targetClass));
            }
        }

        public Collection<CacheOperationContext> get(Class<? extends CacheOperation> operationClass) {
            Collection<CacheOperationContext> result = this.contexts.get(operationClass);
            return (result != null ? result : Collections.<CacheOperationContext>emptyList());
        }
    }


    protected class CacheOperationContext {

        private final CacheOperation operation;

        private final Method method;

        private final Object[] args;

        private final Object target;

        private final Class<?> targetClass;

        private final Collection<? extends Cache> caches;

        private final MethodCacheKey methodCacheKey;

        public CacheOperationContext(CacheOperation operation, Method method, Object[] args, Object target,
                Class<?> targetClass) {
            this.operation = operation;
            this.method = method;
            this.args = extractArgs(method, args);
            this.target = target;
            this.targetClass = targetClass;
            this.caches = ExpiryCacheInterceptor.this.getCaches(operation);
            this.methodCacheKey = new MethodCacheKey(method, targetClass);
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if (!method.isVarArgs()) {
                return args;
            }
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }

        protected boolean isConditionPassing(Object result) {
            if (StringUtils.hasText(this.operation.getCondition())) {
                EvaluationContext evaluationContext = createEvaluationContext(result);
                return evaluator.condition(this.operation.getCondition(), this.methodCacheKey, evaluationContext);
            }
            return true;
        }

        protected boolean canPutToCache(Object value) {
            String unless = "";
            if (this.operation instanceof CacheableOperation) {
                unless = ((CacheableOperation) this.operation).getUnless();
            } else if (this.operation instanceof CachePutOperation) {
                unless = ((CachePutOperation) this.operation).getUnless();
            }
            if (StringUtils.hasText(unless)) {
                EvaluationContext evaluationContext = createEvaluationContext(value);
                return !evaluator.unless(unless, this.methodCacheKey, evaluationContext);
            }
            return true;
        }

        /**
         * Computes the key for the given caching operation.
         * 
         * @return generated key (null if none can be generated)
         */
        protected Object generateKey(Object result) {
            if (StringUtils.hasText(this.operation.getKey())) {
                EvaluationContext evaluationContext = createEvaluationContext(result);
                return evaluator.key(this.operation.getKey(), this.methodCacheKey, evaluationContext);
            }
            return keyGenerator.generate(this.target, this.method, this.args);
        }

        private EvaluationContext createEvaluationContext(Object result) {
            return evaluator.createEvaluationContext(this.caches, this.method, this.args, this.target,
                    this.targetClass, result);
        }

        protected Collection<? extends Cache> getCaches() {
            return this.caches;
        }
    }


    private static class CachePutRequest {

        private final CacheOperationContext context;

        private final Object key;

        public CachePutRequest(CacheOperationContext context, Object key) {
            this.context = context;
            this.key = key;
        }

        public void apply(Object result) {
            if (this.context.canPutToCache(result)) {
                for (Cache cache : this.context.getCaches()) {
                    cache.put(this.key, result);
                }
            }
        }
    }
}
