# 📋 ÍNDICE DE DOCUMENTACIÓN - ACADEMIC PATH

## 🎯 Empezar Aquí

### 1️⃣ **INSTRUCCIONES-USO.md** ← COMIENZA AQUÍ
   - Guía paso a paso para ejecutar el backend
   - Ejemplos de uso con curl
   - Flujo típico de desarrollo

### 2️⃣ **RESUMEN-FINAL.md**
   - Estadísticas completas del proyecto
   - Lista de todos los componentes generados
   - Checklist de verificación

### 3️⃣ **Backend/README-GENERADO.md**
   - Documentación técnica detallada
   - Descripción de endpoints
   - Arquitectura y diseño

### 4️⃣ **VERIFICACION-FINAL.txt**
   - Resumen visual del proyecto
   - Estado de compilación y empaquetamiento
   - Quick start

---

## 📁 Estructura del Proyecto

```
AcademicPath/
├── INSTRUCCIONES-USO.md          ← LEER PRIMERO
├── RESUMEN-FINAL.md
├── VERIFICACION-FINAL.txt
├── ejecucionDelProyecto.MD
├── README.md
└── Backend/
    ├── README-GENERADO.md
    ├── setup.sh
    ├── pom.xml
    ├── src/
    │   ├── main/
    │   │   ├── java/com/academicpath/backend/
    │   │   │   ├── config/           (SecurityConfig, SwaggerConfig)
    │   │   │   ├── controller/       (8 controladores)
    │   │   │   ├── dto/              (14 DTOs)
    │   │   │   ├── exception/        (5 excepciones)
    │   │   │   ├── mapper/           (4 mappers)
    │   │   │   ├── models/entity/    (9 entidades)
    │   │   │   ├── repository/       (9 repositorios)
    │   │   │   ├── security/         (4 clases JWT)
    │   │   │   └── service/          (14 servicios)
    │   │   └── resources/
    │   │       ├── application.yml
    │   │       └── schema.sql
    │   └── test/java/
    │       └── com/academicpath/backend/test/
    │           ├── service/          (3 test services)
    │           └── controller/       (1 test controller)
    └── target/
        └── Backend-0.0.1-SNAPSHOT.jar  (JAR empaquetado)
```

---

## 🚀 Quick Start (Resumen)

```bash
# 1. Crear BD
mysql -u root -p
CREATE DATABASE ruta_academica CHARACTER SET utf8mb4;

# 2. Variables de entorno
export DB_USERNAME=root
export DB_PASSWORD=tu_password
export JWT_SECRET=minimo-32-caracteres

# 3. Ejecutar
cd Backend
./mvnw spring-boot:run

# 4. Acceder
# API: http://localhost:8080/api
# Swagger: http://localhost:8080/api/swagger-ui.html
```

---

## 📚 Documentación por Tipo

### 🔐 Seguridad & Autenticación
- Leer: `Backend/README-GENERADO.md` → Sección "Seguridad"
- Ver: `Backend/src/main/java/com/academicpath/backend/security/`
- Config: `Backend/src/main/java/com/academicpath/backend/config/SecurityConfig.java`

### 🗄️ Base de Datos
- Schema: `Backend/src/main/resources/schema.sql`
- Entidades: `Backend/src/main/java/com/academicpath/backend/models/entity/`
- Repositorios: `Backend/src/main/java/com/academicpath/backend/repository/`

### 🧠 Lógica de Negocio
- Servicios: `Backend/src/main/java/com/academicpath/backend/service/`
- Algoritmo DFS: `Backend/src/main/java/com/academicpath/backend/service/impl/PrerrequisitoServiceImpl.java`
- Cálculo de promedio: `Backend/src/main/java/com/academicpath/backend/service/impl/ProgresoAcademicoServiceImpl.java`

### 🌐 Endpoints REST
- Leer: `INSTRUCCIONES-USO.md` → Sección "Endpoints Principales"
- Swagger interactivo: `/api/swagger-ui.html` (después de ejecutar)
- Controladores: `Backend/src/main/java/com/academicpath/backend/controller/`

### 🧪 Pruebas
- Tests: `Backend/src/test/java/com/academicpath/backend/test/`
- Ejecutar: `./mvnw test`

---

## 💡 Ejemplos de Uso

### Registrarse
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombres": "Juan",
    "apellidos": "Pérez",
    "correo": "juan@test.com",
    "contrasena": "Password123!",
    "universidad": "UdeA",
    "carrera": "Ingeniería"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "juan@test.com",
    "contrasena": "Password123!"
  }'
