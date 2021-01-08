package com.hedgehogsmind.springcouchrest.beans;

import com.hedgehogsmind.springcouchrest.workers.mapping.MappedResource;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappingHandler;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Responsible for finding a {@link MappingHandler} for the request.
 */
public class CouchRestHandlerMapping
        implements HandlerMapping,
                   Ordered {

    private final CouchRestCore couchRestCore;

    private final List<HandlerInterceptor> interceptors;

    private final WebMvcConfigurationSupport webMvcConfigurationSupport;

    private final FormattingConversionService formattingConversionService;

    private final ResourceUrlProvider resourceUrlProvider;

    public CouchRestHandlerMapping(
            CouchRestCore couchRestCore,
            WebMvcConfigurationSupport webMvcConfigurationSupport,
            FormattingConversionService formattingConversionService,
            ResourceUrlProvider resourceUrlProvider
    ) {
        this.couchRestCore = couchRestCore;
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
            throw new RuntimeException("Failed to fetch interceptors fro WebMvcConfigurationSupport for CouchRest.", t);
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) {
        for ( final MappedResource mappedResource : couchRestCore.getMappedResources() ) {

            final Optional<MappingHandler> resourceSubHandler = mappedResource.findHandler(request);

            if ( resourceSubHandler.isPresent() ) {
                return new HandlerExecutionChain(
                        resourceSubHandler.get(),
                        interceptors
                );
            }
        }

        return null;
    }

}
