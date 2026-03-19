package com.daily.cost.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 拦截器配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    //    @Resource
//    private UserInterceptor userInterceptor;
//
//    /**
//     * 注册拦截器
//     *
//     * @param registry registry
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(userInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/error", "/html/*", "/v3/api-docs/**", "/rpc/**", "/swagger-ui/**");
//    }
//
//    @Override
//    public Validator getValidator() {
//        return new LocalValidatorFactoryBean();
//    }

    @Override
    public void configureMessageConverters(
            List<HttpMessageConverter<?>> converters) {

        // String 转换器强制 UTF-8
        converters.stream()
                .filter(c -> c instanceof StringHttpMessageConverter)
                .forEach(c -> ((StringHttpMessageConverter) c)
                        .setDefaultCharset(StandardCharsets.UTF_8));
    }

    /**
     * 响应式流的编码配置
     */
    @Bean
    public ReactiveAdapterRegistry reactiveAdapterRegistry() {
        return ReactiveAdapterRegistry.getSharedInstance();
    }
}
