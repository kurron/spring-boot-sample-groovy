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