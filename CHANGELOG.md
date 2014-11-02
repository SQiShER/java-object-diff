# Changelog

## 0.91

### Features
- **Inclusion:** Implemented a mechanism to control the inclusion of properties of 
  specific object types.
- **Inclusion:** Implemented a mechanism to register custom inclusion resolvers to 
  support inclusion rules far beyond the built-in ones.
- **Introspection:** Implemented a mechanism to register a custom instance factory, 
  which will be used by `DiffNode#canonicalSet` in order to create instances of missing 
  objects 

### Improvements
- **Inclusion:** Performance improvements (via @Deipher)
- **DiffNode:** `canonicalSet` now automatically creates missing objects along the 
  path to the root object 

### Bugfixes
- **Circular Reference Detection:** Fixed 'Detected inconsistency in 
  enter/leave sequence. Must always be LIFO.' bug that could occur 
  due to inconsistent cleanup of the instance memory when a circular
  reference has been detected.