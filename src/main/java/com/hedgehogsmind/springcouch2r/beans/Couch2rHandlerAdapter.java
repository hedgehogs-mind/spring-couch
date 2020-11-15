package com.hedgehogsmind.springcouch2r.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.data.Couch2rMapping;
import com.hedgehogsmind.springcouch2r.util.Couch2rResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class Couch2rHandlerAdapter implements HandlerAdapter {

    // TODO @peter does this always work?
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(Object handler) {
        return handler instanceof Couch2rMapping;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final ResponseEntity responseEntity = ((Couch2rMapping)handler).handle(request);
        Couch2rResponseUtil.writeResponseEntity(responseEntity, response, objectMapper);

        return null;
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return 0;
    }
}
