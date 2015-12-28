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

import static HypermediaControl.JSON_MEDIA_TYPE
import static org.springframework.http.HttpMethod.PUT
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import groovy.util.logging.Slf4j
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.AsyncRestOperations

/**
 * Step definitions of the acceptance test.
 **/
@Slf4j
@SuppressWarnings( 'GrMethodMayBeStatic' )
@ContextConfiguration( classes = [AcceptanceTestConfiguration], loader = SpringApplicationContextLoader )
class TestSteps implements GenerationAbility {

    @Autowired
    AsyncRestOperations template

    /**
     * Knows how to determine the port that the service is listening on.
     **/
    @Autowired
    private HttpServiceResolver serviceResolver

    /**
     * This is state shared between steps and can be setup and torn down by the hooks.
     **/
    class MyWorld {
        HttpEntity<HypermediaControl> request
        def headers = new HttpHeaders()
        ResponseEntity<HypermediaControl> response
    }

    /**
     * Shared between hooks and steps.
     **/
    MyWorld sharedState

    @Before
    void assembleSharedState() {
        log.info( 'Creating shared state' )
        sharedState = new MyWorld()

        assert template
        assert serviceResolver
    }

    @After
    void destroySharedState() {
        log.info( 'Destroying shared state' )
        sharedState = null
    }

    @Given( '^a valid request$' )
    void 'a valid request'() {
        def data = (1..2).collect {
            new Data( knownLanguage: randomHexString(), learningLanguage: randomHexString(), side1: randomHexString(), side2: randomHexString())
        }
        def payload = new HypermediaControl(  items: data )
        sharedState.request = new HttpEntity<>( payload, sharedState.headers )
    }

    @Given( '^an X-Correlation-Id header filled in with a unique request identifier$' )
    void 'an X-Correlation-Id header filled in with a unique request identifier'() {
        sharedState.headers.add( 'X-Correlation-Id', randomHexString() )
    }

    @Given( '^an Accept header filled in with the MIME type of the hypermedia control$' )
    void 'an Accept header filled in with the MIME type of the hypermedia control'() {
        sharedState.headers.setAccept( [JSON_MEDIA_TYPE] )
    }

    @Given( '^a Content-Type header filled in with the media-type of the hypermedia control$' )
    void 'a Content-Type header filled in with the media-type of the hypermedia control'() {
        sharedState.headers.setContentType( JSON_MEDIA_TYPE  )
    }

    @When( '^a PUT request is made to the resource$' )
    void 'a PUT request is made to the resource'() {
        def future = template.exchange( serviceResolver.resolveURI(), PUT, sharedState.request, HypermediaControl )
        sharedState.response = future.get()
    }

    @Then( '^a response with a (\\d+) HTTP status code is returned$' )
    void 'a response with a N HTTP status code is returned'( int statusCode ) {
        assert statusCode == sharedState.response.statusCode.value()
    }

    @Then( '^the hypermedia control contains the fingerprint$' )
    void 'the hypermedia control contains the fingerprint'() {
        sharedState.response.body.items.every {
            it.hid
        }
    }
}
