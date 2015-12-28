/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.inbound

import com.transparent.hid.ApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.client.ResponseErrorHandler

/**
 * We need just enough Spring to parse the application.yml file for us.
 **/
@EnableConfigurationProperties( ApplicationProperties )
class AcceptanceTestConfiguration {
    @Bean
    AsyncRestTemplate asyncRestTemplate() {
        def errorHandler = [hasError: { false } ] as ResponseErrorHandler
        def bean = new AsyncRestTemplate()
        bean.errorHandler = errorHandler
        bean
    }

    @Bean
    DockerCommandServiceResolver dockerCommandServiceResolver() {
        new DockerCommandServiceResolver()
    }

    /**
     * We only create this resolver if we are running acceptance test against an IDE-launched server.
     * @return configured bean.
     */
    @Profile( 'IDE' )
    @Primary
    @Bean
    EnvironmentServiceResolver environmentServiceResolver() {
        new EnvironmentServiceResolver()
    }
}