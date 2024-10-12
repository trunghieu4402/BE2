package com.example.ElearningTLU.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CourseMajor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private long id;

    @ManyToOne
    @JoinColumn(name = "courseId")
    private Course course;


    @ManyToOne
    @JoinColumn(name = "majorId")
    private Major major;
}
