package com.example.autoproject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_registration")
public class Registration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentName;
    private String studentId;
    @TableField
    private Long courseId;
    @TableField
    private Long studentKeyId;
    private LocalDateTime registerTime;
    private Integer rankNum;
    private Integer status; // 0=报名中,1=待考核,2=通过,3=未通过,4=递补
    private LocalDateTime examTime;
    private BigDecimal examScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
