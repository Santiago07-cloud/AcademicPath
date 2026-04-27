#!/bin/bash

# Script de instalación y ejecución de Academic Path Backend

echo "=========================================="
echo "Academic Path - Backend Setup"
echo "=========================================="
echo ""

# Verificar Java
echo "✓ Verificando Java..."
java -version
echo ""

# Crear base de datos
echo "✓ Creando base de datos..."
echo "Ejecuta esto en MySQL:"
echo "CREATE DATABASE ruta_academica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
echo ""

# Variables de entorno
echo "✓ Configurando variables de entorno..."
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=tu-secreto-super-seguro-minimo-32-caracteres-aqui

# Compilar
echo "✓ Compilando proyecto..."
./mvnw clean install -DskipTests

# Ejecutar
echo "✓ Ejecutando aplicación..."
./mvnw spring-boot:run

echo ""
echo "=========================================="
echo "Aplicación ejecutándose en: http://localhost:8080"
echo "Swagger: http://localhost:8080/api/swagger-ui.html"
echo "=========================================="

