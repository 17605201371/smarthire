package com.resume_ai_project.smarthire.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.resume_ai_project.smarthire.entity.Position;
import com.resume_ai_project.smarthire.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 职位服务类
 */
@Service
public class PositionService {

    @Autowired
    private PositionMapper positionMapper;
    
    @Autowired
    private ApplicationService applicationService;

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
     * 获取所有发布中的职位
     */
    public List<Position> findAllPublished() {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getStatus, "PUBLISHED")
                .orderByDesc(Position::getCreateTime);
        return positionMapper.selectList(wrapper);
    }

    /**
     * 删除职位
     */
    public void deletePosition(Long id) {
        positionMapper.deleteById(id);
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
     * 统计企业发布的职位数量
     */
    public long countByEmployer(Long employerId) {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getEmployerId, employerId);
        return positionMapper.selectCount(wrapper);
    }
    
    /**
     * 统计企业在招职位数量（只统计 PUBLISHED 状态）
     */
    public long countPublishedByEmployer(Long employerId) {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getEmployerId, employerId)
                .eq(Position::getStatus, "PUBLISHED");
        return positionMapper.selectCount(wrapper);
    }
    
    /**
     * 获取企业的所有职位（仅 ID）
     */
    public List<Position> findByEmployerId(Long employerId) {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getEmployerId, employerId)
                .select(Position::getId);
        return positionMapper.selectList(wrapper);
    }

    /**
     * 分页查询所有职位
     */
    public Page<Position> findAllPositions(int pageNum, int pageSize) {
        Page<Position> page = new Page<>(pageNum, pageSize);
        return positionMapper.selectPage(page, null);
    }

    /**
     * 分页查询发布中的职位
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
    
    /**
     * 为职位列表填充简历数量
     */
    public void fillResumeCount(List<Position> positions) {
        if (positions == null || positions.isEmpty()) {
            return;
        }
        
        // 获取所有职位的 ID
        List<Long> positionIds = positions.stream()
                .map(Position::getId)
                .toList();
        
        // 统计每个职位的申请数量
        List<Map<String, Object>> counts = applicationService.getBaseMapper().selectMaps(
                new LambdaQueryWrapper<com.resume_ai_project.smarthire.entity.Application>()
                        .in(com.resume_ai_project.smarthire.entity.Application::getPositionId, positionIds)
                        .groupBy(com.resume_ai_project.smarthire.entity.Application::getPositionId)
                        .select(com.resume_ai_project.smarthire.entity.Application::getPositionId)
        );
        
        // 转换为 Map<positionId, count>
        Map<Long, Long> countMap = counts.stream()
                .collect(Collectors.toMap(
                        m -> Long.valueOf(m.get("position_id").toString()),
                        m -> {
                            // 尝试不同的 key 名称来获取计数值
                            Object countValue = m.get("COUNT(position_id)");
                            if (countValue == null) {
                                countValue = m.get("count");
                            }
                            if (countValue == null) {
                                // 查找第一个数值类型的值
                                for (Object value : m.values()) {
                                    if (value instanceof Number) {
                                        countValue = value;
                                        break;
                                    }
                                }
                            }
                            return countValue != null ? Long.valueOf(countValue.toString()) : 0L;
                        }
                ));
        
        // 填充到职位对象
        positions.forEach(position -> {
            Long count = countMap.getOrDefault(position.getId(), 0L);
            position.setResumeCount(count);
        });
    }
}
