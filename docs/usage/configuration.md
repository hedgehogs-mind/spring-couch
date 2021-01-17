# CouchRest Configuration

As the title suggests, this documentation tells you everything about configuration possibilities.

<!-- MDTOC maxdepth:6 firsth1:0 numbering:0 flatten:0 bullets:1 updateOnSave:1 -->

- [Bean](#bean)   
- [Adapter](#adapter)   
- [Configuration settings](#configuration-settings)   

<!-- /MDTOC -->

## Bean

For every CouchRest application there must be one Bean which implements the interface `CouchRestConfiguration`.
CouchRest uses that bean to set uo CouchRest correctly.

## Adapter

You may want to rather extend a template configuration rather than implementing the interface. Therefore exists
the class `CouchRestConfigurationAdapter`.

So you can simply define your bean as follows:

```
@Component
public class MyConfiguration
       extends CouchRestConfigurationAdapter {

}
```

The default settings of that adapter are shown in the table of the next section.

## Configuration settings

| Method | Explanation | Further reading | Adapter default |
|---|---|---|---|
| `getCouchRestBasePath()` | The base HTTP path under which all resources shall be mapped. Must end with a trailing slash. |  | `"/api/"` |
| `getCouchRestObjectMapper()` | The optional Jackson ObjectMapper to use for all JSON (de-)serialization. If `Optional.empty()` is returned, CouchRest either uses a global ObjectMapper bean or instantiates a new one. |  | `Optional.empty()` |
| `getBaseSecurityRule()` | The base security rule check before any endpoint execution. Must be a SpringEL expression. |  [Security](security.md) | `"denyAll()"` |
| `getDefaultEndpointSecurityRule()` | The default security rule to use for all endpoints if no own endpoint level security rule has been set. Must be a SpringEL expression. | [Security](security.md) | `"denyAll()"` |
| `getSpringElEvaluationRootObject()` | The root object for all SpringEL evaluations. This object will be autowired by CouchRest. If `Optional.empty()` is returned, CouchRest instantiates an instance of `CouchRestSpelRoot` as a default root object. | [Security](security.md) | `Optional.empty()` |
|  |  |  |  |
