# Roadmap

This document serves as a collection of possible features for the future.

<!-- MDTOC maxdepth:6 firsth1:0 numbering:0 flatten:0 bullets:1 updateOnSave:1 -->

- [Carved in stone](#carved-in-stone)   
- [Proposals](#proposals)   
- [Loose todos](#loose-todos)   

<!-- /MDTOC -->

## Carved in stone

Here we collect features that will definitely be implemented.

| Title | Description | Priority | Initiator | Concept |
|---|---|---|---|---|
| Disable @CouchRest on repos > instead maybe repo mixin like service mixin? |  |  | Peter |  |
| Move entity class & type from DiscoveredUnit to DiscoveredEntity |  |  | Peter |  |
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
| Embed services/repos? (service/repo ... "BEAN" mixin?) |  | Peter |  |
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
