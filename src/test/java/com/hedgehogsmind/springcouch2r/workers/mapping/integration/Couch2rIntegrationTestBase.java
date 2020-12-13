package com.hedgehogsmind.springcouch2r.workers.mapping.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.beans.Couch2rCore;
import com.hedgehogsmind.springcouch2r.beans.Couch2rHandlerAdapter;
import com.hedgehogsmind.springcouch2r.beans.Couch2rHandlerMapping;
import com.hedgehogsmind.springcouch2r.beans.EnableCouch2r;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rMapping;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.HandlerExecutionChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.Mockito.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class Couch2rIntegrationTestBase {

    @SpringBootApplication(scanBasePackageClasses = Couch2rIntegrationTestBase.class)
    @EnableJpaRepositories(considerNestedRepositories = true)
    @EnableCouch2r
    @Import(Config.class)
    @EntityScan(basePackageClasses = Couch2rIntegrationTestBase.class)
    public static class App {
    }

    public static class Config implements Couch2rConfiguration {
        @Override
        public String getCouch2rBasePath() {
            return "/junit/api/couch2r/";
        }

        @Override
        public Optional<ObjectMapper> getCouch2rObjectMapper() {
            return Optional.empty();
        }
    }

    @Autowired
    public Couch2rCore core;

    @Autowired
    public Couch2rHandlerMapping handlerMapping;

    @Autowired
    public Couch2rHandlerAdapter handlerAdapter;

    /**
     * Mocked response re initialized before each test method.
     */
    protected HttpServletResponse response;

    /**
     * Mocked response writer re initialized before each test method.
     */
    protected PrintWriter responseWriter;

    /**
     * Creates a mocked request.
     * @param method HTTP method.
     * @param path Request path.
     * @return Mocked request.
     */
    protected HttpServletRequest mockRequest(final String method, final String path) {
        final HttpServletRequest mockedRequest = mock(HttpServletRequest.class);

        when(mockedRequest.getLocales()).thenReturn(Collections.enumeration(List.of(Locale.ENGLISH)));
        when(mockedRequest.getLocale()).thenReturn(Locale.ENGLISH);

        when(mockedRequest.getContextPath()).thenReturn("");
        when(mockedRequest.getRequestURI()).thenReturn(path);
        when(mockedRequest.getMethod()).thenReturn(method.toUpperCase());

        return mockedRequest;
    }

    private PrintWriter mockWriter() {
        final PrintWriter mockWriter = mock(PrintWriter.class);
        return mockWriter;
    }

    private HttpServletResponse mockResponse(final PrintWriter writer) {
        try {
            final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            when(mockResponse.getWriter()).thenReturn(writer);
            return mockResponse;
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    protected void setup() {
        responseWriter = mockWriter();
        response = mockResponse(responseWriter);
    }

    @AfterEach
    protected void tearDown() {
        responseWriter = null;
        response = null;
    }

    /**
     * Asserts that {@link Couch2rCore#getCouch2rMappings()} has a mapping for the given entity (class).
     *
     * @param entityClass Entity class to find {@link Couch2rMapping} for.
     */
    protected void assertMappingExists(final Class<?> entityClass) {
        final Optional<Couch2rMapping> entityMapping = core.getCouch2rMappings()
                .stream()
                .filter(mapping -> mapping.getEntityType().getJavaType().equals(entityClass))
                .findAny();

        if ( entityMapping.isEmpty() ) {
            Assertions.fail("No Couch2rMapping found for entity class "+entityClass.getName());
        }
    }

    /**
     * Convenience method. Fetches base path of core's couch2r config.
     * @return Base path (with trailing slash).
     */
    protected String getBasePath() {
        return core.getCouch2rConfiguration().getCouch2rBasePath();
    }

    /**
     * <p>
     *     First calls the {@link Couch2rHandlerMapping} to get a handler for the request.
     *     In case there is no handler, an {@link IllegalStateException} will be thrown.
     * </p>
     *
     * <p>
     *     Then calls {@link Couch2rHandlerAdapter#handle(HttpServletRequest, HttpServletResponse, Object)}
     *     with mocked request and response as well as the fetched handler.
     * </p>
     *
     * @param request Request to perform.
     */
    protected void perform(final HttpServletRequest request) {
        final HandlerExecutionChain executionChain = handlerMapping.getHandler(request);
        if ( executionChain == null ) {
            throw new IllegalStateException("No mapping for URI: "+request.getRequestURI());
        }

        if ( !(executionChain.getHandler() instanceof Couch2rMapping) ) {
            throw new IllegalStateException("HandlerMapping did not return Couch2rMapping handler, instead was: "+executionChain.getHandler());
        }

        handlerAdapter.handle(request, response, executionChain.getHandler());
    }

    /**
     * Fetches response status set on mocked response.
     * @return Status set in mocked response.
     */
    protected int getResponseStatus() {
        final ArgumentCaptor<Integer> argument = ArgumentCaptor.forClass(Integer.class);
        verify(response).setStatus(argument.capture());

        return argument.getValue();
    }

    /**
     * Fetches string which has been written to the response's mocked printer writer.
     * @return String written to mocked writer.
     */
    protected String getResponseBody() {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(responseWriter).print(argument.capture());

        return argument.getValue();
    }

    /**
     * Convenience method. Calls {@link #getResponseBody()} and tries to parse body as JSON.
     * @return Body as JSON.
     */
    protected JSONObject getResponseJson() {
        return new JSONObject(
                getResponseBody()
        );
    }

    /**
     * Parses #getResponseBody() as array.
     * @return JSON array.
     */
    protected JSONArray getResponseJsonArray() {
        return new JSONArray(
                getResponseBody()
        );
    }

}
