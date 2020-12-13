# Get Started with Couch2r

## Add dependency

__TODO @peter__: add docs

## Enable Couch2r

In this section, we will show you, how you can enable Couch2r in your Spring (Boot) Application. After this step,
Couch2r runs but will still throw an error, because we need to add a configuration. This is done in the next step!

### Using `@EnableCouch2r`

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

### Manually

You can also configure the required beans manually. You need to configure the following beans:

- `Couch2rCore`
- `Couch2rHandlerMapping`
- `Couch2rHandlerAdapter`

`@EnableCouch2r` does nothing else than importing these classes into the Spring Application (scan).

## Configuration

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

## Level up your first repository!

TODO @peter add docs.

## Level up your first entity!

TODO @peter add docs.