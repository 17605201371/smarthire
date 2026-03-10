package com.resume_ai_project.smarthire.parser;

import com.resume_ai_project.smarthire.dto.ParseResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MockResumeParserTest {

    @Autowired
    private MockResumeParser parser;

    @Test
    public void testExtractBasicInfo() {
        String resumeText = """
            张三
            13800138000
            zhangsan@example.com
            
            求职意向：Java 开发工程师
            
            专业技能：
            - 熟练掌握 Java、Spring Boot、MyBatis
            - 熟悉 MySQL、Redis
            - 了解 Vue.js、React
            
            教育经历：
            2018-09 ～ 2022-06    山东大学    计算机科学与技术    本科
            
            工作经历：
            2021-07 ～ 2021-12    XX 科技有限公司    Java 开发实习生
            参与后端服务开发，负责用户模块的 CRUD 和接口优化。
            """;

        ParseResult result = parser.parse(resumeText);

        assertNotNull(result, "解析结果不应为空");
        assertEquals("张三", result.getName(), "姓名应该正确提取");
        assertEquals("13800138000", result.getPhone(), "手机号应该正确提取");
        assertEquals("zhangsan@example.com", result.getEmail(), "邮箱应该正确提取");
        assertTrue(result.getSkills().size() > 0, "应该提取到技能");
        assertTrue(result.getEducationList().size() > 0, "应该提取到教育经历");
        assertTrue(result.getWorkExperienceList().size() > 0, "应该提取到工作经历");
    }

    @Test
    public void testExtractMultiplePhones() {
        String resumeText = """
            李四
            手机：13912345678
            备用电话：186-8888-9999
            email: lisi@gmail.com
            """;

        ParseResult result = parser.parse(resumeText);

        assertTrue(result.getPhone().equals("13912345678") || result.getPhone().equals("18688889999"),
                "应该能提取到手机号");
    }

    @Test
    public void testExtractSkills() {
        String resumeText = """
            专业技能
            1. 精通 Java 编程，熟悉 JVM 原理
            2. 熟练使用 Spring Boot、Spring Cloud 进行微服务开发
            3. 熟悉 MySQL、Oracle 数据库
            4. 掌握 Redis、MongoDB 等 NoSQL 技术
            5. 熟悉 Docker、Kubernetes 容器化部署
            """;

        ParseResult result = parser.parse(resumeText);

        assertTrue(result.getSkills().contains("Java"), "应该包含 Java 技能");
        assertTrue(result.getSkills().contains("Spring Boot"), "应该包含 Spring Boot 技能");
        assertTrue(result.getSkills().contains("MySQL"), "应该包含 MySQL 技能");
        assertTrue(result.getSkills().contains("Redis"), "应该包含 Redis 技能");
        assertTrue(result.getSkills().contains("Docker"), "应该包含 Docker 技能");
    }

    @Test
    public void testExtractEducation() {
        String resumeText = """
            教育背景
            
            2015-09 ～ 2019-06    北京大学    软件工程    本科
            主修课程：数据结构、算法、操作系统、计算机网络
            
            2019-09 ～ 2022-06    清华大学    计算机科学与技术    硕士
            研究方向：人工智能、机器学习
            """;

        ParseResult result = parser.parse(resumeText);

        assertTrue(result.getEducationList().size() >= 1, "应该至少提取到一条教育经历");
        
        // 验证第一条教育经历
        if (result.getEducationList().size() > 0) {
            ParseResult.Education edu = result.getEducationList().get(0);
            assertNotNull(edu.getSchool(), "学校名称不应为空");
        }
    }

    @Test
    public void testEmptyResume() {
        assertThrows(RuntimeException.class, () -> {
            parser.parse("");
        }, "空简历应该抛出异常");

        assertThrows(RuntimeException.class, () -> {
            parser.parse("   ");
        }, "纯空格简历应该抛出异常");
    }
}