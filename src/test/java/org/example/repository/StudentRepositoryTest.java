package org.example.repository;

import org.example.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudentRepositoryTest {

    private StudentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new StudentRepository();
    }

    @Test
    void save_DeberiaAsignarIdAutoincrementableYGuardarEstudiante() {
        Student s1 = new Student(null, "Ana López", "ana@correo.com", "1234567890", "español");
        Student s2 = new Student(null, "Carlos Ruiz", "carlos@correo.com", "0987654321", "inglés");

        Student saved1 = repository.save(s1);
        Student saved2 = repository.save(s2);

        assertEquals(1L, saved1.getId());
        assertEquals(2L, saved2.getId());
        assertEquals(2, repository.findAll().size());
    }

    @Test
    void findById_CuandoExiste_DeberiaRetornarEstudiante() {
        Student saved = repository.save(new Student(null, "Ana López", "ana@correo.com", "1234567890", "español"));

        Optional<Student> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Ana López", found.get().getNombre());
    }

    @Test
    void existsByCorreo_DeberiaDetectarDuplicadosSinImportarMayusculas() {
        repository.save(new Student(null, "Ana López", "ana@correo.com", "1234567890", "español"));

        assertTrue(repository.existsByCorreo("ANA@CORREO.COM"));
        assertFalse(repository.existsByCorreo("otro@correo.com"));
    }

    @Test
    void deleteById_DeberiaEliminarEstudianteDeMemoria() {
        Student saved = repository.save(new Student(null, "Ana López", "ana@correo.com", "1234567890", "español"));

        repository.deleteById(saved.getId());

        assertTrue(repository.findById(saved.getId()).isEmpty());
        assertEquals(0, repository.findAll().size());
    }
}