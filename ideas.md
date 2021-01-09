- extend config and core
  - default endpoint rule > acts as a placeholder for indidivudal rules
  - global rule > must be true before an individual rule 
  - EntitySecurityHandler > extend


- entity security handler
- pass core to mapped resources

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

- config root object > as bean > post process > enable dependency injection etc.

- build.gradle > versioned dependencies

- CouchRestHandlerMapping
  - Logging
  - WebMvcConfigurationSupport
    - make optional > fetch interceptors only if present 
    - __otherwise WARNING__ 
  
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
- Security
  - Config
    - ALL_ALLOWED
    - ALL_PROHIBITED
      - Here a default SpringEL expr must be supplied
  - overridden in entities/repos
- Disabling single methods
  - post
  - get
  - ...

- hibernate validator usage
  - output validation errors