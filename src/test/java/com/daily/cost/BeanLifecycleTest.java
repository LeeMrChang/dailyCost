package com.daily.cost;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@SpringBootTest(args = "--spring.config.location=optional:classpath:application-local.yml,optional:classpath:application.yml")
@Slf4j
public class BeanLifecycleTest {

    @Configuration
    @ComponentScan
    static class Config {
        @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
        public AccountService accountService() {
            return new AccountService();
        }
    }

    static class AccountService {

        @Autowired(required = false)
        private Dependency dependency;

        public AccountService() {
            System.out.println("1. 构造方法执行");
        }

        @PostConstruct
        public void init() {
            System.out.println("2. @PostConstruct 执行");
        }

        public void customInit() {
            System.out.println("3. 自定义 init-method");
        }

        @PreDestroy
        public void preDestroy() {
            System.out.println("4. @PreDestroy 执行");
        }

        public void customDestroy() {
            System.out.println("5. 自定义 destroy-method");
        }
    }

    @Component
    static class Dependency {}

    @Autowired
    private AccountService accountService;

    @Test
    public void testBeanLifecycle() {
        System.out.println("Bean 获取成功: " + accountService);
    }

}
