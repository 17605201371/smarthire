package com.resume_ai_project.smarthire.parser;

import com.resume_ai_project.smarthire.dto.ParseResult;
import com.resume_ai_project.smarthire.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.regex.*;

@Component
@Primary // 当有多个 Parser 时，优先使用这个（模拟阶段）
@ConditionalOnProperty(name = "app.parser.type", havingValue = "mock", matchIfMissing = true)
public class MockResumeParser implements ResumeParser {
    private static final Logger logger = LoggerFactory.getLogger(MockResumeParser.class);

    // 正则表达式模式
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "(?:电话 | 手机 | 联系 | 手机号)[:：\\s]*([1-9]\\d{10})"
    );
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "(?:邮箱 | 邮件 |Email)[:：\\s]*([a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
    );
    
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "(?:姓名 | 名)[:：\\s]*([\\u4e00-\\u9fa5]{2,6})"
    );
    
    @Override
    public ParseResult parse(String rawText) {
        logger.info("开始解析简历文本，长度：{} 字符", rawText != null ? rawText.length() : 0);
        
        if (rawText == null || rawText.trim().isEmpty()) {
            logger.error("简历内容为空，无法解析");
            throw new RuntimeException("简历内容为空，可能是文件解析失败或文件格式不支持");
        }
        
        logger.info("简历文本预览：{}", rawText.substring(0, Math.min(100, rawText.length())));
        
        ParseResult result = new ParseResult();
        
        // 1. 提取姓名
        String name = extractName(rawText);
        result.setName(name);
        
        // 2. 提取手机号
        String phone = extractPhone(rawText);
        result.setPhone(phone);
        
        // 3. 提取邮箱
        String email = extractEmail(rawText);
        result.setEmail(email);
        
        // 4. 提取技能
        List<String> skills = extractSkills(rawText);
        result.setSkills(skills);
        
        // 5. 提取教育经历
        List<ParseResult.Education> educationList = extractEducation(rawText);
        result.setEducationList(educationList);
        
        // 6. 提取工作经历
        List<ParseResult.WorkExperience> workExperienceList = extractWorkExperience(rawText);
        result.setWorkExperienceList(workExperienceList);
        
        // 7. 提取期望职位
        String expectedJob = extractExpectedJob(rawText);
        result.setExpectedJob(expectedJob);
        
        logger.info("简历解析完成：姓名={}, 电话={}, 邮箱={}, 期望职位={}, 技能数={}, 教育经历数={}, 工作经历数={}", 
            result.getName(), result.getPhone(), result.getEmail(), result.getExpectedJob(),
            result.getSkills().size(), result.getEducationList().size(), result.getWorkExperienceList().size());
        
        return result;
    }
    
    /**
     * 提取姓名 - 增强版
     */
    private String extractName(String text) {
        logger.info("开始提取姓名");
        
        // 1. 尝试带标签的匹配 "姓名：张三"
        Matcher matcher = NAME_PATTERN.matcher(text);
        if (matcher.find()) {
            String name = matcher.group(1).trim();
            logger.info("从姓名标签提取到：{}", name);
            return name;
        }
        
        // 2. 尝试在"基本信息"部分查找
        Pattern basicInfoPattern = Pattern.compile(
            "(?:基本信息 | 个人资料)([^\\n]*(?:\\n(?!\\n)[^\\n]*)*)",
            Pattern.CASE_INSENSITIVE
        );
        matcher = basicInfoPattern.matcher(text);
        if (matcher.find()) {
            String basicInfo = matcher.group(1);
            // 在基本信息中查找姓名
            Matcher nameMatcher = Pattern.compile("姓名 [:：\\s]*([\\u4e00-\\u9fa5]{2,6})").matcher(basicInfo);
            if (nameMatcher.find()) {
                String name = nameMatcher.group(1).trim();
                logger.info("从基本信息部分提取到：{}", name);
                return name;
            }
        }
        
        // 3. 尝试第一行（简历通常第一行是姓名）
        String[] lines = text.split("\\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            // 如果第一行是 2-6 个中文字符，很可能是姓名
            if (firstLine.length() >= 2 && firstLine.length() <= 6 && 
                firstLine.matches("^[\\u4e00-\\u9fa5]+$")) {
                logger.info("从第一行提取到：{}", firstLine);
                return firstLine;
            }
            
            // 如果第一行包含姓名标签
            if (firstLine.contains("姓名")) {
                Matcher nameMatcher = Pattern.compile("姓名?[:：\\s]*([\\u4e00-\\u9fa5]{2,6})").matcher(firstLine);
                if (nameMatcher.find()) {
                    String name = nameMatcher.group(1).trim();
                    logger.info("从第一行标签提取到：{}", name);
                    return name;
                }
            }
        }
        
        logger.warn("未找到姓名，返回默认值");
        return "未知";
    }
    
    /**
     * 提取手机号 - 增强版
     */
    private String extractPhone(String text) {
        // 尝试带标签的匹配
        Matcher matcher = PHONE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // 尝试直接匹配手机号（11 位数字，可能包含 - 或空格）
        Pattern directPhonePattern = Pattern.compile("(1[3-9]\\d{9})");
        matcher = directPhonePattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return "";
    }
    
    /**
     * 提取邮箱 - 增强版
     */
    private String extractEmail(String text) {
        // 尝试带标签的匹配
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // 尝试直接匹配邮箱
        Pattern directEmailPattern = Pattern.compile("([a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        matcher = directEmailPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return "";
    }
    
    /**
     * 提取专业技能
     */
    private List<String> extractSkills(String text) {
        Set<String> skillSet = new HashSet<>();
        
        // 常见技术栈关键词
        String[] commonSkills = {
            "Java", "Python", "C++", "JavaScript", "TypeScript", "Go",
            "Spring", "Spring Boot", "Spring Cloud", "MyBatis", "Hibernate",
            "Vue", "React", "Angular", "Node.js",
            "MySQL", "Oracle", "PostgreSQL", "MongoDB", "Redis",
            "Linux", "Docker", "Kubernetes", "Jenkins", "Git",
            "HTML", "CSS", "Bootstrap", "ElementUI",
            "微服务", "分布式", "高并发", "负载均衡"
        };
        
        for (String skill : commonSkills) {
            if (text.contains(skill)) {
                skillSet.add(skill);
            }
        }
        
        // 尝试从"专业技能"、"掌握技能"等段落提取
        Pattern skillSectionPattern = Pattern.compile(
            "(?:专业技能 | 掌握技能 | 技术栈 | 熟悉)([^\\n]*(?:\\n(?!\\n)[^\\n]*)*)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = skillSectionPattern.matcher(text);
        if (matcher.find()) {
            String skillSection = matcher.group(1);
            // 按逗号、分号、顿号分割
            String[] items = skillSection.split("[,;,,]");
            for (String item : items) {
                String trimmed = item.trim();
                if (trimmed.length() > 1 && trimmed.length() < 50) {
                    skillSet.add(trimmed);
                }
            }
        }
        
        return new ArrayList<>(skillSet);
    }
    
    /**
     * 提取教育经历
     */
    private List<ParseResult.Education> extractEducation(String text) {
        List<ParseResult.Education> educationList = new ArrayList<>();
        
        // 尝试匹配教育经历段落
        Pattern eduPattern = Pattern.compile(
            "(?:教育经历 | 教育背景 | 学习经历)([^\\n]*(?:\\n(?!\\n)[^\\n]*)*)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = eduPattern.matcher(text);
        if (!matcher.find()) {
            // 如果没有找到明确的教育经历部分，尝试直接匹配学校信息
            extractEducationBySchool(text, educationList);
            return educationList;
        }
        
        String eduSection = matcher.group(1);
        
        // 尝试提取具体的教育信息（简化版）
        ParseResult.Education edu = new ParseResult.Education();
        
        // 提取学校
        Pattern schoolPattern = Pattern.compile("([\\u4e00-\\u9fa5]+(?:大学 | 学院 | 学校))");
        Matcher schoolMatcher = schoolPattern.matcher(eduSection);
        if (schoolMatcher.find()) {
            edu.setSchool(schoolMatcher.group(1));
        }
        
        // 提取学历
        if (eduSection.contains("博士")) {
            edu.setDegree("博士");
        } else if (eduSection.contains("硕士")) {
            edu.setDegree("硕士");
        } else if (eduSection.contains("本科") || eduSection.contains("学士")) {
            edu.setDegree("本科");
        } else if (eduSection.contains("大专")) {
            edu.setDegree("大专");
        }
        
        // 提取时间
        Pattern datePattern = Pattern.compile("(\\d{4}[-./]\\d{1,2})\\s*[-至～~]+\\s*(\\d{4}[-./]\\d{1,2}|至今)");
        Matcher dateMatcher = datePattern.matcher(eduSection);
        if (dateMatcher.find()) {
            edu.setStartDate(dateMatcher.group(1));
            edu.setEndDate(dateMatcher.group(2));
        }
        
        if (edu.getSchool() != null) {
            educationList.add(edu);
        }
        
        return educationList;
    }
    
    /**
     * 通过学校名称提取教育经历（备用方法）
     */
    private void extractEducationBySchool(String text, List<ParseResult.Education> educationList) {
        Pattern schoolPattern = Pattern.compile(
            "([\\u4e00-\\u9fa5]+(?:大学 | 学院))(?:[^\\n]*?(\\d{4}[-./]\\d{1,2}))?"
        );
        
        Matcher matcher = schoolPattern.matcher(text);
        while (matcher.find() && educationList.size() < 3) { // 最多提取 3 条
            ParseResult.Education edu = new ParseResult.Education();
            edu.setSchool(matcher.group(1));
            if (matcher.group(2) != null) {
                edu.setStartDate(matcher.group(2));
            }
            educationList.add(edu);
        }
    }
    
    /**
     * 提取工作经历
     */
    private List<ParseResult.WorkExperience> extractWorkExperience(String text) {
        List<ParseResult.WorkExperience> workList = new ArrayList<>();
        
        // 尝试匹配工作经历段落
        Pattern workPattern = Pattern.compile(
            "(?:工作经历 | 工作经验 | 实习经历 | 项目经历)([^\\n]*(?:\\n(?!\\n)[^\\n]*)*)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = workPattern.matcher(text);
        if (!matcher.find()) {
            return workList;
        }
        
        String workSection = matcher.group(1);
        
        // 创建工作经历对象
        ParseResult.WorkExperience work = new ParseResult.WorkExperience();
        
        // 提取公司名 - 增强匹配
        Pattern companyPattern = Pattern.compile(
            "(?:[\\d.-]+\\s*)?([\\u4e00-\\u9fa5]+(?:公司 | 科技 | 网络 | 有限 | 责任)[^\\n]{0,50})",
            Pattern.CASE_INSENSITIVE
        );
        Matcher companyMatcher = companyPattern.matcher(workSection);
        if (companyMatcher.find()) {
            String company = companyMatcher.group(1).trim();
            // 清理公司名，去掉职位描述
            if (company.contains("\n")) {
                company = company.split("\\n")[0].trim();
            }
            work.setCompany(company);
            logger.info("提取到公司：{}", company);
        }
        
        // 提取职位
        Pattern positionPattern = Pattern.compile(
            "(?:[\\u4e00-\\u9fa5]+(?:公司 | 科技 | 网络 | 有限 | 责任)[^\\n]{0,30})?([\\u4e00-\\u9fa5]+(?:工程师 | 开发 | 实习 | 经理 | 总监|主管))",
            Pattern.CASE_INSENSITIVE
        );
        Matcher positionMatcher = positionPattern.matcher(workSection);
        if (positionMatcher.find()) {
            work.setPosition(positionMatcher.group(1).trim());
        }
        
        // 提取描述
        work.setDescription(workSection.substring(0, Math.min(200, workSection.length())));
        
        // 提取时间
        Pattern datePattern = Pattern.compile("(\\d{4}[-./]\\d{1,2})\\s*[-至～~]+\\s*(\\d{4}[-./]\\d{1,2}|至今)");
        Matcher dateMatcher = datePattern.matcher(workSection);
        if (dateMatcher.find()) {
            work.setStartDate(dateMatcher.group(1));
            work.setEndDate(dateMatcher.group(2));
        }
        
        if (work.getCompany() != null || work.getPosition() != null) {
            workList.add(work);
        }
        
        return workList;
    }
    
    /**
     * 提取期望职位
     */
    private String extractExpectedJob(String text) {
        logger.info("开始提取期望职位");
        
        // 1. 尝试匹配"求职意向"、"期望职位"等
        Pattern jobPattern = Pattern.compile(
            "(?:求职意向 | 期望职位 | 期望工作 | 目标职位)[:：\\s]*([^\\n,;,.]+)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = jobPattern.matcher(text);
        if (matcher.find()) {
            String job = matcher.group(1).trim();
            // 清理多余的空白字符
            job = job.replaceAll("\\s+", " ");
            logger.info("从求职意向提取到：{}", job);
            return job;
        }
        
        // 2. 尝试从第一行或前几行提取（只取第一行）
        String[] lines = text.split("\\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            // 如果第一行包含职位关键词
            if (firstLine.contains("工程师") || firstLine.contains("开发") || 
                firstLine.contains("产品") || firstLine.contains("设计") ||
                firstLine.contains("运营") || firstLine.contains("测试")) {
                // 如果第一行包含邮箱或电话，说明不是纯职位
                if (!firstLine.contains("@") && !firstLine.matches(".*1[3-9]\\d{9}.*")) {
                    logger.info("从第一行提取到期望职位：{}", firstLine);
                    return firstLine;
                }
            }
        }
        
        // 3. 从工作经历中的职位推断（从文本中提取，而不是从 result 中）
        Pattern positionPattern = Pattern.compile(
            "(?:[\\u4e00-\\u9fa5]+(?:公司 | 科技 | 网络 | 有限 | 责任)[^\\n]{0,30})?([\\u4e00-\\u9fa5]+(?:工程师 | 开发 | 实习 | 经理 | 总监 | 主管))",
            Pattern.CASE_INSENSITIVE
        );
        Matcher positionMatcher = positionPattern.matcher(text);
        if (positionMatcher.find()) {
            String position = positionMatcher.group(1).trim();
            logger.info("从工作经历推断职位：{}", position);
            return position;
        }
        
        logger.warn("未找到期望职位");
        return null;
    }
}