package com.warningrc.test.learnspring.module.beans.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

/**
 * 验证{@link org.springframework.beans.factory.BeanFactory BeanFactory}的{@code getBean}方法
 *
 * @author <a href="http://blog.warningrc.com">王宁</a><br/>
 * @date 2015-2-3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BeanFactoryTest1.SpringConfig.class)
public class BeanFactoryTest1 {

    @Configuration
    public static class SpringConfig {
        @Bean(name = "helloWorldService-prototype")
        @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        public HelloWorldServiceImpl prototypeHelloWorldService(@Value("prototype-warning") String name) {
            return new HelloWorldServiceImpl(name);
        }

        @Bean(name = "helloWorldService-singleton")
        @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
        public HelloWorldServiceSingletonImpl singletonHelloWorldService() {
            return new HelloWorldServiceSingletonImpl("singleton-warning");
        }
    }

    @Autowired
    private BeanFactory factory;

    @Test
    public void testGetBean_args__String() {
        HelloWorldService helloWorldService = (HelloWorldService) factory.getBean("helloWorldService-prototype");
        // 验证构造函数注入值
        Assert.assertEquals("prototype-warning", helloWorldService.getUserName());
        // 验证多例模式
        Assert.assertFalse(factory.getBean("helloWorldService-prototype") == factory
                .getBean("helloWorldService-prototype"));
        helloWorldService = (HelloWorldService) factory.getBean("helloWorldService-singleton");
        // 验证构造函数注入值
        Assert.assertEquals("singleton-warning", helloWorldService.getUserName());
        // 验证单例模式
        Assert.assertTrue(factory.getBean("helloWorldService-singleton") == factory
                .getBean("helloWorldService-singleton"));
    }

    // 验证多例模式bean获取实例时注入值
    @Test
    public void testGetBean_args__String_Objects__prototypebean() {
        HelloWorldService helloWorldService =
                (HelloWorldService) factory.getBean("helloWorldService-prototype", "change-warning");
        Assert.assertEquals("change-warning", helloWorldService.getUserName());
    }

    // 验证BeanFactory.getBean(String name, Object... args)
    // 方法获取单例模式bean抛出BeanDefinitionStoreException异常
    @Test(expected = BeanDefinitionStoreException.class)
    public void testGetBean_args__String_Objects__singletonbean() {
        factory.getBean("helloWorldService-singleton", "change-warning");
    }

    // 验证BeanFactory.getBean(Class cls)方法获取单例bean
    @Test
    public void testGetBean_args__Class__singletonbean() {
        HelloWorldService helloWorldService = (HelloWorldService) factory.getBean(HelloWorldServiceSingletonImpl.class);
        Assert.assertEquals("singleton-warning", helloWorldService.getUserName());
    }

    // 验证BeanFactory.getBean(Class cls)方法获取多例bean
    @Test
    public void testGetBean_args__Class__prototypebean() {
        HelloWorldService helloWorldService = (HelloWorldService) factory.getBean(HelloWorldServiceImpl.class);
        Assert.assertEquals("prototype-warning", helloWorldService.getUserName());
    }

    // 验证BeanFactory.getBean(Class cls)方法遇到多个实例时抛出NoUniqueBeanDefinitionException异常
    @Test(expected = NoUniqueBeanDefinitionException.class)
    public void testGetBean_args__Class__prototypebean1() {
        factory.getBean(HelloWorldService.class);
    }

    // 验证BeanFactory.getBean(String name,Class
    // requiredType)方法,当传递的类型与获取到的bean的类型不一致时抛出BeanNotOfRequiredTypeException异常
    @Test(expected = BeanNotOfRequiredTypeException.class)
    public void testGetBean_args__String_Class() {
        HelloWorldService helloWorldService =
                factory.getBean("helloWorldService-prototype", HelloWorldServiceImpl.class);
        Assert.assertEquals("prototype-warning", helloWorldService.getUserName());
        factory.getBean("helloWorldService-prototype", HelloWorldServiceSingletonImpl.class);
    }

}
