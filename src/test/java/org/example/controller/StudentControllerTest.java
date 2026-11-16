package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.StudentNotFoundException;
import org.example.model.Student;
import org.example.model.StudentPatchDTO;
import org.example.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllStudents_DeberiaRetornar200YLista() throws Exception {
        Student s = new Student(1L, "Juan Pérez", "juan@example.com", "1234567890", "español");
        when(studentService.getAllStudents()).thenReturn(List.of(s));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"));
    }

    @Test
    void getStudentById_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(studentService.getStudentById(99L)).thenThrow(new StudentNotFoundException("No encontrado"));

        mockMvc.perform(get("/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createStudent_ConDatosValidos_DeberiaRetornar201() throws Exception {
        Student input = new Student(null, "Juan Pérez", "juan@example.com", "1234567890", "español");
        Student saved = new Student(1L, "Juan Pérez", "juan@example.com", "1234567890", "español");

        when(studentService.createStudent(any(Student.class))).thenReturn(saved);

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createStudent_ConTelefonoInvalidoOIdiomaNoPermitido_DeberiaRetornar400() throws Exception {
        Student invalid = new Student(null, "", "correo-malo", "12345", "alemán");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists())
                .andExpect(jsonPath("$.correo").exists())
                .andExpect(jsonPath("$.numeroTelefono").exists())
                .andExpect(jsonPath("$.idioma").exists());
    }

    @Test
    void partialUpdateStudent_DeberiaRetornar200() throws Exception {
        StudentPatchDTO patch = new StudentPatchDTO();
        patch.setIdioma("inglés");
        Student updated = new Student(1L, "Juan Pérez", "juan@example.com", "1234567890", "inglés");

        when(studentService.updateStudentPartial(eq(1L), any(StudentPatchDTO.class))).thenReturn(updated);

        mockMvc.perform(patch("/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idioma").value("inglés"));
    }

    @Test
    void deleteStudent_DeberiaRetornar200() throws Exception {
        mockMvc.perform(delete("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Estudiante eliminado correctamente"));
    }
}