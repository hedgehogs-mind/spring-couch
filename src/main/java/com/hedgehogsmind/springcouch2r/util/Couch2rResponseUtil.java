package com.hedgehogsmind.springcouch2r.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertible;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

public final class Couch2rResponseUtil {

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
     *     Last step is to write the body to the response (in case it is not null).
     *     This step is a bit more complex: First the body will be converted into a String:
     *
     *     <ul>
     *         <li>
     *             Body is {@link String}, {@link Boolean} or {@link Number},
     *             then the value will be written as a plain String.
     *         </li>
     *         <li>
     *             Body is assignable to {@link ProblemDetailConvertible}:
     *             New ProblemDetail instance is created using fetched locale.
     *             Then this instance is used as if the body is a already a ProblemDetail.
     *             See next case.
     *         </li>
     *         <li>
     *             Body is a ProblemDetail: Use {@link ProblemDetail#serializeAsJson()} as body.
     *         </li>
     *         <li>
     *             Otherwise: We use the given {@link ObjectMapper} to serialize the body as a String.
     *         </li>
     *     </ul>
     *
     *     Then the character encoding is set to UTF-8 and the body written using the response's writer.
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
            String bodyToWrite = null;

            if ( body instanceof String ||
                    body instanceof Boolean ||
                    body instanceof Number ) {

                bodyToWrite = String.valueOf(body);
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);

            } else if ( body instanceof ProblemDetail || body instanceof ProblemDetailConvertible) {

                ProblemDetail problemDetail = null;

                if ( body instanceof ProblemDetailConvertible ) {
                    problemDetail = ((ProblemDetailConvertible)body).toProblemDetail(locale);
                } else {
                    problemDetail = (ProblemDetail) body;
                }

                bodyToWrite = problemDetail.serializeAsJson();
                response.setContentType(ProblemDetail.MEDIA_TYPE.toString());

            } else {
                try {
                    bodyToWrite = objectMapper.writeValueAsString(body);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                } catch ( JsonProcessingException e ) {
                    throw new RuntimeException("Could not write body as JSON", e);
                }
            }

            if ( bodyToWrite == null ) throw new IllegalStateException("Body stringify failed.");

            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            try {
                response.getWriter().print(bodyToWrite);
            } catch ( IOException e ) {
                throw new RuntimeException("Could not write response body", e);
            }
        }
    }

}
