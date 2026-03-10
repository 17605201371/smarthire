package com.resume_ai_project.smarthire.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.resume_ai_project.smarthire.entity.Position;
import org.apache.ibatis.annotations.Mapper;

/**
 * 职位 Mapper 接口
 */
@Mapper
public interface PositionMapper extends BaseMapper<Position> {
    // 自定义复杂查询可在这里添加方法，并用 @Select 或 XML 实现
}
