/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
 */
package com.transparent.hid.inbound

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.TupleConstructor
import javax.validation.constraints.NotNull

/**
 * Optional data section of the control.
 */
@TupleConstructor
@JsonInclude( JsonInclude.Include.NON_NULL )
class Data {

    /**
     * The language code of the learner's native language.
     **/
    @NotNull
    @JsonProperty( 'knownLanguage' )
    String knownLanguage

    /**
     * The language code of the learner's foreign language.
     **/
    @NotNull
    @JsonProperty( 'learningLanguage' )
    String learningLanguage

    /**
     * The known side of the card.
     **/
    @NotNull
    @JsonProperty( 'side1' )
    String side1

    /**
     * The foreign side of the card.
     **/
    @NotNull
    @JsonProperty( 'side2' )
    String side2

    /**
     * The calculated hash id of the card.
     **/
    @JsonProperty( 'hid' )
    String hid

}