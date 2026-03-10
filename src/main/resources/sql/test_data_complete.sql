-- SmartHire 测试数据完整脚本
-- 执行顺序：先创建表 → 插入用户 → 插入职位 → 插入简历

-- ========================================
-- 1. 创建测试用户（如果不存在）
-- ========================================

-- 创建求职者用户（密码：123456，已加密）
INSERT INTO users (username, phone, password, role, create_time) 
SELECT 'zhangsan', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ.uIeLwwmKXqQnFkVN7EYqZj5sGaC.', 'CANDIDATE', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'zhangsan');

-- 创建企业用户（密码：123456，已加密）
INSERT INTO users (username, phone, password, role, create_time) 
SELECT 'employer1', '13900139000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ.uIeLwwmKXqQnFkVN7EYqZj5sGaC.', 'EMPLOYER', NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

-- ========================================
-- 2. 插入职位数据（使用企业用户 ID）
-- ========================================

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT 'Java 开发工程师' AS position_name, 
           '负责公司后端服务开发和维护，参与系统架构设计' AS description, 
           '{"skills": ["Java", "Spring Boot", "MySQL", "Redis"], "experience": "熟悉微服务架构"}' AS requirements,
           '15k-25k' AS salary_range, 
           '北京' AS location, 
           '3-5 年' AS experience_required, 
           '本科' AS degree_required, 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1) AS employer_id, 
           'PUBLISHED' AS status, 
           NOW() AS create_time
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT '高级 Java 工程师', 
           '负责核心业务模块开发，指导初级工程师', 
           '{"skills": ["Java", "Spring Cloud", "Dubbo", "Kafka"], "experience": "有高并发系统经验"}',
           '25k-40k', '北京', '5-10 年', '本科', 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1), 
           'PUBLISHED', NOW()
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT '前端开发工程师', 
           '负责公司 Web 产品开发，优化用户体验', 
           '{"skills": ["Vue.js", "React", "TypeScript", "CSS"], "experience": "熟悉现代前端框架"}',
           '12k-20k', '上海', '1-3 年', '本科', 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1), 
           'PUBLISHED', NOW()
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT '产品经理', 
           '负责产品规划和设计，推动产品迭代', 
           '{"skills": ["产品设计", "原型设计", "数据分析"], "experience": "有互联网产品经验"}',
           '18k-30k', '深圳', '3-5 年', '本科', 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1), 
           'PUBLISHED', NOW()
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT '测试工程师', 
           '负责软件测试和质量保证', 
           '{"skills": ["测试用例", "自动化测试", "Selenium"], "experience": "熟悉测试流程"}',
           '10k-18k', '杭州', '1-3 年', '大专', 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1), 
           'PUBLISHED', NOW()
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT '运维工程师', 
           '负责服务器维护和系统监控', 
           '{"skills": ["Linux", "Docker", "Kubernetes", "Jenkins"], "experience": "有云平台运维经验"}',
           '15k-25k', '广州', '3-5 年', '本科', 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1), 
           'PUBLISHED', NOW()
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT '数据分析师', 
           '负责业务数据分析和报表制作', 
           '{"skills": ["SQL", "Python", "Tableau", "Excel"], "experience": "有数据分析项目经验"}',
           '12k-22k', '成都', '1-3 年', '本科', 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1), 
           'PUBLISHED', NOW()
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) 
SELECT * FROM (
    SELECT 'UI 设计师', 
           '负责产品界面设计和视觉设计', 
           '{"skills": ["Photoshop", "Sketch", "Figma", "AI"], "experience": "有作品集"}',
           '10k-18k', '武汉', '1-3 年', '大专', 
           (SELECT id FROM users WHERE username = 'employer1' LIMIT 1), 
           'PUBLISHED', NOW()
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'employer1');

-- ========================================
-- 3. 插入简历数据（使用求职者用户 ID）
-- ========================================

INSERT INTO resumes (user_id, file_name, file_url, raw_text, candidate_name, candidate_phone, candidate_email, skills, education_json, work_json, expected_job, status, create_time)
SELECT * FROM (
    SELECT 
        (SELECT id FROM users WHERE username = 'zhangsan' LIMIT 1) AS user_id,
        '张三_Java 开发工程师.pdf' AS file_name,
        'resumes/zhangsan_resume.pdf' AS file_url,
        '张三\n13800138000\nzhangsan@example.com\n\n求职意向：Java 开发工程师\n\n专业技能：熟练掌握 Java、Spring Boot、MySQL\n\n教育经历：山东大学 计算机科学与技术 本科' AS raw_text,
        '张三' AS candidate_name,
        '13800138000' AS candidate_phone,
        'zhangsan@example.com' AS candidate_email,
        'Java,Spring Boot,MySQL,Redis' AS skills,
        '[{"school":"山东大学","degree":"本科","startDate":"2018-09","endDate":"2022-06"}]' AS education_json,
        '[]' AS work_json,
        'Java 开发工程师' AS expected_job,
        'SUCCESS' AS status,
        NOW() AS create_time
) AS temp
WHERE EXISTS (SELECT 1 FROM users WHERE username = 'zhangsan');

-- ========================================
-- 4. 查询验证
-- ========================================

-- 查询所有用户
SELECT '=== 用户列表 ===' AS '';
SELECT id, username, phone, role, create_time FROM users ORDER BY create_time;

-- 查询所有职位
SELECT '=== 职位列表 ===' AS '';
SELECT id, position_name, salary_range, location, experience_required, degree_required, status FROM positions ORDER BY create_time DESC;

-- 查询所有简历
SELECT '=== 简历列表 ===' AS '';
SELECT id, candidate_name, candidate_phone, expected_job, skills, status FROM resumes ORDER BY create_time DESC;
