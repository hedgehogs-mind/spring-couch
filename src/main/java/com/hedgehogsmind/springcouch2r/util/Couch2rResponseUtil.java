package com.hedgehogsmind.springcouch2r.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.I18nProblemDetailConvertible;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertible;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Couch2rResponseUtil {

    /**
     * Simple wrapper for {@link #convertBody(Object, ObjectMapper, Locale)}.
     */
    public static class BodyConversionResult {
        private final String bodyAsString;
        private final String contentType;
        private final Optional<Integer> overwritingStatus;

        public BodyConversionResult(String bodyAsString, String contentType) {
            this.bodyAsString = bodyAsString;
            this.contentType = contentType;
            this.overwritingStatus = Optional.empty();
        }

        public BodyConversionResult(String bodyAsString, String contentType, int status) {
            this.bodyAsString = bodyAsString;
            this.contentType = contentType;
            this.overwritingStatus = Optional.of(status);
        }

        public String getBodyAsString() {
            return bodyAsString;
        }

        public String getContentType() {
            return contentType;
        }

        public Optional<Integer> getOverwritingStatus() {
            return overwritingStatus;
        }
    }

    /**
     * <p>
     *     To sum it up: Writes a ResponseEntity to the response.
     * </p>
     *
     * <p>
     *     First fetches tries to fetch locale from request. If none is specified, {@link Locale#ENGLISH} is used.
     * </p>
     *
     * <p>
     *     Then status and headers will be written to the response object.
     * </p>
     *
     * <p>
     *     The body, if present, will be converted to a
     *     string by using {@link #convertBody(Object, ObjectMapper, Locale)} representation and is
     *     then written to the response writer.
     * </p>
     *
     * @param responseEntity ResponseEntity to write to response.
     * @param request Request to check for locale.
     * @param response Response to write ResponseEntity to.
     * @param objectMapper ObjectMapper used to serialize generic object body.
     */
    public static void writeResponseEntity(
            final ResponseEntity responseEntity,
            final HttpServletRequest request,
            final HttpServletResponse response,
            final ObjectMapper objectMapper
            ) {

        final Locale locale = Couch2rRequestUtil.fetchLocale(request, Locale.ENGLISH);

        response.setStatus(responseEntity.getStatusCodeValue());
        responseEntity.getHeaders().forEach((name, value) -> {
            response.setHeader(name, value.stream().collect(Collectors.joining(", ")));
        });

        final Object body = responseEntity.getBody();

        if ( body != null ) {
            final BodyConversionResult conversionResult = convertBody(body, objectMapper, locale);

            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(conversionResult.getContentType());

            if ( conversionResult.getOverwritingStatus().isPresent() ) {
                response.setStatus(conversionResult.getOverwritingStatus().get());
            }

            if ( conversionResult.getBodyAsString() != null && !conversionResult.getBodyAsString().isEmpty() ) {
                try {
                    response.getWriter().print(conversionResult.getBodyAsString());
                } catch ( IOException e ) {
                    throw new RuntimeException("Could not write response body", e);
                }
            }
        }
    }

    /**
     * <p>
     *     Converts body to string body and returns appropriate content type.
     * </p>
     *
     * <p>
     *     First it will be checked, if the given body is a String, Boolean or {@link Number}.
     *     Is this the case, a string representation will be returned with content type
     *     plain text.
     * </p>
     *
     * <p>
     *     Then we check for {@link ProblemDetail}s or {@link ProblemDetailConvertible}s and return
     *     a {@link ProblemDetail}.
     * </p>
     *
     * <p>
     *     The last option is to try to serialize the given body object as JSON.
     * </p>
     *
     * @param body Body object.
     * @param objectMapper ObjectMapper for JSON serialization.
     * @param locale Locale for which to localize ProblemDetails for.
     * @return Conversion result.
     */
    public static BodyConversionResult convertBody(
            final Object body,
            final ObjectMapper objectMapper,
            final Locale locale
    ) {
        if ( body instanceof String ||
                body instanceof Boolean ||
                body instanceof Number ) {

            return new BodyConversionResult(
                    String.valueOf(body),
                    MediaType.TEXT_PLAIN_VALUE
            );

        } else if ( body instanceof ProblemDetail || body instanceof ProblemDetailConvertible ) {

            ProblemDetail problemDetail = null;

            if ( body instanceof I18nProblemDetailConvertible ) {
                problemDetail = ((I18nProblemDetailConvertible)body).toProblemDetail(locale);
            } else if ( body instanceof  ProblemDetailConvertible ) {
                problemDetail = ((ProblemDetailConvertible)body).toProblemDetail();
            } else if ( body instanceof ProblemDetail ) {
                problemDetail = (ProblemDetail) body;
            } else {
                throw new UnsupportedOperationException("problem detail (convertible) type not handled yet");
            }

            final String json = problemDetail.toJson();
            return new BodyConversionResult(
                    json,
                    ProblemDetail.CONTENT_TYPE.toString(),
                    problemDetail.getStatus()
            );

        } else {
            try {
                final String json = objectMapper.writeValueAsString(body);

                return new BodyConversionResult(
                        json,
                        MediaType.APPLICATION_JSON_VALUE
                );
            } catch ( JsonProcessingException e ) {
                // TODO @peter throw problem detail exception
                throw new RuntimeException("Could not write body as JSON", e);
            }
        }
    }


}
