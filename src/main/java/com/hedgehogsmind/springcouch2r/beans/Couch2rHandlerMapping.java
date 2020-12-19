package com.hedgehogsmind.springcouch2r.beans;

import com.hedgehogsmind.springcouch2r.util.Couch2rRequestUtil;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rMappedResource;
import org.springframework.core.Ordered;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

public class Couch2rHandlerMapping implements HandlerMapping, Ordered {

    private final Couch2rCore couch2rCore;

    private final List<HandlerInterceptor> interceptors;

    private final Map<String, Couch2rMappedResource> mappedResourceCache = new HashMap<>();

    private final WebMvcConfigurationSupport webMvcConfigurationSupport;

    private final FormattingConversionService formattingConversionService;

    private final ResourceUrlProvider resourceUrlProvider;

    public Couch2rHandlerMapping(
            Couch2rCore couch2rCore,
            WebMvcConfigurationSupport webMvcConfigurationSupport,
            FormattingConversionService formattingConversionService,
            ResourceUrlProvider resourceUrlProvider
    ) {
        this.couch2rCore = couch2rCore;
        this.webMvcConfigurationSupport = webMvcConfigurationSupport;
        this.formattingConversionService = formattingConversionService;
        this.resourceUrlProvider = resourceUrlProvider;
        this.interceptors = new ArrayList<>();
    }

    @PostConstruct
    public void fetchInterceptors() {
        try {
            final Method getInterceptors = WebMvcConfigurationSupport.class.getDeclaredMethod(
                    "getInterceptors", FormattingConversionService.class, ResourceUrlProvider.class
            );

            if ( !getInterceptors.trySetAccessible() ) {
                throw new IllegalStateException("Could not access WebMvcConfigurationSupport's interceptors.");
            }

            final Object[] rawInterceptors = (Object[]) getInterceptors.invoke(
                    webMvcConfigurationSupport,
                    formattingConversionService,
                    resourceUrlProvider
            );

            synchronized ( this.interceptors ) {
                for ( final Object rawInterceptor : rawInterceptors ) {
                    this.interceptors.add((HandlerInterceptor) rawInterceptor);
                }
            }

        } catch ( Throwable t ) {
            throw new RuntimeException("Failed to fetch interceptors fro WebMvcConfigurationSupport for Couch2r.", t);
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        final String couch2rBasePath = couch2rCore.getCouch2rConfiguration().getCouch2rBasePath();
        final String path = Couch2rRequestUtil.getRequestPathWithTrailingSlash(request);

        if ( path.startsWith(couch2rBasePath) ) {
            final String couch2rPath = path.substring(couch2rBasePath.length());
            final int indexOfFirstSlash = couch2rPath.indexOf("/");
            final String resourcePath = indexOfFirstSlash < 0 ?
                    "" :
                    couch2rPath.substring(0, indexOfFirstSlash+1); // +1 >> to maintain trailing slash

            if ( !resourcePath.isEmpty() ) {
                final Optional<Couch2rMappedResource> mappedResource = getMappedResource(resourcePath);

                if ( mappedResource.isPresent() ) {
                    return new HandlerExecutionChain(mappedResource.get(), this.interceptors);
                }
            }
        }

        return null;
    }

    /**
     * Tries to find mapped resource using cache or by on the fly finding a mapping.
     *
     * @param resourcePath Resource path. Must end with trailing slash in order to work properly.
     * @return Mapped resource or empty if none is present for the given path.
     */
    protected Optional<Couch2rMappedResource> getMappedResource(final String resourcePath) {
        final Couch2rMappedResource cacheEntry = mappedResourceCache.get(resourcePath);
        if (cacheEntry != null) return Optional.of(cacheEntry);

        final Optional<Couch2rMappedResource> match = couch2rCore.getMappingByCouch2rResourcePath(resourcePath);

        if (match.isPresent()) {
            mappedResourceCache.put(resourcePath, match.get());
            return match;
        }

        return Optional.empty();
    }

}
