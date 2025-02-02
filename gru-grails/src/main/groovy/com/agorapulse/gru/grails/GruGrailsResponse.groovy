package com.agorapulse.gru.grails

import com.agorapulse.gru.Client
import com.agorapulse.gru.cookie.Cookie
import org.grails.plugins.testing.GrailsMockHttpServletResponse

/**
 * Wrapper around mock Grails response.
 */
class GruGrailsResponse implements Client.Response {

    final GrailsMockHttpServletResponse response

    GruGrailsResponse(GrailsMockHttpServletResponse response) {
        this.response = response
    }

    @Override
    int getStatus() {
        return response.status
    }

    @Override
    List<String> getHeaders(String name) {
        return response.headers(name)
    }

    @Override
    String getText() {
        return response.text
    }

    @Override
    String getRedirectUrl() {
        return response.redirectUrl
    }

    @Override
    List<Cookie> getCookies() {
        List<javax.servlet.http.Cookie> cookies = response.cookies ? response.cookies.toList() : []

        return cookies.collect {
            Cookie.Builder builder = new Cookie.Builder()
                .name(it.name)
                .value(it.value)

            // TODO: expires?

            if (it.domain) {
                builder.domain(it.domain)
            }

            builder
                .httpOnly(it.httpOnly)
                .path(it.path)
                .secure(it.secure)
                .build()
        }
    }
}
