/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.inbound

/**
 * Contains custom HTTP header names.
 */
class CustomHttpHeaders {

    /**
     * Private constructor, to prevent instantiation.
     */
    private CustomHttpHeaders() { }

    /**
     * The correlation id (a.k.a. work-unit) header, useful in stitching together work being done by the server.
     */
    static final String X_CORRELATION_ID = 'X-Correlation-Id'
}