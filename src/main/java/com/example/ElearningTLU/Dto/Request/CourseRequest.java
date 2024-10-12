package com.example.ElearningTLU.Dto.Request;

import com.example.ElearningTLU.Entity.CourseType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseRequest {
    private String CourseId;
    private String CourseName;
    private int Credits;
    private double Coefficient;
    private CourseType type;
    private List<String> majorId= new ArrayList<>();
    private List<String> departmentId = new ArrayList<>();
    private List<String> reqiId = new ArrayList<>();
    private int requestCredits;

}
