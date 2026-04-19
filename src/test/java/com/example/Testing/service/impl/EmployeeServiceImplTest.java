package com.example.Testing.service.impl;

import com.example.Testing.TestContainerConfiguration;
import com.example.Testing.dto.EmployeeDto;
import com.example.Testing.entities.Employee;
import com.example.Testing.exceptions.ResourceNotFoundException;
import com.example.Testing.repositories.EmployeeRepository;
import com.example.Testing.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest
{

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeServiceImpl;

    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;
    @BeforeEach
    void setUp()
    {
        mockEmployee = Employee.builder()
                .id(1L)
                .name("Suman")
                .email("suman@gmail.com")
                .salary(200L)
                .build();
        mockEmployeeDto = modelMapper.map(mockEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIdIsPresent_ThenReturnEmployeeDto()
    {

        // Assign
        Long id = mockEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee)); //Stubbing
        //Act

        EmployeeDto employeeDto = employeeServiceImpl.getEmployeeById(id);

        //Assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
        verify(employeeRepository , times(1)).findById(id);
    }

    // Second Class
    @Test
    void testGetEmployeeById_whenEmployeeIdIsNotPresent_ThenThrowException()
    {
        //arrange

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(() -> employeeServiceImpl.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");
        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee()
    {
        //Assign

        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        //Act
        EmployeeDto employeeDto = employeeServiceImpl.createNewEmployee(mockEmployeeDto);

        //Assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployeeDto.getEmail());

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository ).save(employeeArgumentCaptor.capture());

        Employee capturedEmployee = employeeArgumentCaptor.getValue();
        assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployeeDto.getEmail());


    }

    //Second Class
    @Test
    void testCreateNewEmployee_WhenAttemptingToCreateEmployeeWithExistingEmail_ThenThrowException()
    {
        //Arrange
        when(employeeRepository.findByEmail(mockEmployee.getEmail())).thenReturn(List.of(mockEmployee));

        //Act and Assert

        assertThatThrownBy(() -> employeeServiceImpl.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployeeDto.getEmail());

        verify(employeeRepository).findByEmail(mockEmployee.getEmail());
        verify(employeeRepository, never()).save(any());


    }

    @Test
    void testUpdateEmployee_WhenEmployeeDoesNotExist_ThenThrowException()
    {
        //Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //Assert And Act
        assertThatThrownBy(() -> employeeServiceImpl.updateEmployee(1L, mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any());

    }

    @Test
    void testUpdateEmployee_WhenAttemptingToUpdateEmail_ThenThrowException()
    {

        //Arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));

        mockEmployeeDto.setName("Random");
        mockEmployeeDto.setEmail("random@gmail.com");

        // Act and Assert
        assertThatThrownBy(() -> employeeServiceImpl.updateEmployee( mockEmployeeDto.getId(), mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository, never()).save(any());

    }

    @Test
    void testUpdateEmployee_WhenValidEmployee_ThenUpdateEmployee()
    {
        //Arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Random");
        mockEmployeeDto.setSalary(199L);
        Employee newEmployee = modelMapper.map(mockEmployeeDto , Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        // Act and Assert
        EmployeeDto UpdatedEmployeeDto = employeeServiceImpl.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto);

        assertThat(UpdatedEmployeeDto).isEqualTo(mockEmployeeDto);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());
    }

    // For Delete Employee The Test Cases Are Written From Here
    @Test
    void testDeleteEmployee_WhenEmployeeDoesNotExist_ThenThrowException()
    {
        //Arrange
        when(employeeRepository.existsById(1L)).thenReturn(false);

        // Act and Assert
        assertThatThrownBy(() -> employeeServiceImpl.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository , never()).deleteById(1L);

    }


    @Test
    void testDeleteEmployee_WhenEmployeeIsValid_ThenDeleteEmployee()
    {
        // Assert
        when(employeeRepository.existsById(1L)).thenReturn(true);

        // Act and Assert
        assertThatCode(() -> employeeServiceImpl.deleteEmployee(1L))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(1L);
    }
}