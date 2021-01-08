package com.hedgehogsmind.springcouchrest.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.AttributedI18nProblemDetailDescriptor;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.I18nProblemDetailDescriptor;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetailDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ResponseUtilTest {

    private URI newInstanceURI() {
        return URI.create("urn:uuid:"+ UUID.randomUUID());
    }

    private static final ObjectMapper OM = new ObjectMapper();

    protected PrintWriter writer() {
        final PrintWriter mockWriter = mock(PrintWriter.class);
        return mockWriter;
    }

    protected HttpServletRequest request() {
        final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getLocales()).thenReturn(Collections.enumeration(Collections.emptyList()));
        return mockRequest;
    }

    protected HttpServletRequest requestWithLocale() {
        final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getLocales()).thenReturn(Collections.enumeration(List.of(Locale.ENGLISH)));
        when(mockRequest.getLocale()).thenReturn(Locale.ENGLISH);
        return mockRequest;
    }

    protected HttpServletResponse response(final PrintWriter writer) {
        try {
            final HttpServletResponse mockResponse = mock(HttpServletResponse.class);
            when(mockResponse.getWriter()).thenReturn(writer);
            return mockResponse;
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }

    protected HttpServletResponse response() {
        return response(writer());
    }

    private String removeInstancePart(final String value) {
        return value.replaceAll("\"instance\":\"urn:uuid:[0-9a-zA-Z-]+\"", "");
    }

    private void checkProblemDetail(final PrintWriter writerMock, final ProblemDetail problemDetail) {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(writerMock).print(argument.capture());

        final String expected = removeInstancePart(problemDetail.toJson());
        final String actual = removeInstancePart(argument.getValue());

        Assertions.assertEquals(
                expected,
                actual
        );
    }

    @Test
    public void testStatusSetCorrectly() {
        final ResponseEntity re = ResponseEntity.status(302).build();

        final HttpServletRequest request = request();
        final HttpServletResponse response = response();

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(response).setStatus(302);
    }

    @Test
    public void testHeadersPopulated() {
        final ResponseEntity re = ResponseEntity.ok()
                .header("H1", "V1")
                .header("H2", "V2.1", "V2.2", "V2.3")
                .build();

        final HttpServletRequest request = request();
        final HttpServletResponse response = response();

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(response).setHeader("H1", "V1");
        verify(response).setHeader("H2", "V2.1, V2.2, V2.3");
    }

    @Test
    public void testCharacterEncodingNotSet() {
        final ResponseEntity re = ResponseEntity.ok().build();

        final HttpServletRequest request = request();
        final HttpServletResponse response = response();

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(response, never()).setCharacterEncoding("UTF-8");
    }

    @Test
    public void testCharacterEncodingSet() {
        final ResponseEntity re = ResponseEntity.ok("Hello");

        final HttpServletRequest request = request();
        final HttpServletResponse response = response();

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    public void testStringBody() {
        final String body = "This is a test! äüöéê∂†®";
        final ResponseEntity re = ResponseEntity.ok(body);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(writer).print(body);
    }

    @Test
    void testBooleanBody() {
        final ResponseEntity re = ResponseEntity.ok(true);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(writer).print("true");
    }

    @Test
    void testLongBody() {
        final ResponseEntity re = ResponseEntity.ok(12345L);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(writer).print("12345");
    }

    @Test
    void testIntegerBody() {
        final ResponseEntity re = ResponseEntity.ok(54321);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(writer).print("54321");
    }

    @Test
    void testDoubleBody() {
        final ResponseEntity re = ResponseEntity.ok(2.345);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(writer).print("2.345");
    }

    @Test
    void testFloatBody() {
        final ResponseEntity re = ResponseEntity.ok(1.05f);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(writer).print("1.05");
    }

    @Test
    void testPlainProblemDetailBody() {
        final ProblemDetail detail = new ProblemDetailDto(
                "abc123",
                "Error",
                "Somewhat bad happened",
                500,
                newInstanceURI()
        );

        final ResponseEntity re = ResponseEntity.ok(detail);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        checkProblemDetail(writer, detail);
    }

    @Test
    void testProblemDescriptorBody() {
        final I18nProblemDetailDescriptor descriptor = new I18nProblemDetailDescriptor(
                "testType",
                "test.test.test",
                "test.test.test",
                400
        );

        final ResponseEntity re = ResponseEntity.ok(descriptor);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        checkProblemDetail(writer, descriptor.toProblemDetail(Locale.ENGLISH));
    }

    @Test
    void testProblemDescriptorBodyWithLocale() {
        final I18nProblemDetailDescriptor descriptor = new I18nProblemDetailDescriptor(
                "testType",
                "test.test.test",
                "test.test.test",
                400
        );

        final ResponseEntity re = ResponseEntity.ok(descriptor);

        final PrintWriter writer = writer();
        final HttpServletRequest request = requestWithLocale(); // Only difference (compared to testProblemDescriptorBody() )
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        checkProblemDetail(writer, descriptor.toProblemDetail(Locale.ENGLISH));
    }

    @Test
    void testAttributedProblemDescriptorBody() {
        final I18nProblemDetailDescriptor descriptor = new I18nProblemDetailDescriptor(
                "testType",
                "test.test.test",
                "test.test.test",
                400
        );

        final AttributedI18nProblemDetailDescriptor attributed = descriptor.withAttributes();
        attributed.addAttribute("a1", "v1");
        attributed.addAttribute("a2", "v2");

        final ResponseEntity re = ResponseEntity.ok(attributed);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        checkProblemDetail(writer, attributed.toProblemDetail(Locale.ENGLISH));
    }

    public static class SomeDto {
        public String a;
        public int b;
    }

    @Test
    void testArbitraryObjectBody() throws JsonProcessingException {
        final SomeDto body = new SomeDto();
        body.a = "Hello World!";
        body.b = 1;

        final ResponseEntity re = ResponseEntity.ok(body);

        final PrintWriter writer = writer();
        final HttpServletRequest request = request();
        final HttpServletResponse response = response(writer);

        ResponseUtil.writeResponseEntity(re, request, response, OM);

        verify(writer).print(OM.writeValueAsString(body));
    }
}
