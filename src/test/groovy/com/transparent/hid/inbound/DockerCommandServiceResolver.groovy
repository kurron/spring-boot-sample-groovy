/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.inbound

import groovy.util.logging.Slf4j
import org.springframework.web.util.UriComponentsBuilder

/**
 * Uses the Docker port command to obtain the port that container is running on.
 **/
@Slf4j
class DockerCommandServiceResolver implements HttpServiceResolver {

    /**
     * Pulls the service port from the Docker container.
     * @return the port number the service is listening on.
     */
    private static int resolvePort() {
        //TODO: I wonder if we can use the REST API instead?

        def command = 'docker port hid-generator 8080/tcp'
        def process = command.execute()
        def results = process.text
        log.info( 'The command {} came back with {}', command, results )
        results.split( ':' ).last() as int
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