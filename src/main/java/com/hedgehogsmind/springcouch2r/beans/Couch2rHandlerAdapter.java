package com.hedgehogsmind.springcouch2r.beans;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDetailConvertibleRuntimeException;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.util.Couch2rRequestUtil;
import com.hedgehogsmind.springcouch2r.util.Couch2rResponseUtil;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class Couch2rHandlerAdapter implements HandlerAdapter {

    private final Couch2rCore couch2rCore;

    public Couch2rHandlerAdapter(Couch2rCore couch2rCore) {
        this.couch2rCore = couch2rCore;
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof Couch2rMapping;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ResponseEntity responseEntityToSend = null;

        try {
            responseEntityToSend = ((Couch2rMapping) handler).handle(
                    request,
                    couch2rCore.getCouch2rObjectMapper()
            );
        } catch ( ProblemDetailConvertibleRuntimeException e ) {
            responseEntityToSend = e.toProblemDetail(
                    Couch2rRequestUtil.fetchLocale(request, Locale.ENGLISH)
            ).toResponseEntity();

        } catch ( RuntimeException e ) {
            responseEntityToSend = Couch2rProblems.UNKNOWN_PROBLEM.toProblemDetail(
                    Couch2rRequestUtil.fetchLocale(request, Locale.ENGLISH)
            ).toResponseEntity();
        }

        // TODO @peter log ProblemDetails and ProblemDetailConvertibles

        Couch2rResponseUtil.writeResponseEntity(
                responseEntityToSend,
                request,
                response,
                couch2rCore.getCouch2rObjectMapper()
        );

        return null;
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return 0;
    }
}
