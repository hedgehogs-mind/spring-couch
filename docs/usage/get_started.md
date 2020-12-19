# Get Started with Couch2r

This page is a quick start guide. At the end, you will have published your first entity via REST.

# 1. Add dependency

__TODO @peter__: add docs

# 2. Enable Couch2r

In this section, we will show you, how you can enable Couch2r in your Spring (Boot) Application. After this step,
Couch2r runs but will still throw an error, because we need to add a configuration. This is done in the next step!

## Via an annotation

Take your Spring Boot Application class and open it. Add the annotation `@EnableCouch2r` to the class. 

```
...
import com.hedgehogsmind.springcouch2r.beans.EnableCouch2r;
...

@SpringBootApplication
@EnableCouch2r
public class MySpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySpringBootApplication.class, args);
	}

}
```

## Manually

You can also configure the required beans manually. You need to configure the following beans:

- `Couch2rCore`
- `Couch2rHandlerMapping`
- `Couch2rHandlerAdapter`

`@EnableCouch2r` does nothing else than importing these classes into the Spring Application (scan).

# 3. Configuration

After enabling Couch2r, you probably instantly started your application. In case I'm right, 
you experienced the following error:

```
Caused by: com.hedgehogsmind.springcouch2r.beans.exceptions.Couch2rNoConfigurationFoundException: No Couch2rConfiguration found.
	at com.hedgehogsmind.springcouch2r.beans.Couch2rCore.fetchCouch2rConfiguration(Couch2rCore.java:87) ~[main/:na]
	at com.hedgehogsmind.springcouch2r.beans.Couch2rCore.setup(Couch2rCore.java:64) ~[main/:na]
	...
```

__You need to provide a `Couch2rConfiguration` Bean!__

Here is an example:

```
@Component
public class MyCouch2rConfiguration
       implements Couch2rConfiguration {

    @Override
    public String getCouch2rBasePath() {
        return "/api/couch2r/";
    }

    @Override
    public Optional<ObjectMapper> getCouch2rObjectMapper() {
        return Optional.empty();
    }
    
    ...
    
}
```

That's it! For more information on what each setting does,
examine the JavaDocs of `Couch2rConfiguration` (TODO @peter: reference JavaDocs).

# 4. Level up your first entity!

## Entity level

Just add the annotation `@Couch2r` to an entity like this:

```
@Entity
@Couch2r
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

__Assuming that you configured the Couch2r BasePath to be `/api/`, you can now perform `GET /api/note` and you will
get all note instances!__

## Repository level

You can also publish a resource via a repository by just adding the annotation `@Couch2r` again:

```
@Repository
@Couch2r
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

Go ahead and read the following documentation pages, to better understand, what Couch2r is capable of:

- [CRUD operations of Entity](crud_operations.md)