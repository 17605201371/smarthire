-- 职位表
CREATE TABLE IF NOT EXISTS `positions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `position_name` VARCHAR(100) NOT NULL COMMENT '职位名称',
    `description` TEXT COMMENT '职位描述',
    `requirements` TEXT COMMENT '职位要求（JSON 格式）',
    `salary_range` VARCHAR(50) COMMENT '薪资范围',
    `location` VARCHAR(100) COMMENT '工作地点',
    `experience_required` VARCHAR(50) COMMENT '经验要求',
    `degree_required` VARCHAR(50) COMMENT '学历要求',
    `employer_id` BIGINT NOT NULL COMMENT '发布企业 ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态：PUBLISHED-发布中，CLOSED-已关闭',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_employer_id` (`employer_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_location` (`location`),
    INDEX `idx_degree_required` (`degree_required`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';
