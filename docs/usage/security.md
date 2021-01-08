# Security with CouchRest

CouchRest emphasizes security a lot. This is done by using Spring Expression Language. Most often you will want
to query a Spring Security state.

CouchRest needs a base security definition.

## Request rejection

Generally: In case a request will be rejected by any security rule we explain in this document, 
a 403 response will be sent to the client.

## Rule interpretation

All security rules must be written as SpringEL expression. They shall produce a boolean result.
The result will be interpreted as follows:

- `true`: Access check succeeded, and the request will be handled.
- `false`: Access check failed, and the request will be rejected.

## Base Security

You can specify a global security rule for all CouchRest endpoints, in case no more detailed
rules exist for the endpoints.

This is done by Specifying a base security rule in SpringEL in your `CouchRestConfiguration`:

```
@Component
public MyCouchRestConfig implements CouchRestConfiguration {

    ...
    
    @Override
    public String getBaseSecurityRule() {
        return "isAuthenticated() && hasAuthority('API_USER')";
    }
  
    ...
  
}
```

__I encourage you__ to choose a rather restrictive base rule. This eliminates unexpected access of endpoints you did
forget to secure with an own rule!

I further encourage you to go by _"restrictive global rule and explicitly open"_. 


## Crud Security

The annotation `@CouchRest` on a Repository or an Entity class publishes the (corresponding) Entity via Rest.
It creates endpoints for creating entities of that type, fetching and deleting them. You can specify security rules
for each of that methods individually.

__In case you do not a rule for a method, the global security rule is used!__

To decalre individual security rules, you add the annotation `@CrudSecurity` to your
Entity or Repository you already annotated with `@CouchRest` and specify the rules via the annotation
arguments. Here is an example:

```
@CouchRest
@CrudSecurity(
    read = "isAuthenticated() && hasAuthority('READ_NOTE')",
    saveUpdate = "isAuthenticated() && hasAuthority('SAVE_NOTE')",
    delete = "isAuthenticated() && hasAuthority('DELETE_NOTE')"
)
public class Note {

    @Id
    @GeneratedValue
    public long id;
    
    @Column
    public String content;

}
```

The rules explained in more detail:
- `read`: This rule secures "GET all" and "GET one by id" requests.
- `saveUpdate`: This rule secures "POST new" and "POST update by id" requests.
- `delete`: This rule secures "DELETE by id" requests.