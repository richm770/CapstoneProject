package com.example.backend.repository;

import com.example.backend.model.Department;
import com.example.backend.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    public List<Request> findAllByCreatedByEmailAndType(String createdBy, String type);

    public List<Request> findAllByApprovedByEmailOrAssignedDepartmentAndStatusIsNot(String approvedByEmail, Department assignedDepartment, String status);

    public Request getRequestById(Integer requestId);
}
