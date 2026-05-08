# SubastaApp — Backend

API REST desarrollada con **Spring Boot 3** para el sistema de subastas dinámicas ascendentes.

## Tecnologías

- Java 17
- Spring Boot 3.2.5
- Spring Security + JWT (jjwt 0.11.5)
- Spring Data JPA + Hibernate
- H2 (base de datos en memoria para desarrollo)
- Lombok
- Springdoc OpenAPI (Swagger UI)

## Requisitos

- Java 17+
- Maven (o usar el bundleado de IntelliJ)

## Instalación y ejecución

```bash
# Con Maven instalado
mvn spring-boot:run

# Con el script incluido (usa Maven de IntelliJ si no hay mvn en PATH)
./run.sh
```

La API queda disponible en: `http://localhost:8080/api/v1`

## Swagger UI

```
http://localhost:8080/api/v1/swagger-ui/index.html
```

## Consola H2 (base de datos)

```
http://localhost:8080/api/v1/h2-console
JDBC URL: jdbc:h2:mem:subastadb
Usuario: sa
Password: (vacío)
```

> Para usar MySQL en producción, descomentar el driver en `pom.xml` y actualizar `application.properties`.

## Estructura del proyecto

```
src/main/java/com/subastaapp/
  model/              # Entidades JPA
  repository/         # Interfaces Spring Data
  dto/
    request/          # Objetos de entrada (validados con @Valid)
    response/         # Objetos de salida
  businesslogic/      # Servicios con la lógica de negocio
  controller/         # Controladores REST
  config/             # JWT, Security, CORS
  exception/          # Manejo global de errores
```

## Endpoints

Ver el README del frontend o acceder al Swagger UI para la documentación interactiva completa.
