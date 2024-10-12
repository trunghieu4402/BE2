package com.example.ElearningTLU.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Class_Student")
public class Class_Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long Id;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Class aClass;

    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    private float midScore;
    private float endScore;
}
