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
# Base de Datos PostgreSQL (Supabase)
spring.datasource.url=jdbc:postgresql://db.wponlgsvcdpobxdhguxh.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=gCBicI6Xxwzw8yFs
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false

# JWT Configuration
jwt.secret=${JWT_SECRET:tu-secreto-super-seguro-minimo-32-caracteres-aqui}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Server
server.port=8080
server.servlet.context-path=/api

# Logging
logging.level.root=INFO
logging.level.com.academicpath=DEBUG
```

### Perfil de Testing

Para tests, el proyecto usa **H2 en memoria**. Ver `src/test/resources/application-test.properties`.

## 📚 Arquitectura

La aplicación sigue una arquitectura por capas:

1. **DAO (Data Access Object)** - Acceso a datos y queries
2. **Manager** - Lógica de negocio
3. **Service** - Servicios de aplicación
4. **Controller** - Endpoints REST (por desarrollar)

## 🔗 Endpoints Disponibles

### 🔐 Autenticación
```
POST   /api/auth/register    - Registrar nuevo usuario
POST   /api/auth/login       - Iniciar sesión
```

### 👥 Usuarios
```
GET    /api/usuarios         - Obtener todos los usuarios
POST   /api/usuarios         - Crear nuevo usuario
GET    /api/usuarios/{id}    - Obtener usuario por ID
PUT    /api/usuarios/{id}    - Actualizar usuario
DELETE /api/usuarios/{id}    - Eliminar usuario
```

### 📚 Materias
```
GET    /api/materias         - Listar todas las materias
POST   /api/materias         - Crear nueva materia
GET    /api/materias/{id}    - Obtener materia por ID
PUT    /api/materias/{id}    - Actualizar materia
DELETE /api/materias/{id}    - Eliminar materia
```

### 📋 Prerequisitos
```
GET    /api/prerequisitos           - Listar todos los prerequisitos
POST   /api/prerequisitos           - Crear nuevo prerequisito
GET    /api/prerequisitos/{id}      - Obtener prerequisito por ID
DELETE /api/prerequisitos/{id}      - Eliminar prerequisito
```

### 📊 Progreso Académico
```
GET    /api/progreso              - Ver progreso académico
POST   /api/progreso              - Crear progreso
PUT    /api/progreso/{id}         - Actualizar progreso
GET    /api/progreso/{usuarioId}  - Progreso de usuario específico
```

## 📖 Documentación

La documentación interactiva está disponible en:
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

## 🛠️ Tecnologías

- **Spring Boot 3.5.13** - Framework principal
- **Spring Security** - Autenticación y autorización con JWT
- **Spring Data JPA** - Acceso a datos con Hibernate
- **PostgreSQL 42.7.10** - Base de datos relacional
- **Swagger/OpenAPI 2.8.6** - Documentación API
- **MapStruct 1.5.5** - Mapeo de DTOs
- **Lombok** - Reducción de boilerplate
- **JUnit 5** - Testing
- **Mockito** - Mock objects
- **Maven 3.6+** - Gestor de dependencias

## ✅ Tests

Ejecutar tests:
```bash
./mvnw test                # Linux/Mac
mvnw.cmd test              # Windows
```

Tests incluidos:
- ✅ `BackendApplicationTests` - Carga del contexto
- ✅ `AuthControllerTest` - Tests de autenticación
- ✅ `AuthServiceTest` - Servicio de autenticación
- ✅ `MateriaServiceTest` - Servicio de materias
- ✅ `PrerrequisitoServiceTest` - Servicio de prerequisitos

## 📦 Compilación

```bash
# Compilar
./mvnw clean compile -DskipTests   # Linux/Mac
mvnw.cmd clean compile -DskipTests # Windows

# Compilar e instalar
./mvnw clean install   # Linux/Mac
mvnw.cmd clean install # Windows

# Build JAR
./mvnw clean package   # Linux/Mac
mvnw.cmd clean package # Windows
```
- MySQL 8.0+
- Java 21
- Maven 3.6+

## 📝 Notas de Desarrollo

- La base de datos se crea automáticamente según `schema.sql`
- El `pom.xml` gestiona todas las dependencias
- Los archivos Maven Wrapper permiten ejecutar maven sin instalación local

