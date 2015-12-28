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

import static org.kurron.sample.feeback.LoggingContext.GENERIC_ERROR
import static org.springframework.web.bind.annotation.RequestMethod.POST
import static org.springframework.web.bind.annotation.RequestMethod.PUT
import groovy.transform.CompileDynamic
import javax.validation.Valid
import org.kurron.categories.ByteArrayEnhancements
import org.kurron.categories.StringEnhancements
import org.kurron.feedback.AbstractFeedbackAware
import org.kurron.stereotype.InboundRestGateway
import org.kurron.traits.GenerationAbility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.hateoas.Link
import org.springframework.hateoas.UriTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.util.UriComponentsBuilder

/**
 * Handles inbound REST requests.
 */
@InboundRestGateway
@RequestMapping( value = '/hash-id' )
class RestInboundGateway extends AbstractFeedbackAware implements GenerationAbility {

    /**
     * Used to track counts.
     */
    private final CounterService counterService

    @Autowired
    RestInboundGateway( final CounterService aCounterService ) {
        counterService = aCounterService
    }

    @RequestMapping( method = [RequestMethod.GET], produces = [HypermediaControl.JSON_MIME_TYPE] )
    ResponseEntity<HypermediaControl> apiDiscovery( @RequestHeader( 'X-Correlation-Id' ) Optional<String> correlationID,
                                                    UriComponentsBuilder builder ) {
        counterService.increment( 'gateway.api-discovery' )

        def loggingID = correlationID.orElse( randomHexString() )
        def response = new HypermediaControl( httpCode: HttpStatus.OK.value(  ) )
        injectLinks( builder, response )
        def headers = new HttpHeaders()
        headers.add( CustomHttpHeaders.X_CORRELATION_ID, loggingID )

        new ResponseEntity<HypermediaControl>( response, headers, HttpStatus.OK )
    }

    //TODO: support for POST is temporary until clients can migrate over to PUT
    @RequestMapping( method = [PUT,POST], consumes = [HypermediaControl.JSON_MIME_TYPE], produces = [HypermediaControl.JSON_MIME_TYPE] )
    ResponseEntity<HypermediaControl> calculateHIDs( @RequestBody @Valid final HypermediaControl request,
                                                     @RequestHeader( 'X-Correlation-Id' ) Optional<String> correlationID,
                                                     UriComponentsBuilder componentsBuilder ) {
        counterService.increment( 'gateway.calculate-hid' )

        def loggingID = correlationID.orElse( randomHexString() )
        feedbackProvider.sendFeedback( GENERIC_ERROR, loggingID )

        def response = calculateIDs( request )
        injectLinks( componentsBuilder, response )
        def headers = new HttpHeaders()
        headers.add( CustomHttpHeaders.X_CORRELATION_ID, loggingID )
        new ResponseEntity<HypermediaControl>( response, headers, HttpStatus.OK )
    }

    private static Link selfLink( UriComponentsBuilder builder ) {
        def selfBuilder = UriComponentsBuilder.fromUri( builder.build().toUri() )
        new Link( new UriTemplate( selfBuilder.path( '/hash-id' ) .build().toUriString() ), 'self' )
    }

    private static Link discoveryLink( UriComponentsBuilder builder ) {
        def discoveryBuilder = UriComponentsBuilder.fromUri( builder.build().toUri() )
        new Link( new UriTemplate( discoveryBuilder.path( '/hash-id' ) .build().toUriString() ), 'api-discovery' )
    }

    private static Link apiLink( UriComponentsBuilder builder ) {
        def docsBuilder = UriComponentsBuilder.fromUri( builder.build().toUri() )
        new Link( new UriTemplate( docsBuilder.path( '/docs/index.html' ).build().toUriString() ), 'api-docs' )
    }

    private static void injectLinks( UriComponentsBuilder builder, HypermediaControl response ) {
        response.add( selfLink( builder ) )
        response.add( apiLink( builder ) )
        response.add( discoveryLink( builder ) )
    }

    @CompileDynamic
    private static HypermediaControl calculateIDs( HypermediaControl request ) {
        request.items.each { data ->
            def buffer = toDigestBytes( data )
            data.hid = use( ByteArrayEnhancements ) { ->
                buffer.toMD5()
            }
        }
        request.httpCode = HttpStatus.OK.value()
        request
    }

    @CompileDynamic
    private static byte[] toDigestBytes( Data data ) {
        def sideOneBytes = use( StringEnhancements ) { ->
            data.side1.utf8Bytes
        }
        def sideTwoBytes = use( StringEnhancements ) { ->
            data.side2.utf8Bytes
        }
        def bytes = new ByteArrayOutputStream( sideOneBytes.length + sideTwoBytes.length )
        bytes.write(  sideOneBytes  )
        bytes.write( sideTwoBytes )
        bytes.toByteArray()
    }
}
