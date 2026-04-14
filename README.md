# AcademicPath

Plataforma académica para la gestión integral de rutas de aprendizaje y seguimiento de estudiantes.

## 📋 Descripción

AcademicPath es una aplicación de backend desarrollada con **Spring Boot** que proporciona servicios API para gestionar usuarios, autenticación y datos académicos. La arquitectura sigue patrones profesionales con capas de DAO, Manager y Service para garantizar escalabilidad y mantenibilidad.

## 🏗️ Estructura del Proyecto

```
AcademicPath/
├── Backend/                    # Aplicación backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/academicpath/backend/
│   │   │   │       ├── BackendApplication.java
│   │   │   │       ├── config/              # Configuración
│   │   │   │       ├── dao/                 # Data Access Objects
│   │   │   │       ├── manager/             # Lógica de negocio
│   │   │   │       ├── models/              # Modelos y entidades
│   │   │   │       └── services/            # Servicios
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── schema.sql
│   │   └── test/
│   │       └── java/...       # Tests unitarios
│   ├── pom.xml                # Configuración Maven
│   ├── mvnw                   # Maven Wrapper (Linux)
│   └── mvnw.cmd               # Maven Wrapper (Windows)
├── .gitignore                 # Archivos ignorados por Git
├── .gitattributes             # Atributos de Git
└── README.md                  # Este archivo

```

## 🚀 Requisitos

- **Java 21** o superior
- **Maven 3.6.0** o superior
- **Base de datos** (MySQL/PostgreSQL)

## 🔧 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Santiago007-cloud/AcademicPath.git
cd AcademicPath
```

### 2. Configurar la Base de Datos

Edita el archivo `Backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/academic_path
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

### 3. Compilar y Ejecutar

```bash
cd Backend
./mvnw clean install          # Linux/Mac
mvnw.cmd clean install        # Windows

# Ejecutar la aplicación
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

La aplicación estará disponible en `http://localhost:8080`

## 📚 API Endpoints

### Usuarios
- `GET /api/usuarios` - Obtener todos los usuarios
- `POST /api/usuarios` - Crear nuevo usuario
- `GET /api/usuarios/{id}` - Obtener usuario por ID
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `DELETE /api/usuarios/{id}` - Eliminar usuario

## 🔐 Autenticación

La aplicación implementa seguridad con **Spring Security**. Se requiere autenticación para acceder a los endpoints protegidos.

### Credenciales por defecto:
```
Usuario: admin
Contraseña: admin123
```

## 🏭 Tecnologías Utilizadas

- **Spring Boot 3.5.13** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Acceso a datos
- **MySQL/PostgreSQL** - Base de datos
- **Maven** - Gestor de dependencias
- **Java 21** - Lenguaje de programación

## 📖 Documentación API

La documentación OpenAPI está disponible en:
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## 🧪 Tests

Ejecutar los tests unitarios:

```bash
cd Backend
./mvnw test                   # Linux/Mac
mvnw.cmd test                 # Windows
```

## 📝 Git Workflow

### Archivos Ignorados
El archivo `.gitignore` está configurado para excluir:
- Carpetas `target/` y `build/`
- IDE files (`.idea/`, `.vscode/`)
- Archivos compilados y dependencias
- Archivos de configuración sensibles (`.env`)

### Commits
```bash
git add .
git commit -m "Descripción del cambio"
git push origin main
```

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo licencia MIT.

## 👨‍💻 Autor

**Santiago Alberto Sañudo**
- GitHub: [@Santiago007-cloud](https://github.com/Santiago007-cloud)

## 📞 Soporte

Para reportar problemas o sugerencias, abre un [Issue](https://github.com/Santiago007-cloud/AcademicPath/issues).
