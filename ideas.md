- logging

- IdTypeParsingNotSupportedException
- ProblemDetailRuntimeException
  - interface with ProblemDescriptor
  - or ProblemDetail
  - maybe just toDetail?
  - multiple interfaces?
    - ProblemDetailRuntimeException
    - ...
   

# Docs

Docs!

# Import annotation
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SchedulingConfiguration.class)
@Documented
public @interface EnableScheduling {

}
```

# Mapping type and more metadata

In Couch2rMapping enum mapping type:

- `FROM_REPOSITORY`
  - already done
- `FROM_ENTITY_TO_EXISTING_REPOSITORY`
  - __todo__
- `FROM_ENTITY_TO_NEW_REPOSITORY`
  - already done

Plus more fields:

- optional repository interface (for embedded methods)

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