package com.example.Testing.repositories;


import com.example.Testing.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long>
{
    List<Employee> findByEmail(String email);
}
