package com.example.ElearningTLU.Entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.NonFinal;

import java.util.ArrayList;
import java.util.List;

@Setter

@Getter

@AllArgsConstructor

@NoArgsConstructor

@ToString
@Entity
public  class Course {
    @Id
    private String courseId;
    private String courseName;
    private int credits;
    private double coefficient;
    private CourseType type;
    private int requestCredits;


    @ManyToMany
    @JsonIgnoreProperties({"courseName","credits","coefficient","type","requestCredits","prerequisites","courseSemesterGroups","listDepartment","listMajor","statisticsStudent"})
    private List<Course> Prerequisites = new ArrayList<>();


    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<Course_SemesterGroup> courseSemesterGroups= new ArrayList<>();

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<CourseDepartment> listDepartment= new ArrayList<>();

   @OneToMany(mappedBy = "course")
   @JsonIgnore
    private List<CourseMajor>listMajor= new ArrayList<>();

    @OneToOne(mappedBy = "course")
    @JsonIgnore
    private StatisticsStudent statisticsStudent;

}