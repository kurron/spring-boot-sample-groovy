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

import org.kurron.sample.ApplicationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.web.client.AsyncRestTemplate
import org.springframework.web.client.ResponseErrorHandler

/**
 * We need just enough Spring to parse the application.yml file for us.
 **/
@EnableConfigurationProperties( ApplicationProperties )
class AcceptanceTestConfiguration {
    @Bean
    AsyncRestTemplate asyncRestTemplate() {
        def errorHandler = [hasError: { false } ] as ResponseErrorHandler
        def bean = new AsyncRestTemplate()
        bean.errorHandler = errorHandler
        bean
    }

    @Bean
    DockerCommandServiceResolver dockerCommandServiceResolver() {
        new DockerCommandServiceResolver()
    }

    /**
     * We only create this resolver if we are running acceptance test against an IDE-launched server.
     * @return configured bean.
     */
    @Profile( 'IDE' )
    @Primary
    @Bean
    EnvironmentServiceResolver environmentServiceResolver() {
        new EnvironmentServiceResolver()
    }
}