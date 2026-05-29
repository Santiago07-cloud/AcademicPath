# AcademicPath 🎓

Plataforma web para que estudiantes universitarios gestionen su ruta académica: materias, calificaciones, actividades, agenda y progreso académico, todo en un solo lugar.

🌐 **Demo en producción:** [academic-path-tau.vercel.app](https://academic-path-tau.vercel.app)

---

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Frontend | Angular 21 + TypeScript 5.9 |
| Estilos | SCSS — tema oscuro personalizado |
| Backend | Java 21 + Spring Boot 3.5 |
| Seguridad | Spring Security + JWT (jjwt 0.12.3) + BCrypt |
| Base de datos | PostgreSQL 17 (Supabase) |
| Email | Brevo SMTP |
| API Docs | Swagger / OpenAPI 3 |
| Deploy Frontend | Vercel |
| Deploy Backend | Render (Docker) |

---

## Estructura del repositorio

```
AcademicPath/
├── Frontend/                          # SPA Angular 21
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/                  # Guards, interceptors, servicios, modelos
│   │   │   ├── features/              # Módulos lazy: dashboard, auth, etc.
│   │   │   └── shared/               # Componentes reutilizables
│   │   └── environments/             # environment.ts / environment.prod.ts
│   └── vercel.json                   # Configuración SPA routing
└── Backend/                          # API REST Spring Boot
    ├── Dockerfile                    # Multi-stage build Maven + JRE Alpine
    └── src/main/java/com/academicpath/backend/
        ├── config/                   # SecurityConfig, OpenAPIConfig
        ├── controller/               # Endpoints REST
        ├── service/                  # Lógica de negocio
        ├── repository/              # Spring Data JPA
        ├── entity/                  # Entidades JPA
        ├── dto/                     # Data Transfer Objects
        ├── security/                # JWT filter y utils
        └── exception/               # Manejo global de errores
```

---

## Funcionalidades principales

- 🔐 Registro, login y recuperación de contraseña por correo
- 📚 Gestión de materias y prerrequisitos
- 📊 Seguimiento de calificaciones y progreso académico
- 📅 Agenda de actividades y entregas
- 👨‍🏫 Gestión de profesores
- 📈 Dashboard con estadísticas académicas

---

## Ejecutar localmente

### Requisitos previos
- Java 21+
- Node.js 20+
- PostgreSQL (o cuenta en Supabase)

### Backend

```bash
cd Backend
# Configurar variables de entorno (ver .env.example)
./mvnw spring-boot:run
# API disponible en: http://localhost:8080/api
# Swagger en:       http://localhost:8080/api/swagger-ui.html
```

### Frontend

```bash
cd Frontend
npm install
npm start
# App disponible en: http://localhost:4200
```

---

## Variables de entorno

### Backend — crear `Backend/src/main/resources/application-local.properties`

```properties
spring.datasource.url=jdbc:postgresql://HOST:6543/postgres?prepareThreshold=0
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
jwt.secret=TU_SECRET_MINIMO_32_CARACTERES
spring.mail.username=TU_CORREO@gmail.com
spring.mail.password=TU_APP_PASSWORD
app.frontend.url=http://localhost:4200
```

> Ver `.env.example` para la lista completa de variables necesarias en producción.

---

## Despliegue en producción

| Servicio | Plataforma | URL |
|----------|------------|-----|
| Frontend | Vercel | [academic-path-tau.vercel.app](https://academic-path-tau.vercel.app) |
| Backend | Render | [academicpath.onrender.com](https://academicpath.onrender.com) |
| Base de datos | Supabase | PostgreSQL administrado |

> ⚠️ El plan gratuito de Render tiene cold start de ~30 segundos tras inactividad.

---

## Equipo

Desarrollado por estudiantes de Tecnología en Sistemas como proyecto académico.
