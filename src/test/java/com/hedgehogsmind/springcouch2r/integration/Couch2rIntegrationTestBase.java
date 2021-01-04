package com.hedgehogsmind.springcouch2r.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.beans.Couch2rCore;
import com.hedgehogsmind.springcouch2r.beans.EnableCouch2r;
import com.hedgehogsmind.springcouch2r.configuration.Couch2rConfiguration;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.I18nProblemDetailDescriptor;
import com.hedgehogsmind.springcouch2r.workers.mapping.entity.MappedEntityResource;
import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class Couch2rIntegrationTestBase {

    @SpringBootApplication(scanBasePackageClasses = Couch2rIntegrationTestBase.class)
    @EnableJpaRepositories(considerNestedRepositories = true)
    @EnableCouch2r
    @Import(Config.class)
    @EntityScan(basePackageClasses = Couch2rIntegrationTestBase.class)
    @EnableWebMvc
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

    @LocalServerPort
    public int port;

    protected int lastStatusCode = -1;

    /**
     * Checks that the {@link Couch2rCore} holds a {@link MappedEntityResource}.
     * for the given entity class.
     *
     * @param entityClass Entity class to look for.
     */
    protected void assertEntityMappingExists(final Class<?> entityClass) {
        final Optional<MappedEntityResource> entityMapping = core.getMappedResources()
                .stream()
                .filter(m -> m instanceof MappedEntityResource)
                .map(m -> (MappedEntityResource) m)
                .filter(em -> em.getEntityType().getJavaType().equals(entityClass))
                .findAny();

        if ( entityMapping.isEmpty() ) {
            Assertions.fail("No mapping for entity of type "+entityClass+" found");
        }
    }

    /**
     * Fetches base path of {@link Couch2rConfiguration}.
     *
     * @return Base path.
     */
    protected String getBasePath() {
        return core.getCouch2rConfiguration().getCouch2rBasePath();
    }

    /**
     * Performs a http request against the local test server. Stores status code in
     * {@link #lastStatusCode}.
     *
     * @param path Path starting with leading slash.
     * @param method HTTP method.
     * @param jsonBody Optional. Null or JSON payload as String.
     * @return Response.
     */
    protected String perform(final String path, final String method, final String jsonBody) {
        if ( !path.startsWith("/") ) {
            throw new IllegalArgumentException("path must start with leading slash");
        }

        final OkHttpClient httpClient = new OkHttpClient();

        final Request request = new Request.Builder()
                .method(
                        method,
                        jsonBody != null ?
                                RequestBody.create(
                                        MediaType.parse("application/json"),
                                        jsonBody
                                )
                                : null
                )
                .url("http://localhost:"+port+path)
                .build();

        try {
            final Response response = httpClient.newCall(request).execute();
            lastStatusCode = response.code();

            return new String(response.body().bytes());
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs get call.
     *
     * @param path Path with leading slash.
     * @return Response.
     */
    protected String get(final String path) {
        return perform(path, "GET", null);
    }

    /**
     * Same as {@link #get(String)} but tries to parse response as {@link JSONObject}.
     *
     * @param path Path with leading slash.
     * @return JSON response.
     */
    protected JSONObject getWithJsonObjectResponse(final String path) {
        return new JSONObject(get(path));
    }

    /**
     * Same as {@link #get(String)} but tries to parse response as {@link JSONArray}.
     *
     * @param path Path with leading slash.
     * @return JSON array response.
     */
    protected JSONArray getWithJsonArrayResponse(final String path) {
        return new JSONArray(get(path));
    }

    /**
     * Performs POST call.
     *
     * @param path Path with leading slash.
     * @param jsonBody JSON payload.
     * @return Response.
     */
    protected String post(final String path, final String jsonBody) {
        return perform(path, "POST", jsonBody);
    }

    /**
     * Same as {@link #post(String, String)} but tries to parse response as {@link JSONObject}.
     *
     * @param path Path with leading slash.
     * @param jsonBody Body payload.
     * @return JSON response.
     */
    protected JSONObject postWithJsonObjectResponse(final String path, final String jsonBody) {
        return new JSONObject(post(path, jsonBody));
    }

    /**
     * Same as {@link #post(String, String)} but tries to parse response as {@link JSONArray}.
     *
     * @param path Path with leading slash.
     * @param jsonBody Body payload.
     * @return JSON array response.
     */
    protected JSONArray postWithJsonArrayResponse(final String path, final String jsonBody) {
        return new JSONArray(post(path, jsonBody));
    }

    /**
     * Checks that the given response object carries a
     * {@link com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetail} with a type
     * equal to the one held by the given descriptor.
     *
     * @param descriptor Descriptor to get expected ProblemDetail type from.
     * @param response Response to check for ProblemDetail and its type.
     */
    protected void assertProblemDetailGiven(final I18nProblemDetailDescriptor descriptor,
                                            final JSONObject response) {

        Assertions.assertTrue(response.has("type"));
        Assertions.assertEquals(descriptor.getType().toString(), response.getString("type"));
    }

}
