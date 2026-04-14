# Backend - AcademicPath

Este directorio contiene la aplicación backend de AcademicPath desarrollada con Spring Boot.

## 📦 Estructura

```
Backend/
├── src/
│   ├── main/
│   │   ├── java/com/academicpath/backend/
│   │   │   ├── BackendApplication.java          # Clase principal
│   │   │   ├── config/
│   │   │   │   ├── OpenAPIConfig.java           # Configuración OpenAPI/Swagger
│   │   │   │   └── SecurityConfig.java          # Configuración de Seguridad
│   │   │   ├── dao/
│   │   │   │   └── usuarios/
│   │   │   │       ├── usuariosDao.java         # Interfaz DAO
│   │   │   │       └── impl/
│   │   │   │           └── usuariosDaoImpl.java  # Implementación DAO
│   │   │   ├── manager/
│   │   │   │   └── usuarios/
│   │   │   │       ├── UsuariosManager.java     # Interfaz Manager
│   │   │   │       └── impl/
│   │   │   │           └── UsuariosManagerImpl.java
│   │   │   ├── models/
│   │   │   │   └── entity/
│   │   │   │       └── Usuarios.java            # Entidad JPA
│   │   │   └── services/
│   │   │       └── usuarios/
│   │   │           └── UsuariosServiceImpl.java  # Servicio
│   │   └── resources/
│   │       ├── application.properties           # Configuración
│   │       └── schema.sql                       # Script SQL
│   └── test/
│       └── java/.../BackendApplicationTests.java
├── pom.xml                                       # Dependencias Maven
├── mvnw y mvnw.cmd                               # Maven Wrapper
└── .mvn/                                         # Configuración Maven

```

## 🚀 Inicio Rápido

### Compilar
```bash
./mvnw clean install       # Linux/Mac
mvnw.cmd clean install     # Windows
```

### Ejecutar
```bash
./mvnw spring-boot:run     # Linux/Mac
mvnw.cmd spring-boot:run   # Windows
```

### Tests
```bash
./mvnw test                # Linux/Mac
mvnw.cmd test              # Windows
```

## 📋 Configuración

Edita `src/main/resources/application.properties`:

```properties
# Puerto del servidor
server.port=8080

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/academic_path
spring.datasource.username=root
spring.datasource.password=contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 📚 Arquitectura

La aplicación sigue una arquitectura por capas:

1. **DAO (Data Access Object)** - Acceso a datos y queries
2. **Manager** - Lógica de negocio
3. **Service** - Servicios de aplicación
4. **Controller** - Endpoints REST (por desarrollar)

## 🔗 Endpoints Disponibles

Ver documentación en el README.md raíz.

## 🛠️ Tecnologías

- Spring Boot 3.5.13
- Spring Security
- Spring Data JPA
- MySQL 8.0+
- Java 21
- Maven 3.6+

## 📝 Notas de Desarrollo

- La base de datos se crea automáticamente según `schema.sql`
- El `pom.xml` gestiona todas las dependencias
- Los archivos Maven Wrapper permiten ejecutar maven sin instalación local

