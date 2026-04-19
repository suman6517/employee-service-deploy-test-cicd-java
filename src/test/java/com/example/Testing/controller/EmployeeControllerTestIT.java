package com.example.Testing.controller;

import com.example.Testing.TestContainerConfiguration;
import com.example.Testing.dto.EmployeeDto;
import com.example.Testing.entities.Employee;
import com.example.Testing.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;





class EmployeeControllerTestIT extends AbstractIntregrationClass
{

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee TestEmployee;

    private EmployeeDto TestEmployeeDto;



    @BeforeEach
    void setUp()
    {
        TestEmployee = Employee.builder()
//                .id(1L)
                .name("Suman")
                .email("suman@gmail.com")
                .salary(200L)
                .build();

        TestEmployeeDto = EmployeeDto.builder()
                //.id(1L)
                .name("Suman")
                .email("suman@gmail.com")
                .salary(200L)
                .build();

        employeeRepository.deleteAll();

    }


    @Test
    void testGetEmployeeById_success()
    {
        Employee savedEmployee = employeeRepository.save(TestEmployee);

        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(TestEmployeeDto.getName());


    }

    @Test
    void testGetEmployeeById_Failure()
    {
        webTestClient.get()
                .uri("/employees/{id}", 1)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void testCreateEmployee_whenEmployeeExists_thenThrowException()
    {
        Employee savedEmployee = employeeRepository.save(TestEmployee);

        webTestClient.post()
                .uri("/employees")
                .bodyValue(TestEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();


    }

    @Test
    void testCreateEmployee_whenEmployeeDoesNotExists_thenCreateEmployee()
    {

        webTestClient.post()
                .uri("/employees")
                .bodyValue(TestEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo(TestEmployeeDto.getName())
                .jsonPath("$.email").isEqualTo(TestEmployeeDto.getEmail());
    }


    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException()
    {
        webTestClient.put()
                .uri("/employees/{id}", 1)
                .bodyValue(TestEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException()
    {
        Employee savedEmployee = employeeRepository.save(TestEmployee);

        TestEmployeeDto.setEmail("random@gmail.com");

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee()
    {
        Employee savedEmployee = employeeRepository.save(TestEmployee);

        TestEmployeeDto.setName("Animesh");
        TestEmployee.setSalary(250L);

        webTestClient.put()
                .uri("/employees/{id}", savedEmployee.getId())
                .bodyValue(TestEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.salary").isEqualTo(TestEmployeeDto.getSalary());

    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException()
    {
       webTestClient.delete()
               .uri("/employees/{id}", 1)
               .exchange()
               .expectStatus().isNotFound();

    }


    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee()
    {
        Employee savedEmployee = employeeRepository.save(TestEmployee);

        webTestClient.delete()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);


        // If the employee deleted we will not Get that
        webTestClient.delete()
                .uri("/employees/{id}", 1)
                .exchange()
                .expectStatus().isNotFound();

    }




}