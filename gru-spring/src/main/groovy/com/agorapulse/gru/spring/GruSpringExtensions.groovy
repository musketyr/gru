package com.agorapulse.gru.spring

import com.agorapulse.gru.RequestDefinitionBuilder
import com.agorapulse.gru.ResponseDefinitionBuilder
import com.agorapulse.gru.spring.minions.RequestBuilderMinion
import com.agorapulse.gru.spring.minions.ResultMatcherMinion
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import space.jasan.support.groovy.closure.ConsumerWithDelegate

import java.util.function.Consumer

/**
 * Add convenient methods for Grails to test definition DSL.
 */
class GruSpringExtensions {

    static RequestDefinitionBuilder and(
        RequestDefinitionBuilder self,
        @DelegatesTo(value = MockHttpServletRequestBuilder, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType, options = 'org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder')
            Closure<MockHttpServletRequestBuilder> step
    ) {
        and(self, ConsumerWithDelegate.create(step))
    }

    static RequestDefinitionBuilder and(RequestDefinitionBuilder self, Consumer<MockHttpServletRequestBuilder> step) {
        request(self, step)
    }

    static RequestDefinitionBuilder request(
        RequestDefinitionBuilder self,
        @DelegatesTo(value = MockHttpServletRequestBuilder, strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = SimpleType, options = 'org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder')
            Closure<MockHttpServletRequestBuilder> step
    ) {
        request(self, ConsumerWithDelegate.create(step))
    }

    static RequestDefinitionBuilder request(RequestDefinitionBuilder self, Consumer<MockHttpServletRequestBuilder> step) {
        self.command(RequestBuilderMinion) {
            addBuildStep step
        }
    }

    static ResponseDefinitionBuilder that(ResponseDefinitionBuilder self, ResultMatcher matcher) {
        self.command(ResultMatcherMinion) {
            addMatcher matcher
        }
    }

    static ResponseDefinitionBuilder and(ResponseDefinitionBuilder self, ResultMatcher matcher) {
        that self, matcher
    }

}
