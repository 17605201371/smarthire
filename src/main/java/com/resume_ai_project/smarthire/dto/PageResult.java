package com.resume_ai_project.smarthire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private long total;       // 总记录数
    private List<T> records;  // 当前页数据
}