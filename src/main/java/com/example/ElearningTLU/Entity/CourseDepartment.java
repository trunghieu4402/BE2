package com.example.ElearningTLU.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CourseDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "courseId",nullable = false)
    private Course course;
    @ManyToOne
    @JoinColumn(name = "departmentId",nullable = false)
    private Department department;
}
