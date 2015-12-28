/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.inbound

/**
 * Strategy for determining the HTTP coordinates the tests should connect to.
 **/
interface HttpServiceResolver {

    /**
     * Resolves the URI that should be used when connecting to the application.
     * @return URI to use.
     */
    URI resolveURI()
}