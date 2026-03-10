package com.resume_ai_project.smarthire.service;

import com.resume_ai_project.smarthire.dto.ResumeParseMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "resume.exchange";
    private static final String ROUTING_KEY = "resume.parse";

    public void sendResumeParseMessage(Long resumeId, String fileUrl) {
        // 可以封装成一个简单的消息对象
        ResumeParseMessage message = new ResumeParseMessage(resumeId, fileUrl);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
    }
}