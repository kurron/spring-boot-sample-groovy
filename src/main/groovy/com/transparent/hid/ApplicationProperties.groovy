/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Application specific properties. This can be injected into beans to share values.
 */
@ConfigurationProperties( value = 'hid', ignoreUnknownFields = false )
class ApplicationProperties {

    /**
     * Flag controlling whether or not the correlation id is required.
     */
    boolean requireCorrelationId
}