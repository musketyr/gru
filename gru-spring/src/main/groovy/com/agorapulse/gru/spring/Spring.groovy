package com.agorapulse.gru.spring

import com.agorapulse.gru.AbstractClient
import com.agorapulse.gru.GruContext
import com.agorapulse.gru.Squad
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder

import java.util.function.Consumer

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request

/**
 * Gru implementation based on MockMvc.
 */
class Spring extends AbstractClient {

    static Spring steal(Object unitTest) {
        return new Spring(unitTest)
    }

    private GruSpringRequest request
    private GruSpringResponse response

    private Spring(Object unitTest) {
        super(unitTest)
        reset()
    }

    @Override
    GruSpringRequest getRequest() {
        return request
    }

    @Override
    GruSpringResponse getResponse() {
        return response
    }

    @Override
    void reset() {
        request = new  GruSpringRequest()
        response = null
    }

    @Override
    @SuppressWarnings('Instanceof')
    GruContext run(Squad squad, GruContext context) {
        MockMvc mockMvc

        if (unitTest.hasProperty('mockMvc')) {
            mockMvc = unitTest.mockMvc as MockMvc
        } else if (unitTest.hasProperty('mvc')) {
            mockMvc = unitTest.mvc as MockMvc
        } else {
            mockMvc = unitTest.properties.find { name, value -> value instanceof MockMvc }?.value as MockMvc
        }

        if (!mockMvc) {
            throw new IllegalStateException('MockMvc is missing in the unit test or it is null. ' +
                'Please provide \'@Autowired MockMvc mockMvc\' field in your specification')
        }

        MockHttpServletRequestBuilder builder = request.multipart ? fileUpload(requestURI) : request(request.method, requestURI)
        for (Consumer step in request.steps) {
            step.accept(builder)
        }
        if (request.multipart) {
            MockMultipartHttpServletRequestBuilder upload = builder as MockMultipartHttpServletRequestBuilder
            request.multipart.parameters.each { k, v ->
                builder.param(k, v ? String.valueOf(v) : null)
            }
            request.multipart.files.each { k, f ->
                upload.file(new MockMultipartFile(
                    f.parameterName,
                    f.filename,
                    f.contentType,
                    f.bytes
                ))
            }
        }
        MvcResult result = mockMvc.perform(builder).andReturn()
        response = new GruSpringResponse(result.response)
        return context.withResult(result)
    }

    @Override
    Object getUnitTest() {
        super.unitTest
    }
    private URI getRequestURI() {
        new URI("${request.baseUri ?: ''}${request.uri ?: ''}".replaceAll('/+', '/'))
    }
}
