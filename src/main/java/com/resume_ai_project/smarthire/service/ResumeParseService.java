package com.resume_ai_project.smarthire.service;

import com.resume_ai_project.smarthire.entity.Resume;
import com.resume_ai_project.smarthire.entity.ResumeStatus;
import com.resume_ai_project.smarthire.mapper.ResumeMapper;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ResumeParseService {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public void parseResume(Long resumeId, String fileUrl) {
        // 1. 查询简历
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new RuntimeException("简历不存在，ID: " + resumeId);
        }

        // 2. 更新状态为 PROCESSING
        resume.setStatus(ResumeStatus.PROCESSING);
        resumeMapper.updateById(resume);  // 注意：这里是更新，不是保存

        try {
            // 3. 从 fileUrl 下载文件（需实现从 MinIO 获取 InputStream）
            // 这里简化，先用模拟文本
            String extractedText = "模拟解析文本：姓名张三，电话13800138000，技能Java、Spring";

            // 4. 解析（后续替换为真实 AI）
            String parsedJson = "{\"name\":\"张三\",\"phone\":\"13800138000\",\"skills\":[\"Java\",\"Spring\"]}";

            // 5. 更新简历
            resume.setRawText(extractedText);
            resume.setParsedJson(parsedJson);
            resume.setStatus(ResumeStatus.SUCCESS);
            resumeMapper.updateById(resume);
        } catch (Exception e) {
            // 解析失败，更新状态为 FAILED
            resume.setStatus(ResumeStatus.FAILED);
            resumeMapper.updateById(resume);
            // 可记录错误日志，并重新抛出异常以便消息队列重试
            throw new RuntimeException("简历解析失败", e);
        }
    }
}