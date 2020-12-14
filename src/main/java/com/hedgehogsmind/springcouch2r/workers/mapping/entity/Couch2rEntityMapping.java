package com.hedgehogsmind.springcouch2r.workers.mapping.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedgehogsmind.springcouch2r.data.discovery.Couch2rDiscoveredUnit;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblems;
import com.hedgehogsmind.springcouch2r.util.Couch2rRequestUtil;
import com.hedgehogsmind.springcouch2r.workers.mapping.Couch2rMappedResourceBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;

import javax.persistence.metamodel.EntityType;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

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
@Slf4j
public class Couch2rEntityMapping extends Couch2rMappedResourceBase {

    private final CrudRepository repository;

    private final EntityType entityType;

    /**
     * Stores given values.
     *
     * @param fullPathWithTrailingSlash Full path including Couch2r base path.
     * @param couch2rResourcePathWithTrailingSlash Path after Couch2r base path.
     * @param discoveredUnit Source of this mapping.
     * @param repository Repository of the entity.
     * @param entityType Handled entity.
     */
    public Couch2rEntityMapping(
            String fullPathWithTrailingSlash,
            String couch2rResourcePathWithTrailingSlash,
            Couch2rDiscoveredUnit discoveredUnit,
            CrudRepository repository,
            EntityType entityType
    ) {
        super(fullPathWithTrailingSlash, couch2rResourcePathWithTrailingSlash, discoveredUnit);

        this.repository = repository;
        this.entityType = entityType;
    }

    @Override
    public ResponseEntity handle(HttpServletRequest request, ObjectMapper objectMapper, Locale locale, String[] pathVariables, Map<String, String[]> queryParameters) {
        return Couch2rProblems.UNKNOWN_PROBLEM.toProblemDetail(
                Couch2rRequestUtil.fetchLocale(request, Locale.ENGLISH)
        ).toResponseEntity();
    }

