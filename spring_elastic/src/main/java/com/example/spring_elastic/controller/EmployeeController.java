package com.example.spring_elastic.controller;

import com.example.spring_elastic.model.Employee;
import com.example.spring_elastic.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) throws IOException {
        String id = employeeService.saveEmployee(employee);
        return ResponseEntity.ok("Employee added with ID: " + id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) throws IOException {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<Employee>> getAllEmployees() throws IOException {
        Iterable<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) throws IOException {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok("Employee deleted with ID: " + id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable String id, @RequestBody Employee employee) throws IOException {
        String updatedId = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok("Employee updated with ID: " + updatedId);
    }
}
