-- 插入测试职位数据
-- 注意：需要先有企业用户（EMPLOYER 角色）

-- 首先查询或创建一个测试企业用户 ID
-- 假设已有企业用户 ID 为 2（如果不存在，请先注册一个 EMPLOYER 角色的用户）

-- 插入 Java 开发职位
INSERT INTO positions (position_name, description, requirements, salary_range, location, experience_required, degree_required, employer_id, status, create_time) VALUES
('Java 开发工程师', '负责公司后端服务开发和维护，参与系统架构设计', 
 '{"skills": ["Java", "Spring Boot", "MySQL", "Redis"], "experience": "熟悉微服务架构"}',
 '15k-25k', '北京', '3-5 年', '本科', 2, 'PUBLISHED', NOW()),

('高级 Java 工程师', '负责核心业务模块开发，指导初级工程师', 
 '{"skills": ["Java", "Spring Cloud", "Dubbo", "Kafka"], "experience": "有高并发系统经验"}',
 '25k-40k', '北京', '5-10 年', '本科', 2, 'PUBLISHED', NOW()),

('前端开发工程师', '负责公司 Web 产品开发，优化用户体验', 
 '{"skills": ["Vue.js", "React", "TypeScript", "CSS"], "experience": "熟悉现代前端框架"}',
 '12k-20k', '上海', '1-3 年', '本科', 2, 'PUBLISHED', NOW()),

('产品经理', '负责产品规划和设计，推动产品迭代', 
 '{"skills": ["产品设计", "原型设计", "数据分析"], "experience": "有互联网产品经验"}',
 '18k-30k', '深圳', '3-5 年', '本科', 2, 'PUBLISHED', NOW()),

('测试工程师', '负责软件测试和质量保证', 
 '{"skills": ["测试用例", "自动化测试", "Selenium"], "experience": "熟悉测试流程"}',
 '10k-18k', '杭州', '1-3 年', '大专', 2, 'PUBLISHED', NOW()),

('运维工程师', '负责服务器维护和系统监控', 
 '{"skills": ["Linux", "Docker", "Kubernetes", "Jenkins"], "experience": "有云平台运维经验"}',
 '15k-25k', '广州', '3-5 年', '本科', 2, 'PUBLISHED', NOW()),

('数据分析师', '负责业务数据分析和报表制作', 
 '{"skills": ["SQL", "Python", "Tableau", "Excel"], "experience": "有数据分析项目经验"}',
 '12k-22k', '成都', '1-3 年', '本科', 2, 'PUBLISHED', NOW()),

('UI 设计师', '负责产品界面设计和视觉设计', 
 '{"skills": ["Photoshop", "Sketch", "Figma", "AI"], "experience": "有作品集"}',
 '10k-18k', '武汉', '1-3 年', '大专', 2, 'PUBLISHED', NOW());

-- 查询插入的职位数据
SELECT * FROM positions ORDER BY create_time DESC;
