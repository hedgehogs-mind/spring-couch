# Toggling CRUD methods

## Idea

There will be a new annotation `@CrudMethods`. It will have three attributes:

- `boolean get() default true`
- `boolean saveUpdate() default true`
- `boolean delete() default true`

These flags determine if the corresponding method(s) shall be enabled or not.

By default, they all are true. As the annotation `@CouchRest` enables all CRUD methods on an entity
or repository, adding the annotation `@CrudMethods` without overwriting the attributes does not change
anything!

__The annotation is meant to explicitly disable certain CRUD methods.__

## Where allowed/impact?

The annotation is allowed everywhere. It shall have only an impact on
entities or repositories annotated with `@CouchRest`.

## Impact

For the disabled methods will no request handler be registered. This means, that Spring will handle this as a 404.