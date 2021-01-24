# Logging

## CouchRestCore

The core of CouchRest logs information about the setup process. This includes e.g. which object mapper
has been applied, whether the security rules are valid or how many CouchRest resources have been found.

## CouchRestHandlerAdapter

The HandlerAdapter only logs unknown exception which occurred while executing a CouchRest
MappingHandler. The ProblemDetail instance will be logged as well as the exception.

You can use the `instance` value of the returned ProblemDetail JSON to search for the exception
in the application logs.