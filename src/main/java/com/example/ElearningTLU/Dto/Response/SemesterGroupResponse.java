package com.example.ElearningTLU.Dto.Response;

import com.example.ElearningTLU.Dto.Lop;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SemesterGroupResponse {

    private String semesterGroupId;
    private List<Lop> listClass= new ArrayList();

}
