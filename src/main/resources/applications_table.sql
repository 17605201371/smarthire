-- 职位申请表
CREATE TABLE IF NOT EXISTS `applications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请 ID',
    `position_id` BIGINT NOT NULL COMMENT '职位 ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历 ID',
    `user_id` BIGINT NOT NULL COMMENT '申请人 ID',
    `cover_letter` TEXT COMMENT '自我介绍',
    `expected_salary` VARCHAR(50) COMMENT '期望薪资',
    `available_date` DATE COMMENT '到岗时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'APPLIED' COMMENT '申请状态：APPLIED-已申请，VIEWED-已查看，INTERVIEW-面试中，REJECTED-已拒绝',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_position_id` (`position_id`),
    INDEX `idx_resume_id` (`resume_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位申请表';
