package com.hedgehogsmind.springcouchrest.integration;

import com.hedgehogsmind.springcouchrest.annotations.EnableCouchRest;
import com.hedgehogsmind.springcouchrest.beans.CouchRestCore;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfiguration;
import com.hedgehogsmind.springcouchrest.configuration.CouchRestConfigurationAdapter;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.I18nProblemDetailDescriptor;
import com.hedgehogsmind.springcouchrest.workers.mapping.entity.MappedEntityResource;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Configuration
@DirtiesContext
public abstract class CouchRestIntegrationTestBase {

    protected static final String LOGIN_URL = "/do_login";

    protected static final String LOGIN_SUCCESS_URL = "/success_login";

    protected static final String LOGOUT_URL = "/do_logout";

    protected static final String LOGOUT_SUCCESS_URL = "/success_logout";

    protected static ThreadLocal<String> TEST_BASE_SEC_RULE = new ThreadLocal<>();

    protected static ThreadLocal<String> TEST_DEFAULT_ENDPOINT_SEC_RULE = new ThreadLocal<>();

    public CouchRestIntegrationTestBase() {
        TEST_BASE_SEC_RULE.set(getBaseSecurityRule());
        TEST_DEFAULT_ENDPOINT_SEC_RULE.set(getDefaultEndpointSecurityRule());
    }

    protected String getBaseSecurityRule() {
        return "permitAll()";
    }

    protected String getDefaultEndpointSecurityRule() {
        return "permitAll()";
    }

    @SpringBootApplication(scanBasePackageClasses = CouchRestIntegrationTestBase.class)
    @EnableJpaRepositories(considerNestedRepositories = true)
    @EnableCouchRest
    @EntityScan(basePackageClasses = CouchRestIntegrationTestBase.class)
    @Import({SpringSecurityConfig.class})
    public static class App {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new TestSingleUserDetailsService(passwordEncoder());
        }

