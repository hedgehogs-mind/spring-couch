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