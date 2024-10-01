# spring-boot-crud-module

A reusable Spring Boot module that provides generic CRUD (Create, Read, Update, Delete) controller and service implementations. This module simplifies the process of building CRUD functionality in Spring Boot applications by allowing easy integration of standard logic.

## Features

- Generic `CrudController` to handle basic CRUD operations
- Generic `CrudService` for database interaction and validation
- Easy integration with JPA repositories and entity mappings
- Filterable and pageable queries

## Usage

### 1. Add the Submodule to Your Project

To include `spring-boot-crud-module` in your project, add it as a Git submodule:

```bash
git submodule add https://github.com/nedim-hairlahovic/spring-boot-crud-module
```

### 2. Add Dependency in pom.xml
After adding the submodule, ensure you include it as a dependency in your pom.xml file (if using Maven):
```xml
<dependency>
    <groupId>dev.nhairlahovic</groupId>
    <artifactId>spring-boot-crud-module</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 3. Extend CrudController and CrudService
In your project, extend the `CrudController` and `CrudService` to create specific controllers and services for your entities.

## Demo

An example implementation of the `spring-boot-crud-module` can be found in the following [demo repository](https://github.com/nedim-hairlahovic/spring-boot-crud-demo).
