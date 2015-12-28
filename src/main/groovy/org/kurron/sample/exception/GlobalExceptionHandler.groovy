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
package org.kurron.sample.exception

import static org.kurron.sample.feeback.LoggingContext.GENERIC_ERROR
import static org.kurron.sample.feeback.LoggingContext.INVALID_FIELD
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.kurron.feedback.FeedbackAware
import org.kurron.feedback.FeedbackProvider
import org.kurron.feedback.NullFeedbackProvider
import org.kurron.feedback.exceptions.AbstractError
import org.kurron.sample.inbound.ErrorBlock
import org.kurron.sample.inbound.HypermediaControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Global handling for REST exceptions.
 */
@SuppressWarnings( 'GroovyUnusedDeclaration' ) // found via component scanning
@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler implements FeedbackAware {

    /**
     * The provider to use.
     */
    @Delegate
    private FeedbackProvider theFeedbackProvider = new NullFeedbackProvider()

    @Override
    FeedbackProvider getFeedbackProvider() {
        theFeedbackProvider
    }

    @Override
    void setFeedbackProvider( final FeedbackProvider aProvider ) {
        theFeedbackProvider = aProvider
    }

    @Override
    protected ResponseEntity<HypermediaControl> handleExceptionInternal( Exception e,
                                                                         Object body,
                                                                         HttpHeaders headers,
                                                                         HttpStatus status,
                                                                         WebRequest request ) {
        ResponseEntity<HypermediaControl> response

        // I couldn't get a handler specific to MethodArgumentNotValidException bound. Spring says it is ambiguous!
        if ( e instanceof MethodArgumentNotValidException ) {
            response = handleValidationException( e as MethodArgumentNotValidException )
        }
        else {
            sendFeedback( GENERIC_ERROR, e.message )
            def control = new HypermediaControl( httpCode: status.value() )
            control.errorBlock = new ErrorBlock( code: GENERIC_ERROR.code,
                                                 message: e.message,
                                                 developerMessage: 'Indicates that the exception was not handled explicitly and is being handled as a generic error' )
            response = wrapInResponseEntity( control, status, headers )
        }
        response
    }

    /**
     * Handles validation errors detected by Spring.
     * @param e the validation failure.
     * @return the constructed response entity, containing details about the error.
     */
//    @ExceptionHandler( MethodArgumentNotValidException )
    ResponseEntity<HypermediaControl> handleValidationException( MethodArgumentNotValidException e ) {
        def errors = e.bindingResult.fieldErrors
        def field = errors.first().field
        def message = errors.first().defaultMessage
        sendFeedback( INVALID_FIELD, field, message )
        def control = new HypermediaControl( httpCode: BAD_REQUEST.value() )
        control.errorBlock = new ErrorBlock( code: INVALID_FIELD.code,
                                             message: 'Request failed validation.',
                                             developerMessage: "The field ${field} ${message}. Please correct your request and try again." )
        wrapInResponseEntity( control, BAD_REQUEST)
    }


    /**
     * Handles errors thrown by application itself.
     * @param e the error.
     * @return the constructed response entity, containing details about the error.
     */
    @ExceptionHandler( AbstractError )
    static ResponseEntity<HypermediaControl> handleApplicationException( AbstractError e ) {
        def control = new HypermediaControl( httpCode: e.httpStatus.value() ).with {
            errorBlock = new ErrorBlock( code: e.code, message: e.message, developerMessage: e.developerMessage )
            it
        }
        wrapInResponseEntity( control, e.httpStatus )
    }

    /**
     * Knows how to transform a non-application exception into a hypermedia control.
     * @param throwable non-application error.
     * @return control that contains as much data about the error that is available to us.
     */
    @ExceptionHandler( Throwable )
    static ResponseEntity<HypermediaControl> handleSystemException( final Throwable throwable ) {
        def control = new HypermediaControl( httpCode: INTERNAL_SERVER_ERROR.value() )
        control.errorBlock = new ErrorBlock( code: GENERIC_ERROR.code,
                                             message: throwable.message,
                                             developerMessage: 'Indicates that the exception was not handled explicitly and is being handled as a generic error' )
        wrapInResponseEntity( control, INTERNAL_SERVER_ERROR )
    }

    /**
     * Wraps the provided control in a response entity.
     * @param control the control to return in the body of the response.
     * @param status the HTTP status to return.
     * @param headers the HTTP headers to return. If provided, the existing headers are used, otherwise new headers are created.
     * @return the response entity.
     */
    private static ResponseEntity<HypermediaControl> wrapInResponseEntity( HypermediaControl control,
                                                                           HttpStatus status,
                                                                           HttpHeaders headers = new HttpHeaders() ) {
        new ResponseEntity<>( control, headers, status )
    }
}