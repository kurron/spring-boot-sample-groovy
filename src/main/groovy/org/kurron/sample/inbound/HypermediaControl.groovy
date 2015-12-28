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
import javax.validation.Valid
import org.springframework.hateoas.ResourceSupport
import org.springframework.http.MediaType

/**
 * The hypermedia REST control for the HID resource.  Can be serialized into JSON.
 */
@JsonInclude( JsonInclude.Include.NON_NULL )
@TupleConstructor
class HypermediaControl extends ResourceSupport {

    /**
     * The expected JSON MIME type for the control.
     */
    public static final String JSON_MIME_TYPE = 'application/json;type=hash-id;version=1.0.0'

    /**
     * Convenience form of the JSON MIME-TYPE for Spring MVC APIs.
     **/
    public static final MediaType JSON_MEDIA_TYPE = MediaType.parseMediaType( JSON_MIME_TYPE )

    /**
     * The HTTP status code. We put it here in case the client isn't allowed access to the headers.
     */
    @JsonProperty( 'http-code' )
    Integer httpCode

    /**
     * Required block that contains both request and response information.
     */
    @Valid
    @JsonProperty( 'items' )
    List<Data> items

    /**
     * An optional block that is only populated after an error occurs.
     */
    @JsonProperty( 'error' )
    ErrorBlock errorBlock
}
