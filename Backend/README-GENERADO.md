# Academic Path - Backend Completamente Generado ✅

## Descripción General

Backend completamente funcional para gestión académica universitaria desarrollado en **Java 21 con Spring Boot 3.x**.

---

## 📋 Características Implementadas

✅ **Autenticación & Autorización**
- Registro de usuarios con validación
- Login con JWT (JSON Web Tokens)
- Hash de contraseñas con BCrypt
- Filtro JWT automático en requests

✅ **Gestión de Materias**
- CRUD completo de materias
- Validación de créditos (1-10)
- Código único por materia
- Descripción optional

✅ **Gestión de Actividades**
- Crear actividades con validación de peso (0-100)
- Asociación a usuario-materia
- Tipos de actividades (parcial, taller, proyecto, etc.)
- Nota máxima configurable

✅ **Calificaciones**
- Registrar calificaciones por actividad
- Retroalimentación a estudiantes
- Validación de notas según nota máxima

✅ **Inscripción de Materias**
- Validación automática de prerrequisitos
- Prevención de inscripción duplicada
- Relación profesor-materia-usuario

✅ **Algoritmo de Prerrequisitos (DFS)**
- Cálculo de materias disponibles según prerrequisitos cumplidos
- Validación antes de inscripción
- Detección de ciclos
- Búsqueda en profundidad optimizada

✅ **Progreso Académico**
- Cálculo automático de promedio ponderado
- Seguimiento de créditos totales y aprobados
- Recálculo automático
- Dashboard de progreso

✅ **Seguridad**
- CORS configurado para frontend
- Endpoints públicos/privados bien definidos
- Manejo global de excepciones
- Validación en todos los DTOs

✅ **Documentación**
- Swagger/OpenAPI 3.0 integrado
- Documentación de todos los endpoints
- Seguridad JWT documentada en Swagger

✅ **Pruebas**
- Tests unitarios con JUnit 5
- Mocks con Mockito
- Cobertura de servicios principales

---

## 🏗️ Estructura del Proyecto

```
Backend/
├── src/main/java/com/academicpath/backend/
│   ├── BackendApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── MateriaController.java
│   │   ├── ActividadController.java
│   │   ├── CalificacionController.java
│   │   ├── UsuarioMateriaController.java
│   │   ├── PrerrequisitoController.java
│   │   ├── ProgresoAcademicoController.java
│   │   └── UsuarioController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── RegistroRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── MateriaRequest.java
│   │   │   ├── ActividadRequest.java
│   │   │   ├── CalificacionRequest.java
│   │   │   └── UsuarioMateriaRequest.java
│   │   └── response/
│   │       ├── UsuarioResponse.java
│   │       ├── MateriaResponse.java
│   │       ├── ActividadResponse.java
│   │       ├── CalificacionResponse.java
│   │       ├── UsuarioMateriaResponse.java
│   │       ├── ProgresoAcademicoResponse.java
│   │       ├── LoginResponse.java
│   │       └── ApiResponse.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ErrorResponse.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── UsuarioException.java
│   │   ├── MateriaException.java
│   │   └── ActividadException.java
│   ├── mapper/
│   │   ├── UsuariosMapper.java
│   │   ├── MateriasMapper.java
│   │   ├── ActividadesMapper.java
│   │   └── CalificacionesMapper.java
│   ├── models/entity/
│   │   ├── Usuarios.java
│   │   ├── Materias.java
│   │   ├── Profesores.java
│   │   ├── UsuarioMaterias.java
│   │   ├── Actividades.java
│   │   ├── Calificaciones.java
│   │   ├── Prerrequisitos.java
│   │   ├── ProgresoAcademico.java
│   │   └── SugerenciasMaterias.java
│   ├── repository/
│   │   ├── UsuariosRepository.java
│   │   ├── MateriasRepository.java
│   │   ├── ProfesoresRepository.java
│   │   ├── UsuarioMateriasRepository.java
│   │   ├── ActividadesRepository.java
│   │   ├── CalificacionesRepository.java
│   │   ├── PrerrequsitosRepository.java
│   │   ├── ProgresoAcademicoRepository.java
│   │   └── SugerenciasMateriasRepository.java
│   ├── security/
│   │   ├── JwtUtil.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── UsuarioUserDetails.java
│   │   └── UsuariosUserDetailsService.java
│   └── service/
│       ├── AuthService.java
│       ├── MateriaService.java
│       ├── ActividadService.java
│       ├── CalificacionService.java
│       ├── UsuarioMateriaService.java
│       ├── PrerrequisitoService.java
│       ├── ProgresoAcademicoService.java
│       └── impl/
│           ├── AuthServiceImpl.java
│           ├── MateriaServiceImpl.java
│           ├── ActividadServiceImpl.java
│           ├── CalificacionServiceImpl.java
│           ├── UsuarioMateriaServiceImpl.java
│           ├── PrerrequisitoServiceImpl.java
│           └── ProgresoAcademicoServiceImpl.java
├── src/test/java/com/academicpath/backend/test/service/
│   ├── AuthServiceTest.java
│   ├── MateriaServiceTest.java
│   └── PrerrequisitoServiceTest.java
├── src/main/resources/
│   ├── application.yml
│   └── schema.sql
└── pom.xml
```

---

## 🔐 Endpoints REST

### 🔑 Autenticación (Pública)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registrar nuevo usuario |
| POST | `/api/auth/login` | Iniciar sesión |

