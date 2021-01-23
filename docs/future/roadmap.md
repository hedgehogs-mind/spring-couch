# Roadmap

This document serves as a collection of possible features for the future.

## Carved in stone

Here we collect features that will definitely be implemented.

| Title | Description | Priority | Initiator | Concept |
|---|---|---|---|---|
| Java 11 support |  |  |  |  |
| Logging | Setup, unhandled exceptions. Maybe even ProblemDetails? |  |  |  |
| Disable CRUD methods |  |  |  |  |
| In/Out mapping |  |  |  |  |
| Bean validations |  |  |  |  |
| DOC: interceptors |  |  |  |  |
| DOC: CouchRestHandlerMapping/Adapter |  |  |  |  |

## Proposals

Here we collect loose ideas, which may be implemented.

| Title | Description | Initiator | Concept |
|---|---|---|---|
| OpenAPI extension |  |  |  |
| Batch requests |  |  |  |
| Custom ID parser |  |  |  |
| Exception handler |  |  |  |
| Embed services (service mixin) |  |  |  |
| Publish repository methods (like service mixin?) |  |  |  |
| Service as published resource |  |  |  |
| Event listeners | For CRUD operations, service calls, exceptions, ... (before, after, afterRequest?) |  |  |
| Spring ApplicationEvents |  |  |  |

## Loose todos

- docs for error handling > reference to ProblemDetails

- build.gradle > versioned dependencies

- must the base path start with a slash?
- could the base path be "/" ?

- entity method > cache id field and getter and setter

- mapping test for both
    - entity with @CouchRest and
    - repo with @CouchRest
  