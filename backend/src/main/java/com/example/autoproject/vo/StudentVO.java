package com.example.autoproject.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentVO {
    private Long id;
    private String studentName;
    private String phone;
    private String grade;
    private String school;
    private Integer status;
}
