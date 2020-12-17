- entity method > cache id field and getter and setter

- mapping test for both
  - entity with @Couch2r and
  - repo with @Couch2r
- logging
  - especially unhandled exceptions
  - logging of problem details > really ? isn't this overhead?
- json batch
 
# Entity Mappings

- delete
- update

# More

- custom id parsers
- bean validations

# Docs

Docs!

# Register Exception Handler

Register one or more exception handlers.

Maybe global exception handler too?

# Embed Services and Repo methods

By annotating Couch2r Element, you can also publish public methods
via Couch2r.

```
@Service
class ExampleService {

    public Entity findBySuperSecret(...) { ... }

    public Iterable<Entity> getAllBy(...) { ... }

}

@Couch2r(
    ...
    publishRepositoryMethods = true,
    publishRepositoryMethodsExplicitOnes = true
)
@Couch2rPublish(
    beanClass = ExampleService.class,
    onlyExplicitOnes = false // if true, use @Couch2rPublished
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