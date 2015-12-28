/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid

import static com.transparent.hid.feeback.LoggingContext.MISSING_CORRELATION_ID
import static com.transparent.hid.feeback.LoggingContext.PRECONDITION_FAILED
import com.transparent.hid.inbound.CustomHttpHeaders
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.kurron.feedback.AbstractFeedbackAware
import org.kurron.feedback.exceptions.PreconditionFailedError
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

/**
 * Intercepts each REST request and extracts the X-Correlation-Id header, which is added to the MDC logging context. If no header is
 * found, an error is thrown.
 */
@Component
class CorrelationIdHandlerInterceptor extends AbstractFeedbackAware implements HandlerInterceptor {

    /**
     * Provides currently active property values.
     */
    private final ApplicationProperties configuration

    /**
     * Correlation id key into the mapped diagnostic context.
     */
    public static final String CORRELATION_ID = 'correlation-id'

    @Autowired
    CorrelationIdHandlerInterceptor( final ApplicationProperties aConfiguration ) {
        configuration = aConfiguration
    }

    @Override
    boolean preHandle( final HttpServletRequest request, final HttpServletResponse response, final Object handler ) throws Exception {
        def correlationId = request.getHeader( CustomHttpHeaders.X_CORRELATION_ID )
        if ( !correlationId ) {
            if ( configuration.requireCorrelationId ) {
                feedbackProvider.sendFeedback( PRECONDITION_FAILED, CustomHttpHeaders.X_CORRELATION_ID )
                throw new PreconditionFailedError( PRECONDITION_FAILED, CustomHttpHeaders.X_CORRELATION_ID )
            } else {
                correlationId = UUID.randomUUID().toString()
                feedbackProvider.sendFeedback( MISSING_CORRELATION_ID, correlationId )
            }
        }
        MDC.put( CORRELATION_ID, correlationId )
        true
    }

    @Override
    void postHandle( HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView ) throws Exception {
        MDC.remove( CORRELATION_ID )
    }

    @Override
    void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex ) throws Exception { }
}