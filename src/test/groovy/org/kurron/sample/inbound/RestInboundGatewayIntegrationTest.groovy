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
import static org.springframework.http.HttpStatus.BAD_REQUEST
import java.util.concurrent.Future
import org.junit.experimental.categories.Category
import org.kurron.categories.InboundIntegrationTest
import org.kurron.sample.Application
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.AsyncRestOperations
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

/**
 * An integration level test of the inbound gateway.
 **/
@Category( InboundIntegrationTest )
@ContextConfiguration( loader = SpringApplicationContextLoader, classes = [Application] )
@WebIntegrationTest( randomPort = true )
class RestInboundGatewayIntegrationTest extends Specification implements GenerationAbility {

    @Autowired
    AsyncRestOperations theTemplate

    @Value( '${local.server.port}' )
    int port

    static final String PATH = '/hash-id'

    def 'exercise new HID calculation'() {
        given: 'a valid environment'
        assert theTemplate
        assert port

        and: 'a valid request'
        def payload = buildControl()
        def headers = new HttpHeaders()
        headers.setContentType( JSON_MEDIA_TYPE  )
        headers.add( 'X-Correlation-Id', randomHexString() )
        HttpEntity<HypermediaControl> request = new HttpEntity<>( payload, headers )

        and: 'the PUT request is made'
        def uri = UriComponentsBuilder.newInstance().scheme( 'http' ).host( 'localhost' ).port( port ).path( PATH ).build().toUri()
        def future = theTemplate.exchange( uri, PUT, request, HypermediaControl )

        when: 'the answer comes back'
        def response = future.get()

        then: 'we get a 200'
        response.statusCode == HttpStatus.OK

        and: 'the response content type is set'
        // actual value is application/json;charset=UTF-8 so you can't do a straight equals
        response.headers.getContentType().isCompatibleWith( JSON_MEDIA_TYPE )

        and: 'the control contains the status code'
        response.body.httpCode == HttpStatus.OK.value()

        and: 'the response contains a HID for each item sent'
        payload.items.size() == response.body.items.size()
        response.body.items.every { it.hid }

    }

    def 'exercise data validation'() {
        given: 'a valid environment'
        assert theTemplate
        assert port

        and: 'an invalid request'
        def payload = buildControl()
        payload.items.first().knownLanguage = null
        def headers = new HttpHeaders()
        headers.setContentType( JSON_MEDIA_TYPE  )
        headers.add( 'X-Correlation-Id', randomHexString() )
        HttpEntity<HypermediaControl> request = new HttpEntity<>( payload, headers )

        and: 'the PUT request is made'
        def uri = UriComponentsBuilder.newInstance().scheme( 'http' ).host( 'localhost' ).port( port ).path( PATH ).build().toUri()
        Future<ResponseEntity<HypermediaControl>> future = theTemplate.exchange( uri, PUT, request, HypermediaControl )

        when: 'the answer comes back'
        def response = future.get()

        then: 'we get a 400'
        response.statusCode == BAD_REQUEST

        and: 'the response content type is set'
        // actual value is application/json;charset=UTF-8 so you can't do a straight equals
        response.headers.getContentType().isCompatibleWith( JSON_MEDIA_TYPE )

        and: 'the control contains the status code'
        response.body.httpCode == BAD_REQUEST.value()

        and: 'the response contains failure information'
        response.body.errorBlock.code
        response.body.errorBlock.message
        response.body.errorBlock.developerMessage

    }

    HypermediaControl buildControl() {
        def data = (1..2).collect {
            new Data( knownLanguage: randomHexString(), learningLanguage: randomHexString(), side1: randomHexString(), side2: randomHexString())
        }
        new HypermediaControl(  items: data )
    }
}
