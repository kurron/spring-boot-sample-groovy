/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.inbound

import static com.transparent.hid.feeback.LoggingContext.GENERIC_ERROR
import static com.transparent.hid.inbound.HypermediaControl.JSON_MIME_TYPE
import static org.springframework.web.bind.annotation.RequestMethod.POST
import static org.springframework.web.bind.annotation.RequestMethod.PUT
import com.transparent.jcommon.domain.Phrase
import com.transparent.jcommon.hid.HidBuilder
import javax.validation.Valid
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

    @RequestMapping( method = [RequestMethod.GET], produces = [JSON_MIME_TYPE] )
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
    @RequestMapping( method = [PUT,POST], consumes = [JSON_MIME_TYPE], produces = [JSON_MIME_TYPE] )
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

    private static HypermediaControl calculateIDs( HypermediaControl request ) {
        request.items.each { data ->
            def hidBuilder = new HidBuilder()
            hidBuilder.knownLegacyCode( data.knownLanguage )
            hidBuilder.learningLegacyCode( data.learningLanguage )
            data.hid = hidBuilder.newHid( createPhrase( data.side1 ), createPhrase( data.side2 ) )
        }
        request.httpCode = HttpStatus.OK.value()
        request
    }

    private static Phrase createPhrase( String phrase ) {
        new Phrase( phrase, null, null )
    }
}
