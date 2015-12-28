/*
 * Copyright (c) 2015 Transparent Language.  All rights reserved.
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
