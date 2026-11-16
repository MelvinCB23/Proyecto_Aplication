package org.example.service;

import org.example.exception.DuplicateEmailException;
import org.example.exception.StudentNotFoundException;
import org.example.model.Student;
import org.example.model.StudentPatchDTO;
import org.example.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    public Student createStudent(Student student) {
        if (studentRepository.existsByCorreo(student.getCorreo())) {
            throw new DuplicateEmailException("El correo electrónico ya está registrado: " + student.getCorreo());
        }
        student.setId(null); // Asegura que el ID sea autogenerado
        return studentRepository.save(student);
    }

    public Student updateStudentFull(Long id, Student updatedData) {
        Student existing = getStudentById(id);

        if (studentRepository.existsByCorreoAndIdNot(updatedData.getCorreo(), id)) {
            throw new DuplicateEmailException("El correo electrónico ya pertenece a otro estudiante: " + updatedData.getCorreo());
        }

        existing.setNombre(updatedData.getNombre());
        existing.setCorreo(updatedData.getCorreo());
        existing.setNumeroTelefono(updatedData.getNumeroTelefono());
        existing.setIdioma(updatedData.getIdioma());

        return studentRepository.save(existing);
    }

    public Student updateStudentPartial(Long id, StudentPatchDTO patchData) {
        Student existing = getStudentById(id);

        if (patchData.getNombre() != null && !patchData.getNombre().isBlank()) {
            existing.setNombre(patchData.getNombre());
        }
        if (patchData.getCorreo() != null && !patchData.getCorreo().isBlank()) {
            if (studentRepository.existsByCorreoAndIdNot(patchData.getCorreo(), id)) {
                throw new DuplicateEmailException("El correo electrónico ya pertenece a otro estudiante: " + patchData.getCorreo());
            }
            existing.setCorreo(patchData.getCorreo());
        }
        if (patchData.getNumeroTelefono() != null && !patchData.getNumeroTelefono().isBlank()) {
            existing.setNumeroTelefono(patchData.getNumeroTelefono());
        }
        if (patchData.getIdioma() != null && !patchData.getIdioma().isBlank()) {
            existing.setIdioma(patchData.getIdioma());
        }

        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        if (studentRepository.findById(id).isEmpty()) {
            throw new StudentNotFoundException("Estudiante no encontrado con ID: " + id);
        }
        studentRepository.deleteById(id);
    }
}