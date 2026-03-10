-- SmartHire 数据库初始化脚本

-- 创建简历表
CREATE TABLE IF NOT EXISTS `resumes` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '简历 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_url` VARCHAR(500) NOT NULL COMMENT '文件 URL',
    `status` VARCHAR(50) NOT NULL COMMENT '简历状态',
    `raw_text` TEXT COMMENT '原始文本内容',
    `parsed_json` TEXT COMMENT '解析后的 JSON',
    `candidate_name` VARCHAR(100) COMMENT '候选人姓名',
    `candidate_phone` VARCHAR(20) COMMENT '候选人电话',
    `candidate_email` VARCHAR(255) COMMENT '候选人邮箱',
    `skills` VARCHAR(1000) COMMENT '专业技能，逗号分隔',
    `education_json` TEXT COMMENT '教育经历 JSON',
    `work_json` TEXT COMMENT '工作经历 JSON',
    `expected_job` VARCHAR(500) COMMENT '期望职位',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `email` VARCHAR(255) UNIQUE NOT NULL COMMENT '邮箱',
    `role` VARCHAR(50) NOT NULL DEFAULT 'CANDIDATE' COMMENT '角色',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
