CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombres VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    universidad VARCHAR(100),
    carrera VARCHAR(100),
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS materias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    creditos INT NOT NULL,
    descripcion TEXT,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS profesores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario_materias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    materia_id BIGINT NOT NULL,
    profesor_id BIGINT,
    semestre INT NOT NULL,
    anio INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    nota_final DECIMAL(5,2),
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_usuario_materia (usuario_id, materia_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (materia_id) REFERENCES materias(id) ON DELETE CASCADE,
    FOREIGN KEY (profesor_id) REFERENCES profesores(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS actividades (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_materia_id BIGINT NOT NULL,
    titulo VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    peso DECIMAL(5,2) NOT NULL,
    nota_maxima DECIMAL(5,2) NOT NULL,
    fecha_entrega DATETIME,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_materia_id) REFERENCES usuario_materias(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS calificaciones (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    actividad_id BIGINT NOT NULL,
    nota DECIMAL(5,2) NOT NULL,
    retroalimentacion TEXT,
    fecha_calificacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (actividad_id) REFERENCES actividades(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS prerrequisitos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    materia_id BIGINT NOT NULL,
    materia_prerrequisito_id BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_prerrequisito (materia_id, materia_prerrequisito_id),
    FOREIGN KEY (materia_id) REFERENCES materias(id) ON DELETE CASCADE,
    FOREIGN KEY (materia_prerrequisito_id) REFERENCES materias(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS progreso_academico (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL UNIQUE,
    creditos_totales INT NOT NULL DEFAULT 0,
    creditos_aprobados INT NOT NULL DEFAULT 0,
    promedio DECIMAL(3,2) NOT NULL DEFAULT 0,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sugerencias_materias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    materia_id BIGINT NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sugerencia (usuario_id, materia_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (materia_id) REFERENCES materias(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para optimizar consultas frecuentes
CREATE INDEX idx_usuario_correo ON usuarios(correo);
CREATE INDEX idx_usuario_materias_usuario ON usuario_materias(usuario_id);
CREATE INDEX idx_usuario_materias_materia ON usuario_materias(materia_id);
CREATE INDEX idx_actividades_usuario_materia ON actividades(usuario_materia_id);
CREATE INDEX idx_calificaciones_actividad ON calificaciones(actividad_id);
CREATE INDEX idx_sugerencias_usuario ON sugerencias_materias(usuario_id);
CREATE INDEX idx_progreso_usuario ON progreso_academico(usuario_id);
