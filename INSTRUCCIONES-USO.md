# INSTRUCCIONES DE USO - ACADEMIC PATH BACKEND

## 🚀 INICIO RÁPIDO

### 1. Base de Datos
```sql
CREATE DATABASE ruta_academica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Variables de Entorno
```bash
export DB_USERNAME=root
export DB_PASSWORD=tu_password
export JWT_SECRET=tu-secreto-super-seguro-minimo-32-caracteres-aqui
```

### 3. Compilar
```bash
cd Backend
./mvnw clean install -DskipTests
```

### 4. Ejecutar
```bash
./mvnw spring-boot:run
```

### 5. Acceder
- **API**: http://localhost:8080/api
- **Swagger**: http://localhost:8080/api/swagger-ui.html

---

## 📚 ENDPOINTS PRINCIPALES

### Autenticación
```bash
# Registrar
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombres": "Juan",
    "apellidos": "Pérez",
    "correo": "juan@test.com",
    "contrasena": "Password123!",
    "universidad": "UdeA",
    "carrera": "Ingeniería de Sistemas"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan@test.com",
    "contrasena": "Password123!"
  }'
```

### Con Token
```bash
curl -X GET http://localhost:8080/api/materias \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 📋 ARCHIVOS GENERADOS

### Entidades (8 tablas)
- ✅ Usuarios
- ✅ Materias
- ✅ Profesores
- ✅ UsuarioMaterias
- ✅ Actividades
- ✅ Calificaciones
- ✅ Prerrequisitos
- ✅ ProgresoAcademico
- ✅ SugerenciasMaterias

### Servicios (7 servicios)
- ✅ AuthService
- ✅ MateriaService
- ✅ ActividadService
- ✅ CalificacionService
- ✅ UsuarioMateriaService
- ✅ PrerrequisitoService
- ✅ ProgresoAcademicoService

### Controladores (8 endpoints)
- ✅ AuthController
- ✅ MateriaController
- ✅ ActividadController
- ✅ CalificacionController
- ✅ UsuarioMateriaController
- ✅ PrerrequisitoController
- ✅ ProgresoAcademicoController
- ✅ UsuarioController

### Seguridad JWT
- ✅ JwtUtil
- ✅ JwtAuthenticationFilter
- ✅ UsuarioUserDetails
- ✅ UsuariosUserDetailsService

### DTOs (12 clases)
- ✅ RegistroRequest
- ✅ LoginRequest
- ✅ MateriaRequest
- ✅ ActividadRequest
- ✅ CalificacionRequest
- ✅ UsuarioMateriaRequest
- ✅ UsuarioResponse
- ✅ MateriaResponse
- ✅ ActividadResponse
- ✅ CalificacionResponse
- ✅ UsuarioMateriaResponse
- ✅ ProgresoAcademicoResponse
- ✅ LoginResponse
- ✅ ApiResponse

### Mappers (4 mappers)
- ✅ UsuariosMapper
- ✅ MateriasMapper
- ✅ ActividadesMapper
- ✅ CalificacionesMapper

### Excepciones
- ✅ GlobalExceptionHandler
- ✅ ErrorResponse
- ✅ ResourceNotFoundException
- ✅ UsuarioException
- ✅ MateriaException
- ✅ ActividadException

### Tests (3 test suites)
- ✅ AuthServiceTest
- ✅ MateriaServiceTest
- ✅ PrerrequisitoServiceTest

### Configuración
- ✅ SecurityConfig
- ✅ SwaggerConfig
- ✅ application.yml
- ✅ schema.sql

---

## 🧪 EJECUTAR TESTS

```bash
./mvnw test
```

---

## 🔍 VALIDAR COMPILACIÓN

```bash
./mvnw clean compile
```

---

## 📦 EMPAQUETAR

```bash
./mvnw clean package -DskipTests
```

Genera: `target/Backend-0.0.1-SNAPSHOT.jar`

---

## 🚀 EJECUTAR JAR

```bash
java -jar target/Backend-0.0.1-SNAPSHOT.jar
```

---

## ⚙️ PROPIEDADES IMPORTANTES

Archivo: `src/main/resources/application.yml`

```yaml
server.port: 8080
spring.datasource.url: jdbc:mysql://localhost:3306/ruta_academica
spring.jpa.hibernate.ddl-auto: validate
jwt.secret: ${JWT_SECRET}
jwt.expiration: 86400000
```

---

## 🎯 FLUJO TÍPICO DE USO

1. **Registrarse** → POST /api/auth/register
2. **Login** → POST /api/auth/login (obtener token)
3. **Crear materias** → POST /api/materias
4. **Inscribir en materias** → POST /api/usuario-materias
5. **Crear actividades** → POST /api/actividades
6. **Registrar calificaciones** → POST /api/calificaciones
7. **Ver progreso** → GET /api/progreso/{usuarioId}
8. **Ver materias disponibles** → GET /api/prerrequisitos/disponibles/{usuarioId}

---

## ✅ ESTADO ACTUAL

- ✅ Backend completamente generado
- ✅ Compilación exitosa
- ✅ Todas las entidades configuradas
- ✅ JWT implementado
- ✅ CORS configurado
- ✅ Swagger documentado
- ✅ Tests unitarios incluidos
- ✅ Manejo de excepciones global
- ✅ Base de datos lista

---

## 📝 NOTAS

- Las contraseñas se hashean con BCrypt
- Los tokens expiran en 24 horas
- La base de datos usa InnoDB con índices optimizados
- CORS permitido solo para localhost:4200 y localhost:3000
- Todas las rutas protegidas requieren JWT

---

**¡Backend completamente listo para producción! 🚀**