        @Bean
        public CouchRestConfiguration couchRestConfiguration() {
            return new CouchRestConfigurationAdapter() {
                @Override
                public String getBaseSecurityRule() {
                    return TEST_BASE_SEC_RULE.get();
                }

                @Override
                public String getDefaultEndpointSecurityRule() {
                    return TEST_DEFAULT_ENDPOINT_SEC_RULE.get();
                }
            };
        }

    }

    public static class SpringSecurityConfig
            extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET).permitAll()
                    .antMatchers(HttpMethod.POST).permitAll()
                    .antMatchers(HttpMethod.DELETE).permitAll()

                    .and()
                    .formLogin()
                    .loginProcessingUrl(LOGIN_URL)
                    .defaultSuccessUrl(LOGIN_SUCCESS_URL)

                    .and()
                    .logout()
                    .logoutUrl(LOGOUT_URL)
                    .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                    .deleteCookies("JSESSIONID");

        }
    }

    @Autowired
    protected CouchRestCore core;

    @Autowired
    protected TestSingleUserDetailsService userDetailsService;

    @LocalServerPort
    protected int port;

    protected int lastStatusCode = -1;

    protected OkHttpClient httpClient;

    @BeforeEach
    public void resetTestUser() {
        userDetailsService.resetTestUser();
    }

    @BeforeEach
    public void setupHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .cookieJar(new DumbCookieJar())
                .build();

    }

    @AfterEach
    public void logoutAtTheEnd() {
        logout();
    }

    /**
     * Performs HTTP call against Spring Security's form login.
     */
    protected void login() {
        final RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", userDetailsService.testUser.username)
                .addFormDataPart("password", userDetailsService.testUser.password)
                .build();

        final Request request = new Request.Builder()
                .url("http://localhost:"+port+LOGIN_URL)
                .post(formBody)
                .build();

        try {
            final Response response = httpClient.newCall(request).execute();
            if ( response.code() != 302 &&
                    response.header("Location", "/") !=
                            ("http://localhost:"+port+LOGIN_SUCCESS_URL) ) {

                throw new IllegalStateException("Login failed: "+response.toString());
            }
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls Spring Security's logout url.
     */
    protected void logout() {
        final Request request = new Request.Builder()
                .url("http://localhost:"+port+LOGOUT_URL)
                .get()
                .build();

        try {
            final Response response = httpClient.newCall(request).execute();
            if ( response.code() != 302 &&
                    response.header("Location", "/") !=
                            ("http://localhost:"+port+LOGOUT_SUCCESS_URL) ) {

                throw new IllegalStateException("Logout failed: "+response.toString());
            }
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets authorities of test user stored in userDetailsService.
     *
     * @param authorities Authorities.
     */
    protected void setAuthoritiesOfTestUser(final String... authorities) {
        userDetailsService.testUser.authorities = new HashSet<>(
                Arrays.asList(authorities)
        );
    }

    /**
     * Checks that the {@link CouchRestCore} holds a {@link MappedEntityResource}.
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

        if (entityMapping.isEmpty()) {
            Assertions.fail("No mapping for entity of type " + entityClass + " found");
        }
    }

    /**
     * Fetches base path of {@link CouchRestConfiguration}.
     *
     * @return Base path.
     */
    protected String getBasePath() {
        return core.getCouchRestConfiguration().getCouchRestBasePath();
    }

    /**
     * Performs a http request against the local test server. Stores status code in
     * {@link #lastStatusCode}.
     *
     * @param path     Path starting with leading slash.
     * @param method   HTTP method.
     * @param jsonBody Optional. Null or JSON payload as String.
     * @return Response.
     */
    protected String perform(final String path, final String method, final String jsonBody) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("path must start with leading slash");
        }

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
                .url("http://localhost:" + port + path)
                .build();

        try {
            final Response response = httpClient.newCall(request).execute();
            lastStatusCode = response.code();

            return new String(response.body().bytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses body as json object.
     *
     * @param body Body.
     * @return Object from body or empty object if body is empty.
     */
    protected JSONObject parseJsonObject(final String body) {
        return body.isBlank() ?
                new JSONObject() :
                new JSONObject(body);
    }

    /**
     * Parses body as json array.
     *
     * @param body Body.
     * @return Array of body or empty array if body is empty.
     */
    protected JSONArray parseJsonArray(final String body) {
        return body.isBlank() ?
                new JSONArray() :
                new JSONArray(body);
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
        return parseJsonObject(get(path));
    }

    /**
     * Same as {@link #get(String)} but tries to parse response as {@link JSONArray}.
     *
     * @param path Path with leading slash.
     * @return JSON array response.
     */
    protected JSONArray getWithJsonArrayResponse(final String path) {
        return parseJsonArray(get(path));
    }

    /**
     * Performs POST call.
     *
     * @param path     Path with leading slash.
     * @param jsonBody JSON payload.
     * @return Response.
     */
    protected String post(final String path, final String jsonBody) {
        return perform(path, "POST", jsonBody);
    }

    /**
     * Same as {@link #post(String, String)} but tries to parse response as {@link JSONObject}.
     *
     * @param path     Path with leading slash.
     * @param jsonBody Body payload.
     * @return JSON response.
     */
    protected JSONObject postWithJsonObjectResponse(final String path, final String jsonBody) {
        return parseJsonObject(post(path, jsonBody));
    }

    /**
     * Same as {@link #post(String, String)} but tries to parse response as {@link JSONArray}.
     *
     * @param path     Path with leading slash.
     * @param jsonBody Body payload.
     * @return JSON array response.
     */
    protected JSONArray postWithJsonArrayResponse(final String path, final String jsonBody) {
        return parseJsonArray(post(path, jsonBody));
    }

    /**
     * Convenience method. Calls {@link #perform(String, String, String)} with method DELETE and no body.
     *
     * @param path Path to perform delete call against.
     * @return Response body as string.
     */
    protected String delete(final String path) {
        return perform(path, "DELETE", null);
    }

    /**
     * Sames as {@link #delete(String)}, but tries to parse response body as JSON.
     *
     * @param path Path to perform delete call against.
     * @return Response as JSON object.
     */
    protected JSONObject deleteWithJsonObjectResponse(final String path) {
        return parseJsonObject(delete(path));
    }

    /**
     * Checks that the given response object carries a
     * {@link com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetail} with a type
     * equal to the one held by the given descriptor. Also checks last status code to match the one
     * held in the descriptor.
     *
     * @param descriptor Descriptor to get expected ProblemDetail type from.
     * @param response   Response to check for ProblemDetail and its type.
     */
    protected void assertProblemDetailGiven(final I18nProblemDetailDescriptor descriptor,
                                            final JSONObject response) {
        assertStatusCode(descriptor.getStatus());
        Assertions.assertTrue(response.has("type"));
        Assertions.assertEquals(descriptor.getType().toString(), response.getString("type"));
    }

    /**
     * Checks that {@link #lastStatusCode} mathches the given one.
     *
     * @param expected Expected status code.
     */
    protected void assertStatusCode(int expected) {
        Assertions.assertEquals(expected, lastStatusCode, "Status codes do not match");
    }

}
