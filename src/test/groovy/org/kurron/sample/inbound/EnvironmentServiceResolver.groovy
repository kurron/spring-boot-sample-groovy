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