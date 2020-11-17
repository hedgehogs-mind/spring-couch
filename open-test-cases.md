# Couch2Core - missing Couch2rConfiguration

We need to test that an exception will be thrown, if
no Couch2rConfiguration bean can be found.

# Couch2rCore - path clash

We need to test that path clashes will be detected for
- repos and
- entities.

# Couch2Core - entity also managed by repo

We need to test that an exception will be thrown, if an entity is tagged
with @Couch2r, but a repository with the same entity type is tagged with
@Couch2r too.

# Couch2Core - unsupported bean with @Couch2r

We need to test that an exception will be thrown, if a bean is found with
@Couch2r, but it is not supported by Couch2r.

# All Util classes

Todo...

# Couch2rMapping - We need to test the handle method

Trivial.
- Successful cases
- Unsupported HTTP methods
- Too many additional path variables for various HTTP methods

# Couch2rHandlerMapping

We need to test
- mapping matching
- cache construction

# Couch2rHandlerAdapter

We need to test
- correct ResponseEntity conversion
- correct Couch2rMapping call