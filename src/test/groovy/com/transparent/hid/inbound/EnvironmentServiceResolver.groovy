/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.inbound

import org.springframework.web.util.UriComponentsBuilder

/**
 * Uses the environment to obtain the port that a standalone server is running on.
 **/
class EnvironmentServiceResolver implements HttpServiceResolver {

    /**
     * Pulls the service port from the environment.
     * @return the port the service is listening on.
     */
    private static int resolvePort() {
        System.properties['integration.test.port'] as int

    }

    @Override
    URI resolveURI() {
        UriComponentsBuilder.newInstance()
                            .scheme( 'http' )
                            .host( 'localhost' )
                            .port( resolvePort() )
                            .path( '/hash-id' )
                            .build()
                            .toUri()
    }

}