# Respuesta: { "accessToken": "eyJhbG..." }
```

### Usar Token
```bash
curl -X GET http://localhost:8080/api/materias \
  -H "Authorization: Bearer eyJhbG..."
```

---

## 🎯 Guía por Caso de Uso

### 📚 Agregar Materias
1. Login para obtener token
2. POST `/api/materias` con datos de materia
3. Ver materias: GET `/api/materias`

### 👤 Inscribirse en Materia
1. Verificar prerrequisitos: GET `/api/prerrequisitos/verificar/{usuarioId}/{materiaId}`
2. Inscribirse: POST `/api/usuario-materias`
3. Ver mis materias: GET `/api/usuario-materias/usuario/{usuarioId}`

### 📝 Registrar Actividades
1. Crear actividad: POST `/api/actividades`
2. Registrar calificación: POST `/api/calificaciones`
3. Ver calificaciones: GET `/api/calificaciones/actividad/{actividadId}`

### 📊 Ver Progreso
1. GET `/api/progreso/{usuarioId}` para ver créditos y promedio
2. POST `/api/progreso/{usuarioId}/recalcular` para actualizar

---

## 🔧 Configuración

### Application.yml
```yaml
server.port: 8080
spring.datasource.url: jdbc:mysql://localhost:3306/ruta_academica
spring.jpa.hibernate.ddl-auto: validate
jwt.secret: ${JWT_SECRET}
jwt.expiration: 86400000
```

### Variables de Entorno Requeridas
- `DB_USERNAME` - Usuario MySQL
- `DB_PASSWORD` - Contraseña MySQL
- `JWT_SECRET` - Mínimo 32 caracteres

---

## 📦 Dependencias Clave

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| Spring Boot | 3.5.13 | Framework principal |
| Spring Security | - | Autenticación/Autorización |
| Spring Data JPA | - | Acceso a datos |
| JWT (jjwt) | 0.12.3 | Tokens JWT |
| MapStruct | 1.5.5 | Mapeo de objetos |
| Lombok | - | Boilerplate |
| MySQL Driver | - | Base de datos |
| SpringDoc OpenAPI | 2.0.4 | Documentación Swagger |

---

## ✅ Verificación

Ejecutar para verificar todo está bien:

```bash
# Compilar
./mvnw clean compile
# Salida: BUILD SUCCESS

# Tests
./mvnw test
# Salida: BUILD SUCCESS

# Empaquetar
./mvnw clean package -DskipTests
# Genera: target/Backend-0.0.1-SNAPSHOT.jar
```

---

## 🆘 Solución de Problemas

### Error de compilación
```bash
# Limpiar caché
./mvnw clean
# Reintentar
./mvnw compile
```

### Puerto 8080 en uso
```yaml
# En application.yml
server.port: 8081  # Cambiar puerto
```

### Conexión a BD fallida
```bash
# Verificar variables de entorno
echo $DB_USERNAME
echo $DB_PASSWORD

# Crear BD si no existe
mysql -u root -p -e "CREATE DATABASE ruta_academica;"
```

---

## 📞 Archivos de Referencia Rápida

| Archivo | Contenido |
|---------|----------|
| `Backend/pom.xml` | Dependencias Maven |
| `Backend/src/main/resources/application.yml` | Configuración |
| `Backend/src/main/resources/schema.sql` | Script SQL |
| `Backend/src/main/java/...` | Código fuente (77+ archivos) |
| `Backend/src/test/java/...` | Tests unitarios |
| `README-GENERADO.md` | Documentación completa |

---

## 🎓 Para Aprender Más

1. Ver Swagger en `/api/swagger-ui.html` durante ejecución
2. Leer `README-GENERADO.md` para documentación detallada
3. Ver código fuente con comentarios en `service/impl/`
4. Ejecutar tests para entender el flujo: `./mvnw test`

---

## 📝 Notas Importantes

- ✅ Backend completamente compilado
- ✅ Todas las funcionalidades implementadas
- ✅ Seguridad JWT activada
- ✅ Base de datos diseñada
- ✅ Tests incluidos
- ✅ Documentación completa
- ⏳ Próximo: Frontend

---

## 🚀 Próximos Pasos

1. Ejecutar el backend
2. Probar endpoints en Swagger
3. Integrar con frontend
4. Deploy en servidor

---

**¡Documentación completa disponible. Empezar por INSTRUCCIONES-USO.md**

