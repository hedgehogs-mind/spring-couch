package com.hedgehogsmind.springcouchrest.beans;

import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetail;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.ProblemDetailConvertibleRuntimeException;
import com.hedgehogsmind.springcouchrest.rest.problemdetail.problems.CouchRestProblems;
import com.hedgehogsmind.springcouchrest.util.RequestUtil;
import com.hedgehogsmind.springcouchrest.util.ResponseUtil;
import com.hedgehogsmind.springcouchrest.workers.mapping.MappingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Supports handling {@link MappingHandler}s.
 */
public class CouchRestHandlerAdapter
        implements HandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(CouchRestHandlerAdapter.class);

    private final CouchRestCore couchRestCore;

    public CouchRestHandlerAdapter(CouchRestCore couchRestCore) {
        this.couchRestCore = couchRestCore;
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof MappingHandler;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        ResponseEntity responseEntityToSend = null;

        try {
            responseEntityToSend = ((MappingHandler) handler).handle(
                    request,
                    couchRestCore.getCouchRestObjectMapper()
            );
        } catch ( ProblemDetailConvertibleRuntimeException e ) {
            responseEntityToSend = e.toProblemDetail(
                    RequestUtil.fetchLocale(request, Locale.ENGLISH)
            ).toResponseEntity();

        } catch ( RuntimeException e ) {
            final ProblemDetail exceptionProblemDetail = CouchRestProblems.UNKNOWN_PROBLEM.toProblemDetail(
                    RequestUtil.fetchLocale(request, Locale.ENGLISH)
            );

            log.error("A CouchRest MappingHandler threw an unknown exception. " +
                    "Returning ProblemDetail with type 'unknown problem' to client. ProblemDetail instance: "
                    +exceptionProblemDetail.getInstance(), e);

            responseEntityToSend = exceptionProblemDetail.toResponseEntity();
        }

        ResponseUtil.writeResponseEntity(
                responseEntityToSend,
                request,
                response,
                couchRestCore.getCouchRestObjectMapper()
        );

        return null;
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return 0;
    }
}
