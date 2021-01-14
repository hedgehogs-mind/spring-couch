# Get Started with CouchRest

This page is a quick start guide. At the end, you will have published your first entity via REST.

# 1. Add dependency

__TODO @peter__: add docs

# 2. Enable CouchRest

In this section, we will show you, how you can enable CouchRest in your Spring (Boot) Application. After this step,
CouchRest runs but will still throw an error, because we need to add a configuration. This is done in the next step!

## Via an annotation

Take your Spring Boot Application class and open it. Add the annotation `@EnableCouchRest` to the class. 

```
...
import com.hedgehogsmind.springcouchrest.annotations.EnableCouchRest;
...

@SpringBootApplication
@EnableCouchRest
public class MySpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySpringBootApplication.class, args);
	}

}
```

## Manually

You can also configure the required beans manually. You need to configure the following beans:

- `CouchRestCore`
- `CouchRestHandlerMapping`
- `CouchRestHandlerAdapter`

`@EnableCouchRest` does nothing else than importing these classes into the Spring Application (scan).

# 3. Configuration

After enabling CouchRest, you probably instantly started your application. In case I'm right, 
you experienced the following error:

```
Caused by: com.hedgehogsmind.springcouchrest.beans.exceptions.NoConfigurationFoundException: No CouchRestConfiguration found.
	at com.hedgehogsmind.springcouchrest.beans.CouchRestCore.fetchCouchRestConfiguration(CouchRestCore.java:87) ~[main/:na]
	at com.hedgehogsmind.springcouchrest.beans.CouchRestCore.setup(CouchRestCore.java:64) ~[main/:na]
	...
```

__You need to provide a `CouchRestConfiguration` Bean!__ For convenience, you can extend the `CouchRestConfigurationAdapter`.

*For our first demo, we will disable all security restrictions.* __This is not recommended for normal usage!__ Please
revert this later. Also read the [security documentation](security.md)!

Here is an example:

```
@Component
public class MyCouchRestConfiguration
       extends CouchRestConfigurationAdapter {
    
    @Override
    public String getBaseSecurityRule() {
        return "permitAll()";
    }

    @Override
    public String getDefaultEndpointSecurityRule() {
        return "permitAll()";
    }
    
}
```

For more on that checkout the [configuration docs](configuration.md) as well as the [security docs](security.md).

# 4. Level up your first entity!

## Entity level

Just add the annotation `@CouchRest` to an entity like this:

```
@Entity
@CouchRest
public class Note {

    @Id
    @GeneratedValue
    public int id;

    @Column
    public String title;

    @Column
    @Lob
    public String content;

}
```

__You can now perform `GET /api/note` and you will get all note instances!__

## Repository level

You can also publish a resource via a repository by just adding the annotation `@CouchRest` again:

```
@Repository
@CouchRest
public interface AddressRepository extends CrudRepository<Address, Long> {
   
}

@Entity
public class Address {

    @Id
    @GeneratedValue
    public long id;

    @Column
    public String street;

    @Column
    public String nr;

    @Column
    public String zip;

    @Column
    public String town;
    
}
```

# 5.+ Next steps

## Documentation

Go ahead and read the following documentation pages, to better understand, what CouchRest is capable of:

- [CRUD operations for entities](crud_operations.md)
- [Configuration of CouchRest](configuration.md)
- [Security](security.md)