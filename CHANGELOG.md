# Changelog

## 0.91

### Features
- **Inclusion:** Implemented a mechanism to control the inclusion of properties of 
  specific object types.
- **Inclusion:** Implemented a mechanism to register custom inclusion resolvers to 
  support inclusion rules far beyond the built-in ones.

### Improvements
- **Inclusion:** Performance improvements (via @Deipher)

### Bugfixes
- **Circular Reference Detection:** Fixed 'Detected inconsistency in 
  enter/leave sequence. Must always be LIFO.' bug that could occur 
  due to inconsistent cleanup of the instance memory when a circular
  reference has been detected.
