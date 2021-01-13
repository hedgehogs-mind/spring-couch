- security docs
  - @CouchRest > attribute `checkBaseSecurityRule`
  - security check flow
  - custom root object for spel evaluation
    - authentication trust resolver
    - permission evaluator
    - role prefix
    - custom extension
    - available expressions
  
- config docs
  - spring el root object
  - adapter
  - security rule default
  - security rule global

- build.gradle > versioned dependencies

- CouchRestHandlerMapping
  - Logging
  
- crud doc
  
- must the base path start with a slash?
- could the base path be "/" ?

- entity method > cache id field and getter and setter

- mapping test for both
  - entity with @CouchRest and
  - repo with @CouchRest
- logging
  - especially unhandled exceptions
  - logging of problem details > really ? isn't this overhead?
  
- json batch

# More

- custom id parsers
- bean validations

# Docs

- interceptors

Docs!

# Register Exception Handler

Register one or more exception handlers.

Maybe global exception handler too?

# Embed Services and Repo methods

By annotating CouchRest Element, you can also publish public methods
via CouchRest.

```
@Service
class ExampleService {

    public Entity findBySuperSecret(...) { ... }

    public Iterable<Entity> getAllBy(...) { ... }

}

@CouchRest(
    ...
    publishRepositoryMethods = true,
    publishRepositoryMethodsExplicitOnes = true
)
@CouchRestPublish(
    beanClass = ExampleService.class,
    onlyExplicitOnes = false // if true, use @CouchRestPublished
)
interface EntityRepo extends CrudRepository {
}
```

Somewhat like that.

Maybe also support for explicit method:

@Post, @Get, ...

Problems:

- multiple Methods with same name

# More

- Spring ApplicationEvents
- Own Eventlistener classes > with state before/after
  - before
  - afterInSameTransaction
  - afterTransaction
- Mapper
  - Specify intermediate DTO classes
- Disabling single methods (separate > not via @CrudSecurity and setting rule to "permitAll()")
  - post
  - get
  - ...

- hibernate validator usage
  - output validation errors