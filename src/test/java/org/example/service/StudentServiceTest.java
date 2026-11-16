package org.example.service;

import org.example.exception.DuplicateEmailException;
import org.example.exception.StudentNotFoundException;
import org.example.model.Student;
import org.example.model.StudentPatchDTO;
import org.example.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student(1L, "Juan Pérez", "juan@example.com", "1234567890", "español");
    }

    @Test
    void getAllStudents_DeberiaRetornarListaDeEstudiantes() {
        when(studentRepository.findAll()).thenReturn(List.of(sampleStudent));

        List<Student> result = studentService.getAllStudents();

        assertEquals(1, result.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentById_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudentById(99L));
    }

    @Test
    void createStudent_CuandoCorreoEsUnico_DeberiaGuardarYRetornar() {
        when(studentRepository.existsByCorreo("juan@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(sampleStudent);

        Student created = studentService.createStudent(sampleStudent);

        assertNotNull(created);
        assertEquals("Juan Pérez", created.getNombre());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudent_CuandoCorreoEstaDuplicado_DeberiaLanzarDuplicateEmailException() {
        when(studentRepository.existsByCorreo("juan@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> studentService.createStudent(sampleStudent));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void updateStudentFull_CuandoDatosSonValidos_DeberiaActualizarTodosLosCampos() {
        Student updatedInfo = new Student(null, "Juan Actualizado", "nuevo@example.com", "0987654321", "inglés");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.existsByCorreoAndIdNot("nuevo@example.com", 1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        Student result = studentService.updateStudentFull(1L, updatedInfo);

        assertEquals("Juan Actualizado", result.getNombre());
        assertEquals("nuevo@example.com", result.getCorreo());
        assertEquals("0987654321", result.getNumeroTelefono());
        assertEquals("inglés", result.getIdioma());
    }

    @Test
    void updateStudentPartial_CuandoSoloSeEnviaIdioma_DeberiaModificarSoloIdioma() {
        StudentPatchDTO patch = new StudentPatchDTO();
        patch.setIdioma("francés");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        Student result = studentService.updateStudentPartial(1L, patch);

        assertEquals("francés", result.getIdioma());
        assertEquals("Juan Pérez", result.getNombre()); // Conserva los demás campos
    }

    @Test
    void deleteStudent_CuandoExiste_DeberiaEliminar() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).deleteById(1L);
    }
}