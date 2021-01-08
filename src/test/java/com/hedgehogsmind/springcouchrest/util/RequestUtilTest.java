package com.hedgehogsmind.springcouchrest.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestUtilTest {

    @Test
    public void testFetchExistingLocale() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getLocales()).thenReturn(Collections.enumeration(List.of(Locale.KOREAN)));
        when(request.getLocale()).thenReturn(Locale.KOREAN);

        Assertions.assertEquals(
                Locale.KOREAN,
                RequestUtil.fetchLocale(request, Locale.JAPANESE)
        );
    }

    @Test
    public void testDefaultLocale() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getLocales()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(request.getLocale()).thenReturn(Locale.getDefault());

        Assertions.assertEquals(
                Locale.JAPANESE,
                RequestUtil.fetchLocale(request, Locale.JAPANESE)
        );
    }

    @Test
    public void testGetPathWithoutTrailingSlash() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("hello/world");

        Assertions.assertEquals(
                "hello/world/",
                RequestUtil.getRequestPathWithTrailingSlash(request)
        );
    }

    @Test
    public void testGetPathWithTrailingSlash() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("hello/world/");

        Assertions.assertEquals(
                "hello/world/",
                RequestUtil.getRequestPathWithTrailingSlash(request)
        );
    }

}
