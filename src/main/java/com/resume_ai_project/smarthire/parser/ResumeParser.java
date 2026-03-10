package com.resume_ai_project.smarthire.parser;

import com.resume_ai_project.smarthire.dto.ParseResult;

/**
 * 简历解析器接口
 */
public interface ResumeParser {
    /**
     * 解析简历文本
     * @param rawText 从文件中提取的原始文本
     * @return 解析后的结构化数据
     */
    ParseResult parse(String rawText);
}