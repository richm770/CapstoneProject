package com.example.backend.repository;

import com.example.backend.model.Department;
import com.example.backend.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, String> {

    Optional<Faculty> findByEmail(String email);

    Optional<Faculty> findFacultyByDepartment(Department department);

}
