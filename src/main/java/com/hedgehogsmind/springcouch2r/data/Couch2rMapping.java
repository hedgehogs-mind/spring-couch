package com.hedgehogsmind.springcouch2r.data;

import com.hedgehogsmind.springcouch2r.util.Couch2rPathUtil;
import lombok.Getter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *     Path is stored with trailing slash so that it is prefix free.
 *     <br>
 *     Problem example if we would not use trailing slashes:
 *     <ul>
 *         <li>Mapping 1: <code>/api/code</code></li>
 *         <li>Mapping 2: <code>/api/codeRedeemed</code></li>
 *         <li>Incoming request: <code>/api/code</code> (here startsWith check would not lead to unique result!</li>
 *     </ul>
 * </p>
 */
@Getter
public class Couch2rMapping {

    private final String pathWithTrailingSlash;

    private final CrudRepository repository;

    // TODO @peter docs
    public Couch2rMapping(String path, CrudRepository repository) {
        this.pathWithTrailingSlash = Couch2rPathUtil.normalizeWithTrailingSlash(path);
        this.repository = repository;
    }

    // TODO @peter docs
    public ResponseEntity handle(final HttpServletRequest request) {
        final String path = Couch2rPathUtil.normalizeWithTrailingSlash(
                UrlPathHelper.defaultInstance.getPathWithinApplication(request)
        );

        if ( path.length() < pathWithTrailingSlash.length() ) {
            throw new IllegalStateException("Shall handle path which is too short and " +
                    "should not have been mapped to this mapper.");
        }

        final String[] additionalParts = getAdditionalParts(path);

        switch ( request.getMethod() ) {
            case "GET": return handleGet(additionalParts);
            default: return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
    }

    /**
     * If path is longer than pathWithTrailingSlash, then we fetch the tail and split it into parts by
     * slash separator.
     */
    protected String[] getAdditionalParts(final String path) {
        if ( path.length() > pathWithTrailingSlash.length() ) {
            return path.substring(pathWithTrailingSlash.length()).split("\\/");
        }

        return new String[0];
    }

    // TODO @peter docs
    protected ResponseEntity handleGet(final String[] additionalParts) {
        if ( additionalParts.length == 0 ) {
            // Simple get all
            return ResponseEntity.ok().body(
                repository.findAll()
            );
        } else {
            throw new UnsupportedOperationException();
        }
    }


}
