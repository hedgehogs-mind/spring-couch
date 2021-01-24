# Logging [concept]

## What aspects shall be logged?

- CouchRest boot/setup
    - Welcome message
    - config steps
    - start of discovery
    - end of discovery
    - start of mapping creation > for each type
    - end of mapping creation > for each type
- CouchRestHandlerAdapter > Exceptions
    - unknown Exceptions shall be logged
    - ProblemDetails not > the have been handled by the MappingHandlers