# Roadmap

This document serves as a collection of possible features for the future.

## Carved in stone

Here we collect features that will definitely be implemented.

| Title | Description | Priority | Initiator | Concept |
|---|---|---|---|---|
| Logging | Setup, unhandled exceptions. Maybe even ProblemDetails? | [logging](concepts/logging.md) | Peter |  |
| Disable CRUD methods |  |  | Peter |  |
| In/Out mapping |  |  | Peter |  |
| Bean validations |  |  | Peter |  |
| DOC: interceptors |  |  | Peter |  |
| DOC: CouchRestHandlerMapping/Adapter |  |  | Peter |  |

## Proposals

Here we collect loose ideas, which may be implemented.

| Title | Description | Initiator | Concept |
|---|---|---|---|
| OpenAPI extension |  | Peter |  |
| Batch requests |  | Peter |  |
| Custom ID parser |  | Peter |  |
| Exception handler |  | Peter |  |
| Embed services (service mixin) |  | Peter |  |
| Publish repository methods (like service mixin?) |  | Peter |  |
| Service as published resource |  | Peter |  |
| Event listeners | For CRUD operations, service calls, exceptions, ... (before, after, afterRequest?) | Peter |  |
| Spring ApplicationEvents |  | Peter |  |

## Loose todos

- docs for error handling > reference to ProblemDetails

- build.gradle > versioned dependencies

- must the base path start with a slash?
- could the base path be "/" ?

- entity method > cache id field and getter and setter

- mapping test for both
    - entity with @CouchRest and
    - repo with @CouchRest
  