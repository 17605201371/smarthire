package com.resume_ai_project.smarthire.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.resume_ai_project.smarthire.entity.Application;
import org.apache.ibatis.annotations.Mapper;

/**
 * 职位申请 Mapper
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
