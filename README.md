# Sistema de Gestión de Estudiantes (CRUD Full-Stack)

Aplicación web full-stack desarrollada con **Spring Boot** (Back-End) y **HTML/CSS/JavaScript** (Front-End) para la gestión completa de estudiantes mediante una API REST.

## Tecnologías
* **Back-End:** Java, Spring Boot, Maven, JUnit 5 / Mockito (17 pruebas unitarias).
* **Front-End:** HTML5, CSS3, JavaScript (Fetch API).
* **Diseño:** Diagrama de Clases UML elaborado en Editor DIA.

## Endpoints principales (`/students`)
* `GET /students` — Listar todos los estudiantes.
* `GET /students/{id}` — Consultar estudiante por ID.
* `POST /students` — Registrar nuevo estudiante.
* `PUT /students/{id}` — Actualización completa.
* `PATCH /students/{id}` — Actualización parcial.
* `DELETE /students/{id}` — Eliminar estudiante.

## Ejecución
1. Ejecutar `ProyectoApplication.java`.
2. Abrir `http://localhost:8080` en el navegador.