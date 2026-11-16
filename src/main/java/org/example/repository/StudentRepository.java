package org.example.repository;

import org.example.model.Student;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class StudentRepository {

    private final Map<Long, Student> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Student> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean existsByCorreo(String correo) {
        return storage.values().stream()
                .anyMatch(s -> s.getCorreo().equalsIgnoreCase(correo));
    }

    public boolean existsByCorreoAndIdNot(String correo, Long id) {
        return storage.values().stream()
                .anyMatch(s -> !s.getId().equals(id) && s.getCorreo().equalsIgnoreCase(correo));
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(idGenerator.getAndIncrement());
        }
        storage.put(student.getId(), student);
        return student;
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }
}