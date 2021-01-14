# CRUD operations

This documentation page explains the basic CRUD operations CouchRest makes available. CRUD stands
for **C**reate, **R**ead, **U**pdate and **D**elete.

## Assumptions

In all examples we assume, that you configured your __CouchRest BasePath__ to be `/api/`!

Furthermore, there is the following entity, which has been published via `@CouchRest`:

```
@Entity
@CouchRest
public class Tree {

    @Id
    @GeneratedValue
    public long id;
    
    @Column
    public int height;
    
    @Column
    public int leafCount;
    
}
```

## Resource path

By default, an entity's path will be the concatenation of the base path and the resource path. The latter
is set to the snake case entity class name. You can overwrite that by specifying the argument `resourceName`
of the `@CouchRest` annotation.

## Operations

Based on the knowledge we gained about the resource path, our `Tree` entity would be accessible under
the following path: `/api/tree/`. We will use this path for examples in the single method explanations.

### Create

You can create a new entity by performing an HTTP POST request against the entity's path (here `/api/tree/`).
You need to pass a JSON body.

An example body:

```
{
    "height": 42,
    "leafCount": 145 
}
```

The response will be the persisted entity in the JSON format:

```
{
    "id": 1,
    "height": 42,
    "leafCount": 145 
}
```

As we see, the id got assigned. This is useful for further operations.

### Read

You can fetch either all or just one instance of the mapped entity.

To fetch all, you just perform an HTTP GET request against the entity's path (here `/api/tree/`). The result
will be a JSON array. Here is an example:

```
[
    {
        "id": 1,
        "height": 42,
        "leafCount": 145
    },
    {
        "id": 2,
        "height": 76,
        "leafCount": 65
    },
    {
        "id": 5,
        "height": 502,
        "leafCount": 2048
    }
]
```

In case you want to query a specific entity, you add the id of the wanted instances as a path variable after
the entity's path. Here is an example for the entity with id "2":

```
GET /api/tree/2:

{
    "id": 2,
    "height": 76,
    "leafCount": 65
}
```

### Update

To update an existing entity instance, we need to perform an HTTP POST request against the entity's path and append
the id of the entity instance to that path.

Here is an example for the entity with id 5:

```
POST /api/tree/5

Body:
{
    "height": 23,
    "leafCount": 35
}

Result:
{
    "id": 5,
    "height": 23,
    "leafCount": 35
}
```

You may only want to update a subset of fields. You can do this. You only need to add those fields to the request
body, that you want to update. Here is an example for the entity with id 5:

```
POST /api/tree/5

Body:
{
    "height": 65535,
}

Result:
{
    "id": 5,
    "height": 65535,
    "leafCount": 35
}
```

### Delete

To complete the lifecycle of an entity, you also have the possibility to delete an entity instance.
To do that, you need to perform an HTTP DELETE request against the entity's path and append the instance id to
that path.

Here is an example for deleting the entity with id "1": `DELETE /api/tree/1`. There will be no result data.

## Security

You can secure all CRUD operations individually using the annotation `@CrudSecurity`. Checkout the
[security documentation](security.md) and especially the section "CRUD Security".

