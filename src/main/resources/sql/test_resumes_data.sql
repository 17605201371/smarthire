-- 插入测试简历数据
-- 注意：需要先有求职者用户（CANDIDATE 角色）

-- 假设已有求职者用户 ID 为 1（如果不存在，请先注册一个 CANDIDATE 角色的用户）

INSERT INTO resumes (user_id, file_name, file_url, raw_text, candidate_name, candidate_phone, candidate_email, skills, education_json, work_json, expected_job, status, create_time) VALUES
(1, '张三_Java 开发工程师.pdf', 'resumes/zhangsan_resume.pdf', 
'张三
13800138000
zhangsan@example.com

求职意向：Java 开发工程师

专业技能：
- 熟练掌握 Java 编程语言，熟悉 JVM 原理
- 熟练使用 Spring Boot、Spring Cloud 框架
- 熟悉 MySQL、Oracle 数据库
- 掌握 Redis、MongoDB 等 NoSQL 技术
- 熟悉 Docker、Kubernetes 容器化部署

教育经历：
2018-09 ～ 2022-06    山东大学    计算机科学与技术    本科

工作经历：
2021-07 ～ 2021-12    XX 科技有限公司    Java 开发实习生
参与后端服务开发，负责用户模块的 CRUD 和接口优化。',
'张三', '13800138000', 'zhangsan@example.com', 
'Java,Spring Boot,MySQL,Redis,Docker,Kubernetes',
'[{"school":"山东大学","degree":"本科","startDate":"2018-09","endDate":"2022-06"}]',
'[{"company":"XX 科技有限公司","position":"Java 开发实习生","startDate":"2021-07","endDate":"2021-12","description":"参与后端服务开发，负责用户模块的 CRUD 和接口优化。"}]',
'Java 开发工程师', 'SUCCESS', NOW()),

(1, '李四_前端工程师.pdf', 'resumes/lisi_resume.pdf',
'李四
13912345678
lisi@gmail.com

求职意向：前端开发工程师

专业技能：
- 精通 HTML5、CSS3、JavaScript
- 熟练使用 Vue.js、React 框架
- 熟悉 TypeScript、Webpack
- 了解 Node.js、Express

教育经历：
2019-09 ～ 2023-06    北京大学    软件工程    本科',
'李四', '13912345678', 'lisi@gmail.com',
'HTML5,CSS3,JavaScript,Vue.js,React,TypeScript',
'[{"school":"北京大学","degree":"本科","startDate":"2019-09","endDate":"2023-06"}]',
'[]',
'前端开发工程师', 'SUCCESS', NOW()),

(1, '王五_产品经理.pdf', 'resumes/wangwu_resume.pdf',
'王五
18688889999
wangwu@163.com

求职意向：产品经理

专业技能：
- 3 年互联网产品经验
- 熟练使用 Axure、Sketch、XMind
- 擅长数据分析、用户研究
- 优秀的沟通协调能力

教育经历：
2016-09 ～ 2019-06    清华大学    MBA    硕士
2012-09 ～ 2016-06    复旦大学    工商管理    本科

工作经历：
2019-07 ～ 至今    某互联网公司    产品经理
负责电商平台产品规划，主导多个核心功能迭代。',
'王五', '18688889999', 'wangwu@163.com',
'产品设计，原型设计，数据分析，Axure，用户研究',
'[{"school":"清华大学","degree":"硕士","startDate":"2016-09","endDate":"2019-06"},{"school":"复旦大学","degree":"本科","startDate":"2012-09","endDate":"2016-06"}]',
'[{"company":"某互联网公司","position":"产品经理","startDate":"2019-07","endDate":"至今","description":"负责电商平台产品规划，主导多个核心功能迭代。"}]',
'产品经理', 'SUCCESS', NOW());

-- 查询插入的简历数据
SELECT * FROM resumes ORDER BY create_time DESC;
