package com.example.autoproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String courseName;
    private String description;
    private String timePeriod;
    private Integer quota;
    private BigDecimal passScore;
    private String examDesc;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
