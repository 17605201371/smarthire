package com.resume_ai_project.smarthire.consumer;

import com.resume_ai_project.smarthire.config.RabbitMQConfig;
import com.resume_ai_project.smarthire.dto.ResumeParseMessage;
import com.resume_ai_project.smarthire.service.ResumeParseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumeParseConsumer {

    @Autowired
    private ResumeParseService resumeParseService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleResumeParse(ResumeParseMessage message) {
        resumeParseService.parseResume(message.getResumeId(), message.getFileUrl());
    }
}