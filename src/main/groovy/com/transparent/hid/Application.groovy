/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid

import groovy.util.logging.Slf4j
import org.kurron.feedback.FeedbackAwareBeanPostProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate

/**
 * This is the main entry into the application. Running from the command-line using embedded Tomcat will invoke
 * the main() method.
 */
@SpringBootApplication
@EnableConfigurationProperties( ApplicationProperties )
@Slf4j
@SuppressWarnings( 'GStringExpressionWithinString' )
class Application {

    /**
     * Called to start the entire application. Typically, java -jar foo.jar.
     * @param args any arguments to the program.
     */
    static void main( String[] args ) {
        log.info '--- Running embedded web container ----'
        SpringApplication.run( Application, args )
    }

    /**
     * Indicates the type of service emitting the messages.
     */
    @Value( '${info.app.name}' )
    String serviceCode

    /**
     * Indicates the instance of the service emitting the messages.
     */
    @Value( '${PID}' )
    String serviceInstance

    /**
     * Indicates the logical group of the service emitting the messages.
     */
    @Value( '${info.app.realm}' )
    String realm

    @Bean
    FeedbackAwareBeanPostProcessor feedbackAwareBeanPostProcessor() {
        new FeedbackAwareBeanPostProcessor( serviceCode, serviceInstance, realm )
    }

    @Bean
    AsyncRestTemplate asyncRestTemplate() {
        def errorHandler = [hasError: { false } ] as ResponseErrorHandler
        def bean = new AsyncRestTemplate()
        bean.errorHandler = errorHandler
        bean
    }

    @Bean
    RestTemplate restTemplate() {
        def errorHandler = [hasError: { false } ] as ResponseErrorHandler
        def bean = new RestTemplate()
        bean.errorHandler = errorHandler
        bean
    }
}

