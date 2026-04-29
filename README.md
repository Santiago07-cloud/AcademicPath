# Academic Path

Plataforma web para que estudiantes universitarios gestionen su ruta academica: materias, actividades, calificaciones y progreso academico.

## Estructura del repositorio

```
AcademicPath/
├── Backend/          # API REST - Spring Boot 3.5 + Java 17
└── Frontend/         # SPA - Angular 17+ (standalone components)
```

## Tecnologias

| Capa | Tecnologia |
|------|-----------|
| Backend | Java 17 + Spring Boot 3.5 |
| Seguridad | Spring Security + JWT |
| Base de datos | MySQL 8 + Spring Data JPA |
| API Docs | Swagger / OpenAPI 3 |
| Frontend | Angular 17+ + TypeScript |
| Estilos | SCSS con tema oscuro personalizado |

## Como ejecutar

### Backend
```bash
cd Backend
./mvnw spring-boot:run
# Disponible en http://localhost:8080/api
# Swagger en http://localhost:8080/api/swagger-ui/index.html
```

### Frontend
```bash
cd Frontend
npm install
npx ng serve --port 4200
# Disponible en http://localhost:4200
```

## Variables de entorno (Backend)

Crear archivo `Backend/src/main/resources/application.yml` con:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ruta_academica
    username: TU_USUARIO
    password: TU_PASSWORD
jwt:
  secret: TU_SECRET_MINIMO_32_CARACTERES
```

## Ramas

- `main` — codigo estable
- `feature/solucionErroresBackend` — desarrollo activo
