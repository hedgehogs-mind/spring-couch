package com.hedgehogsmind.springcouchrest.util;

import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public final class RequestUtil {

    private RequestUtil() {}

    /**
     * In case as locale is specified, it is returned. Otherwise the default one.
     *
     * @param request Request to try to fetch locale from.
     * @param defaultLocale Fallback locale.
     * @return Request's locale or given defaultLocale.
     */
    public static Locale fetchLocale(final HttpServletRequest request, final Locale defaultLocale) {
        return request.getLocales().hasMoreElements() ?
                request.getLocale() :
                defaultLocale;
    }

    /**
     * Fetches path using {@link UrlPathHelper} and normalizes the value with
     * {@link PathUtil#normalizeWithTrailingSlash(String)}.
     *
     * @param request Request to get
     * @return
     */
    public static String getRequestPathWithTrailingSlash(final HttpServletRequest request) {
        return PathUtil.normalizeWithTrailingSlash(
                UrlPathHelper.defaultInstance.getPathWithinApplication(request)
        );
    }

}
