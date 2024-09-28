package com.example.spring_elastic.service;

import com.example.spring_elastic.model.Employee;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    private RestHighLevelClient client;

    // Save Employee to Elasticsearch
    public String saveEmployee(Employee employee) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", employee.getName());
        jsonMap.put("department", employee.getDepartment());
        jsonMap.put("salary", employee.getSalary());

        IndexRequest request = new IndexRequest("employees").source(jsonMap, XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        return response.getId();
    }

    // Get Employee by ID from Elasticsearch
    public Optional<Employee> getEmployeeById(String id) throws IOException {
        GetRequest getRequest = new GetRequest("employees", id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.isExists()) {
            Map<String, Object> source = getResponse.getSourceAsMap();
            String name = (String) source.get("name");
            String department = (String) source.get("department");
            double salary = (double) source.get("salary");

            Employee employee = new Employee(id, name, department, salary);
            return Optional.of(employee);
        }
        return Optional.empty();
    }

    // Get all Employees from Elasticsearch
    public Iterable<Employee> getAllEmployees() throws IOException {
        SearchRequest searchRequest = new SearchRequest("employees");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        List<Employee> employees = new ArrayList<>();

        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String id = hit.getId();
            String name = (String) sourceAsMap.get("name");
            String department = (String) sourceAsMap.get("department");
            double salary = (double) sourceAsMap.get("salary");

            Employee employee = new Employee(id, name, department, salary);
            employees.add(employee);
        }

        return employees;
    }

    // Delete Employee by ID from Elasticsearch
    public void deleteEmployeeById(String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("employees", id);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("Deleted employee with ID: " + deleteResponse.getId());
    }

    // Update Employee in Elasticsearch
    public String updateEmployee(String id, Employee employee) throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", employee.getName());
        jsonMap.put("department", employee.getDepartment());
        jsonMap.put("salary", employee.getSalary());

        UpdateRequest updateRequest = new UpdateRequest("employees", id).doc(jsonMap, XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        return updateResponse.getId();
    }
}
