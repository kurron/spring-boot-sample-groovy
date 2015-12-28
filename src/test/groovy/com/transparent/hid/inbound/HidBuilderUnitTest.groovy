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
package com.transparent.hid.inbound

import com.transparent.jcommon.domain.Phrase
import com.transparent.jcommon.hid.HidBuilder
import groovy.util.logging.Slf4j
import org.junit.experimental.categories.Category
import org.kurron.categories.UnitTest
import org.kurron.traits.GenerationAbility
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

/**
 * A test to explore the capabilities of the HID builder library.
 **/
@Slf4j
@Category( UnitTest )
class HidBuilderUnitTest extends Specification implements GenerationAbility {

    @Unroll( 'Combining #sideOne.text #sideOne.hint #sideOne.translit #sideTwo.text #sideTwo.hint #sideTwo.translit' )
    def 'exercise newHid'() {
        given: 'valid subject under test'
        def sut = new HidBuilder()
        sut.knownLegacyCode( randomHexString() )
        sut.learningLegacyCode( randomHexString() )

        when:
        def results = sut.newHid( sideOne, sideTwo )

        then: 'the expected hid is generated'
        results

        where:
        sideOne                       | sideTwo
        phrase( true, true, true )    | phrase( true, true, true )
        phrase( false, true, true )   | phrase( true, true, true )
        phrase( false, false, true )  | phrase( true, true, true )
        phrase( false, false, false ) | phrase( true, true, true )
    }

    @Ignore
    @Unroll( '#sideOne.text/#sideTwo.text hashes to #expected')
    def 'for Mr. Davidson'() {
        given: 'valid subject under test'
        def sut = new HidBuilder()
        sut.knownLegacyCode( 'ENGLISH' )
        sut.learningLegacyCode( 'FRENCH' )

        when:
        def results = sut.newHid( sideOne, sideTwo )

        then: 'the expected hid is generated'
        log.debug( '{}/{} hashes to {}', sideOne.text, sideTwo.text, results )
        results == expected

        where:
        sideOne                      | sideTwo                           || expected
        phrase( 'order' )            | phrase( 'la commande' )           || 'OWUW6UVQ2RVXGYM5'
        phrase( 'to fill an order' ) | phrase( 'remplir une commande' )  || 'OWUW6UVQ2RVXGYM5'
        phrase( 'to pay taxes' )     | phrase( 'payer des impôts' )      || 'OWUW6UVQ2RVXGYM5'
        phrase( 'payment in kind' )  | phrase( 'le versement anticipé' ) || 'OWUW6UVQ2RVXGYM5'
    }

    Phrase phrase( String text  ) {
        new Phrase( text, null, null )
    }

    Phrase phrase( boolean text, boolean hint, boolean transliteration ) {
        def randomText = text ? randomHexString() : null
        def randomHint = hint ? randomHexString() : null
        def randomTransliteration = transliteration ? randomHexString() : null
        new Phrase( randomText, randomHint, randomTransliteration )
    }
}
