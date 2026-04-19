package com.example.Testing.repositories;

import com.example.Testing.TestContainerConfiguration;
import com.example.Testing.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@DataJpaTest
@Import(TestContainerConfiguration.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EmployeeRepositoryTest
{

    @Autowired
    private EmployeeRepository employeeRepository;
    private Employee employee;

    @BeforeEach
    void setUp()
    {
        employee = Employee.builder()
                .name("Suman")
                .email("suman@gmail.com")
                .salary(100L)
                .build();
    }
    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee()
    {
        // Arrange , Given

        employeeRepository.save(employee);

        // Act  , When

        List<Employee> employeeList = employeeRepository.findByEmail(employee.getEmail());

        // Assert , Then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.get(0).getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmployeeEmptyList()
    {
        //Given

        String email = "notpresent123@gmail.com";

        //When

        List<Employee> employeeList = employeeRepository.findByEmail(email);

        //Then

        assertThat(employeeList).isNotNull();
        assertThat(employeeList).isEmpty();
    }

}