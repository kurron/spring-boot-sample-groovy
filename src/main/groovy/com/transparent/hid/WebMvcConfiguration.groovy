/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * Allows us to customize Spring MVC in a Spring Boot friendly manner.
 */
@Configuration
class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    CorrelationIdHandlerInterceptor correlationIdHandlerInterceptor

    @Override
    void addInterceptors( final InterceptorRegistry registry ) {
        registry.addInterceptor( correlationIdHandlerInterceptor )
    }
}
