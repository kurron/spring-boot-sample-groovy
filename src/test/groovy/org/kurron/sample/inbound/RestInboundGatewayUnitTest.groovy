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

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.experimental.categories.Category
import org.kurron.categories.UnitTest
import org.kurron.traits.GenerationAbility
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

/**
 * A unit level test of the inbound gateway.
 **/
@Category( UnitTest )
class RestInboundGatewayUnitTest extends Specification implements GenerationAbility {

    static final String URI = '/hash-id'

    def counter = Stub( CounterService )
    def sut = new RestInboundGateway( counter )
    def mockMvc = MockMvcBuilders.standaloneSetup( sut ).build()
    def mapper = new ObjectMapper()

    def 'exercise newHid'() {
        given: 'valid request'
        def request = buildControl()
        def payload = mapper.writeValueAsString( request )
        def requestBuilder = MockMvcRequestBuilders.put( URI )
                                                   .content( payload )
                                                   .contentType( HypermediaControl.JSON_MEDIA_TYPE )
                                                   .header( 'Content-Length', payload.bytes.length )
                                                   .header( CustomHttpHeaders.X_CORRELATION_ID, randomHexString() )
        when: 'the POST request is made'
        def result = mockMvc.perform( requestBuilder ).andReturn()

        then: 'we get a 200'
        result.response.status == HttpStatus.OK.value()

        and: 'the response content type is set'
        result.response.contentType == HypermediaControl.JSON_MIME_TYPE

        and: 'the response contains a HID for each item sent'
        def response = mapper.readValue( result.response.contentAsByteArray, HypermediaControl )
        request.items.size() == response.items.size()
        response.items.every { it.hid }
    }

    HypermediaControl buildControl() {
        def data = (1..2).collect {
            new Data( knownLanguage: randomHexString(), learningLanguage: randomHexString(), side1: randomHexString(), side2: randomHexString())
        }
        new HypermediaControl(  items: data )
    }
}
