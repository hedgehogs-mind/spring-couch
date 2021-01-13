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

## Security flow

The following image shows CouchRest's two stage security model. If you prefer a textual description or need one, 
skip the image, there is a transcription of what the image shows.

![Two staged security flow](../imgs/security_flow.png)

Security flow explained in words:

When a method/endpoint handler has been found for a request, that handler will be executed by Spring MVC at some point.
When the handler starts executing, CouchRest checks the access privilege of the client.

First CouchRest checks the `@CouchRest` annotation of the resource the request targets.
In case the argument `checkBaseSecurityRule` of the `@CouchRest` annotation is true,
CouchRest will evaluate the base security rule specified in the `CouchRestConfiguration`.
If the result is true, CouchRest proceeds. If it is false, CouchRest will return an 
"access forbidden" error to the client.

In case the argument `checkBaseSecurityRule` is false, CouchRest will just skip that step.

After handling the base security rule stuff (security stage one), CouchRest checks, if the endpoint/target method
has an own security rule defined by the developer/you. In case there is one, it will be evaluated. If not,
the default endpoint security rule specified in the `CouchRestConfiguration` will be evaluated.

In either the case: If the result of the evaluated rule is true, CouchRest executes the rest of the endpoint handler
and returns the result to the client. If the result is false, an "access forbidden" error will be sent to the
client. That was the last stage – security stage two.

## SpringEL expressions for rules

Here we will explain the most important expressions which are available in rules. We will also discuss, how
you can extend the dictionary of expressions!

### Where are expressions defined

While evaluating a security rule written in SpringEL, the SpringEL evaluation root object is used. So
if you try to call the method `isSomeoneThere()` in your expression, the evaluator tries to execute a method
named `isSomeoneThere` of the root object.

By default, this root object is of type `CouchRestSpelRoot`. It implements Spring Security's interface
`SecurityExpressionOperations` and thus provides the most common expressions you probably already know
from Spring Security.

This root object can be replaced/extended in the `CouchRestConfiguration`. More on that later in "Extend expressions".

### Default expression


TODO @peter


### Extend expressions

You may want to add own expressions. Therefore, you need to extend the SpringEL evaluators root object. Please ensure
your read the section "Where are expressions defined".

Take your `CouchRestConfiguration` and override the method `getSpringElEvaluationRootObject()`. There you can either
return an extended class or instance an instance of `CouchRestSpelRoot` with additional methods via anonymous inner
declaration:

Example using anonymous inner declaration:

```
@Component
public MyCouchRestConfig implements CouchRestConfiguration {

    ...
    
    @Override
    public Optional<Object> getSpringElEvaluationRootObject() {
        return new CouchRestSpelRoot() {
        
            public void isSuperUser() {
                // Here you can literally do everything you want 
                return getAuthentication() instanceOf MySuperUserClass;
            }
        
        };
    }
  
    ...

}
```

### Further customization

TODO @peter:

- custom root object for spel evaluation
    - authentication trust resolver
    - permission evaluator
    - role prefix

## Rule definitions

### Base Security

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

### Default endpoint security rule

Within CouchRest you can always define endpoint/method level security rules. They represent the second security
stage. In case you have not specified an own rule for an endpoint, a default one will be checked.

This default endpoint rule must be defined in the CouchRestConfiguration:

```
@Component
public MyCouchRestConfig implements CouchRestConfiguration {

    ...
    
    @Override
    public String getDefaultEndpointSecurityRule() {
        return "denyAll()";
    }
  
    ...
  
}
```

__As always: per as restrictive as possible!__

### Crud Security

The annotation `@CouchRest` on a Repository or an Entity class publishes the (corresponding) Entity via Rest.
It creates endpoints for creating entities of that type, fetching and deleting them. You can specify security rules
for each of that methods individually.

__In case you did not specify an individual rule for a method, the default endpoint security rule will be used!__

To declare individual security rules for the CRUD functionalities, you add the annotation `@CrudSecurity` to your
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