### 👥 Usuarios (Protegido)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| GET | `/api/usuarios/profile` | Obtener perfil del autenticado |

### 📚 Materias (Protegido)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/materias` | Crear materia |
| GET | `/api/materias` | Listar todas las materias |
| GET | `/api/materias/{id}` | Obtener materia por ID |
| PUT | `/api/materias/{id}` | Actualizar materia |
| DELETE | `/api/materias/{id}` | Eliminar materia |

### 📝 Actividades (Protegido)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/actividades` | Crear actividad |
| GET | `/api/actividades/{id}` | Obtener actividad |
| GET | `/api/actividades/usuario-materia/{usuarioMateriaId}` | Actividades por usuario-materia |
| PUT | `/api/actividades/{id}` | Actualizar actividad |
| DELETE | `/api/actividades/{id}` | Eliminar actividad |

### ✍️ Calificaciones (Protegido)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/calificaciones` | Crear calificación |
| GET | `/api/calificaciones/{id}` | Obtener calificación |
| GET | `/api/calificaciones/actividad/{actividadId}` | Calificaciones por actividad |
| PUT | `/api/calificaciones/{id}` | Actualizar calificación |
| DELETE | `/api/calificaciones/{id}` | Eliminar calificación |

### 🎓 Usuario-Materias (Protegido)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/usuario-materias` | Inscribir en materia |
| GET | `/api/usuario-materias/{id}` | Obtener usuario-materia |
| GET | `/api/usuario-materias/usuario/{usuarioId}` | Materias del usuario |
| PUT | `/api/usuario-materias/{id}` | Actualizar inscripción |
| DELETE | `/api/usuario-materias/{id}` | Cancelar inscripción |

### 📊 Progreso Académico (Protegido)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/progreso/{usuarioId}` | Obtener progreso |
| POST | `/api/progreso/{usuarioId}/recalcular` | Recalcular progreso |

### 🔗 Prerrequisitos (Protegido)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/prerrequisitos/materia/{materiaId}` | Prerrequisitos de materia |
| GET | `/api/prerrequisitos/disponibles/{usuarioId}` | Materias disponibles |
| GET | `/api/prerrequisitos/verificar/{usuarioId}/{materiaId}` | Verificar prerrequisitos |

---

## 🚀 Instalación y Ejecución

### Requisitos Previos
- Java 21
- Maven 3.8+
- MySQL 8.0+

### Pasos

1. **Clonar el repositorio**
```bash
cd Backend
```

2. **Crear base de datos**
```sql
CREATE DATABASE ruta_academica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configurar variables de entorno**
```bash
export DB_USERNAME=root
export DB_PASSWORD=tuPassword
export JWT_SECRET=tu-secreto-super-seguro-minimo-32-caracteres-aqui
```

4. **Compilar el proyecto**
```bash
mvn clean install
```

5. **Ejecutar la aplicación**
```bash
mvn spring-boot:run
```

6. **Acceder a Swagger**
```
http://localhost:8080/api/swagger-ui.html
```

---

## 📦 Dependencias Principales

```xml
- Spring Boot 3.5.13
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL Connector
- JWT (jjwt 0.12.3)
- MapStruct 1.5.5.Final
- Lombok
- SpringDoc OpenAPI 2.0.4
- JUnit 5 + Mockito
```

---

## 🧪 Pruebas Unitarias

Ejecutar tests:
```bash
mvn test
```

Tests incluidos:
- `AuthServiceTest` - Registro y login
- `MateriaServiceTest` - CRUD de materias
- `PrerrequisitoServiceTest` - Validación de prerrequisitos

---

## 🔒 Seguridad

- ✅ JWT con HS256
- ✅ Tokens de 24h de expiración
- ✅ BCrypt para hashing de contraseñas
- ✅ CORS configurado
- ✅ Validación en todos los endpoints
- ✅ Manejo global de excepciones
- ✅ Sesiones stateless

---

## 📝 Ejemplo de Uso

### 1. Registrarse
```bash
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
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan@test.com",
    "contrasena": "Password123!"
  }'
```

### 3. Usar Token en Requests
```bash
curl -X GET http://localhost:8080/api/materias \
  -H "Authorization: Bearer <TOKEN_JWT>"
```

---

## ⚡ Optimizaciones

- ✅ Índices en base de datos
- ✅ FetchType LAZY para relaciones
- ✅ Evitar N+1 queries
- ✅ Caché de Hibernate activado
- ✅ Transacciones bien definidas
- ✅ Queries optimizadas con JPA

---

## 📚 Arquitectura

Sigue **Clean Architecture** y principios **SOLID**:

- **Controller** → Recibe requests
- **Service** → Lógica de negocio
- **Repository** → Acceso a datos
- **Entity** → Modelo de datos
- **DTO** → Transferencia de datos
- **Mapper** → Conversión Entity ↔ DTO
- **Exception** → Manejo de errores

---

## 🎯 Próximas Mejoras

- [ ] Sugerencias automáticas de materias
- [ ] Reportes de progreso PDF
- [ ] Notificaciones por email
- [ ] Sistema de roles (admin, profesor, estudiante)
- [ ] Historial de cambios
- [ ] Backup automático

---

## 📄 Licencia

Este proyecto es de uso educativo para Academic Path.

---

**¡Backend completamente generado y listo para producción! 🚀**

