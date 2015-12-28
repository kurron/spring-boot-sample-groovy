/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.feeback

import static org.kurron.feedback.Audience.QA
import static org.kurron.feedback.FeedbackLevel.ERROR
import static org.kurron.feedback.FeedbackLevel.WARN
import org.kurron.feedback.Audience
import org.kurron.feedback.FeedbackContext
import org.kurron.feedback.FeedbackLevel

/**
 * Message codes specific to this application.
 */
enum LoggingContext implements FeedbackContext {

    GENERIC_ERROR( 2000, 'The following error has occurred and was caught by the global error handler: {}', ERROR, QA ),
    PRECONDITION_FAILED( 2001, 'The required {} header was not found on an inbound REST request', ERROR, QA ),
    MISSING_CORRELATION_ID( 2002, 'A correlation id was missing from a request, and an auto-generated id of {} will be used instead', WARN, QA ),
    INVALID_FIELD( 2003, 'The field {} {}', ERROR, QA ),

    /**
     * Unique context code for this instance.
     */
    private final int theCode

    /**
     * Message format string for this instance.
     */
    private final String theFormatString

    /**
     * Feedback level for this instance.
     */
    private final FeedbackLevel theFeedbackLevel

    /**
     * The audience for this instance.
     */
    private final Audience theAudience

    LoggingContext( int aCode, String aFormatString, FeedbackLevel aFeedbackLevel, Audience anAudience ) {
        theCode = aCode
        theFormatString = aFormatString
        theFeedbackLevel = aFeedbackLevel
        theAudience = anAudience
    }

    @Override
    int getCode() {
        theCode
    }

    @Override
    String getFormatString() {
        theFormatString
    }

    @Override
    FeedbackLevel getFeedbackLevel() {
        theFeedbackLevel
    }

    @Override
    Audience getAudience() {
        theAudience
    }
}
