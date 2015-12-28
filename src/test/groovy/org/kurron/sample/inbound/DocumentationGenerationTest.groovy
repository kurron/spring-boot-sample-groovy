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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Rule
import org.kurron.sample.Application
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.restdocs.RestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

/**
 * This test generates sample input and output that the API documentation generator uses.
 **/
@ContextConfiguration( loader = SpringApplicationContextLoader, classes = [Application] )
@WebIntegrationTest( randomPort = true )
class DocumentationGenerationTest extends Specification implements GenerationAbility {

    @Rule
    final RestDocumentation restDocumentation = new RestDocumentation( 'build/generated-snippets' )

    @Autowired
    private WebApplicationContext context

    @Autowired
    ObjectMapper mapper

    static final String URI = '/hash-id'

    MockMvc mockMvc
    def documentationConfiguration = MockMvcRestDocumentation.documentationConfiguration( restDocumentation )

    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup( context )
                                 .apply( documentationConfiguration.uris()
                                                                   .withHost( 'api.example.com' ) )
                                 .build()
    }

    def 'document GET'() {
        given: 'valid request'
        def requestBuilder = MockMvcRequestBuilders.get( URI )
                                                   .header( CustomHttpHeaders.X_CORRELATION_ID, randomHexString() )
                                                   .accept( HypermediaControl.JSON_MEDIA_TYPE)
        expect: 'the GET request to succeed'
        mockMvc.perform( requestBuilder ).andExpect( status().isOk() ).andDo( MockMvcRestDocumentation.document( 'api-discovery' ) )
    }

    def 'document PUT'() {
        given: 'valid request'
        def control = buildControl()
        def payload = mapper.writeValueAsString( control )
        def requestBuilder = MockMvcRequestBuilders.put( URI )
                                                   .content( payload )
                                                   .contentType( HypermediaControl.JSON_MEDIA_TYPE )
                                                   .header( 'Content-Length', payload.bytes.length )
                                                   .header( CustomHttpHeaders.X_CORRELATION_ID, randomHexString() )
                                                   .accept( HypermediaControl.JSON_MEDIA_TYPE)
        expect: 'the PUT request to succeed'
        mockMvc.perform( requestBuilder ).andExpect( status().isOk() ).andDo( MockMvcRestDocumentation.document( 'hid-calculation' ) )
    }

    def 'document validation scenario'() {
        given: 'invalid request'
        def control = buildControl()
        control.items.first().knownLanguage = null
        def payload = mapper.writeValueAsString( control )
        def requestBuilder = MockMvcRequestBuilders.put( URI )
                                                   .content( payload )
                                                   .contentType( HypermediaControl.JSON_MEDIA_TYPE )
                                                   .header( 'Content-Length', payload.bytes.length )
                                                   .header( CustomHttpHeaders.X_CORRELATION_ID, randomHexString() )
                                                   .accept( HypermediaControl.JSON_MEDIA_TYPE)
        expect: 'the PUT request fail'
        mockMvc.perform( requestBuilder ).andExpect( status().is4xxClientError() ).andDo( MockMvcRestDocumentation.document( 'validation' ) )
    }

    HypermediaControl buildControl() {
        def data = (1..2).collect {
            new Data( knownLanguage: randomHexString(), learningLanguage: randomHexString(), side1: randomHexString(), side2: randomHexString())
        }
        new HypermediaControl(  items: data )
    }
}
