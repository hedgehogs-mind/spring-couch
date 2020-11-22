package com.hedgehogsmind.springcouch2r.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Couch2rRequestUtilTest {

    @Test
    public void testFetchExistingLocale() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getLocales()).thenReturn(Collections.enumeration(List.of(Locale.KOREAN)));
        when(request.getLocale()).thenReturn(Locale.KOREAN);

        Assertions.assertEquals(
                Locale.KOREAN,
                Couch2rRequestUtil.fetchLocale(request, Locale.JAPANESE)
        );
    }

    @Test
    public void testDefaultLocale() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getLocales()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(request.getLocale()).thenReturn(Locale.getDefault());

        Assertions.assertEquals(
                Locale.JAPANESE,
                Couch2rRequestUtil.fetchLocale(request, Locale.JAPANESE)
        );
    }

}