    public CrudRepository getRepository() {
        return repository;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    //    // TODO @peter docs
//    public ResponseEntity handle(final HttpServletRequest request, final ObjectMapper objectMapper) {
//        final String path = Couch2rPathUtil.normalizeWithTrailingSlash(
//                UrlPathHelper.defaultInstance.getPathWithinApplication(request)
//        );
//
//        if ( path.length() < fullPathWithTrailingSlash.length() ) {
//            throw new IllegalStateException("Shall handle path which is too short and " +
//                    "should not have been mapped to this mapper.");
//        }
//
//        final String[] additionalParts = getAdditionalParts(path);
//
//        switch ( request.getMethod() ) {
//            case "GET": return handleGet(additionalParts);
//            case "POST": return handlePost(request, additionalParts, objectMapper);
//            default: return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
//        }
//    }
//
//    /**
//     * If path is longer than pathWithTrailingSlash, then we fetch the tail and split it into parts by
//     * slash separator.
//     */
//    protected String[] getAdditionalParts(final String path) {
//        if ( path.length() > fullPathWithTrailingSlash.length() ) {
//            return path.substring(fullPathWithTrailingSlash.length()).split("\\/");
//        }
//
//        return new String[0];
//    }
//
//    /**
//     * <p>
//     *     This handles GET requests. Depending on the additional parts, this method does:
//     *     <ul>
//     *         <li>0 additional parameters: fetch all entity instances and returns them (200 OK).</li>
//     *         <li>1 additional parameter: try to parse as ID and try to fetch entity instance (200 if found, 404 if not)</li>
//     *         <li>2+ additional parameters: 400 BadRequest</li>
//     *     </ul>
//     * </p>
//     *
//     * @param additionalParts Additional path parts after base path.
//     * @return Result as ResponseEntity.
//     */
//    protected ResponseEntity handleGet(final String[] additionalParts) {
//        if ( additionalParts.length == 0 ) {
//
//            // Simple get all
//            return ResponseEntity.ok(
//                repository.findAll()
//            );
//
//        } else if ( additionalParts.length == 1 ) {
//
//            // Get one by id
//            final Object parsedId = parseId(additionalParts[0]);
//
//            final Optional<Object> entityInstance = repository.findById(parsedId);
//            if ( entityInstance.isEmpty() ) return ResponseEntity.notFound().build();
//
//            return ResponseEntity.ok(entityInstance.get());
//
//        } else {
//            return Couch2rProblems.TOO_MANY_PATH_VARIABLES.toResponseEntity();
//        }
//    }
//
//    // TODO @peter docs
//    public ResponseEntity handlePost(final HttpServletRequest request,
//                                     final String[] additionalParts,
//                                     final ObjectMapper objectMapper) {
//
//        if ( additionalParts.length == 0 ) {
//            if ( request.getContentLengthLong() == 0 ) {
//                return ResponseEntity.badRequest().body("No body specified (content length = 0)");
//            }
//
//            try {
//                final String body = request.getReader().lines().collect(Collectors.joining("\n"));
//
//                try {
//                    final Object serializedData = objectMapper.readValue(body, entityType.getJavaType());
//
//                    // TODO @peter validate serializedData
//
//                    try {
//                        final Object savedEntity = repository.save(serializedData);
//                        return ResponseEntity.ok(savedEntity);
//
//                    } catch ( RuntimeException e ) {
//                        log.warn("Save attempt failed.\nBody:"+body, e);
//                        return ResponseEntity.badRequest().body("Error during save attempt. See logs for more information.");
//                    }
//
//                } catch ( JsonProcessingException e ) {
//                    // TODO @peter can we provide a better error message
//                    // TODO @peter ProblemDescriptor
//                    return ResponseEntity.badRequest().body("Serialization error: "+e.getMessage());
//                }
//            } catch ( IOException e ) {
//                log.error("Error while reading POST request's body", e);
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Error while reading request body. See logs for more information.");
//            }
//        } else {
//            return ResponseEntity.badRequest().body("Couch2r POST supports only data post to resource base path." +
//                    " Further path variables are not supported.");
//        }
//    }
//
//    /**
//     * Delegator method. Currently supports:
//     * <ul>
//     *     <li>{@link Long}</li>
//     *     <li>{@link Integer}</li>
//     *     <li>{@link String}</li>
//     * </ul>
//     *
//     * @param value Id value as string.
//     * @return ID as parsed object.
//     */
//    protected Object parseId(final String value) {
//        final Class idClass = entityType.getIdType().getJavaType();
//
//        if ( idClass == Long.class || idClass == long.class )
//            return parseLongId(value);
//
//        else if ( idClass == Integer.class || idClass == int.class )
//            return parseIntegerId(value);
//
//        else if ( idClass == String.class )
//            return parseStringId(value);
//
//        else
//            throw new Couch2rIdTypeParsingNotSupportedException(idClass);
//    }
//
//    /**
//     * Tries to parse value as Long. If not possible, a {@link Couch2rIdValueNotParsableException}
//     * will be thrown.
//     * @param value Value to be parsed.
//     * @return Value as Long.
//     */
//    protected Long parseLongId(final String value) {
//        try {
//            return Long.parseLong(value);
//        } catch ( NumberFormatException e ) {
//            throw new Couch2rIdValueNotParsableException("Id not a number", e);
//        }
//    }
//
//    /**
//     * Tries to parse value as Integer. If not possible, a {@link Couch2rIdValueNotParsableException}
//     * will be thrown.
//     * @param value Value to be parsed.
//     * @return Value as Integer.
//     */
//    protected Integer parseIntegerId(final String value) {
//        try {
//            return Integer.parseInt(value);
//        } catch ( NumberFormatException e ) {
//            throw new Couch2rIdValueNotParsableException("ID not a number", e);
//        }
//    }
//
//    /**
//     * Just returns value.
//     * @param value Value to be parsed.
//     * @return Value.
//     */
//    protected String parseStringId(final String value) {
//        return value;
//    }
//
//    public Couch2rDiscoveredUnit getDiscoveredUnit() {
//        return discoveredUnit;
//    }
//
//    public String getFullPathWithTrailingSlash() {
//        return fullPathWithTrailingSlash;
//    }
//
//    public String getEntityPathWithTrailingSlash() {
//        return entityPathWithTrailingSlash;
//    }
//
//    public CrudRepository getRepository() {
//        return repository;
//    }
//
//    public EntityType getEntityType() {
//        return entityType;
//    }
//
//    @Override
//    public String getFullPath() {
//        return fullPathWithTrailingSlash;
//    }
//
//    @Override
//    public String getGroupResourcePath() {
//        return entityPathWithTrailingSlash;
//    }
//
//    @Override
//    public Couch2rDiscoveredUnit getMappingSource() {
//        return discoveredUnit;
//    }
//
//    @Override
//    public List<Couch2rMapping> getMappings() {
//        throw new UnsupportedOperationException();
//    }
}
