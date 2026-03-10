package com.resume_ai_project.smarthire.parser;

import com.resume_ai_project.smarthire.dto.ParseResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.parser.type", havingValue = "llm")
public class LLMResumeParser implements ResumeParser {
    @Override
    public ParseResult parse(String rawText) {
        // 调用大模型 API，如 OpenAI、本地模型等
        // 返回解析结果
        return new ParseResult();
    }
}