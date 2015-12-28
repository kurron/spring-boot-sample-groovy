/*
 * Copyright (c) 2015. Ronald D. Kurr kurr@jvmguy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kurron.sample.inbound

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

        def command = 'docker port spring-boot-sample-groovy 8080/tcp'
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