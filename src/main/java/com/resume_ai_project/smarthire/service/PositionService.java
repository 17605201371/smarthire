package com.resume_ai_project.smarthire.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.resume_ai_project.smarthire.entity.Position;
import com.resume_ai_project.smarthire.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 职位服务类
 */
@Service
public class PositionService {

    @Autowired
    private PositionMapper positionMapper;

    /**
     * 保存职位
     */
    public Position savePosition(Position position) {
        positionMapper.insert(position);
        return position;
    }

    /**
     * 根据 ID 查询职位
     */
    public Position getPositionById(Long id) {
        return positionMapper.selectById(id);
    }

    /**
     * 更新职位
     */
    public Position updatePosition(Position position) {
        positionMapper.updateById(position);
        return position;
    }

    /**
     * 删除职位
     */
    public void deletePosition(Long id) {
        positionMapper.deleteById(id);
    }

    /**
     * 分页查询所有职位
     */
    public Page<Position> findAllPositions(int pageNum, int pageSize) {
        Page<Position> page = new Page<>(pageNum, pageSize);
        return positionMapper.selectPage(page, null);
    }

    /**
     * 分页查询企业发布的职位
     */
    public Page<Position> findByEmployer(Long employerId, int pageNum, int pageSize) {
        Page<Position> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getEmployerId, employerId)
                .orderByDesc(Position::getCreateTime);
        return positionMapper.selectPage(page, wrapper);
    }

    /**
     * 查询发布中的职位
     */
    public Page<Position> findPublishedPositions(int pageNum, int pageSize) {
        Page<Position> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getStatus, "PUBLISHED")
                .orderByDesc(Position::getCreateTime);
        return positionMapper.selectPage(page, wrapper);
    }

    /**
     * 根据条件搜索职位
     */
    public Page<Position> searchPositions(String keyword, String location, String degree, int pageNum, int pageSize) {
        Page<Position> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Position::getPositionName, keyword)
                    .or().like(Position::getDescription, keyword));
        }
        
        if (location != null && !location.isEmpty()) {
            wrapper.like(Position::getLocation, location);
        }
        
        if (degree != null && !degree.isEmpty()) {
            wrapper.eq(Position::getDegreeRequired, degree);
        }
        
        wrapper.eq(Position::getStatus, "PUBLISHED")
                .orderByDesc(Position::getCreateTime);
        
        return positionMapper.selectPage(page, wrapper);
    }
}
