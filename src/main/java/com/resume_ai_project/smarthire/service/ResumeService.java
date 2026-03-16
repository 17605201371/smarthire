package com.resume_ai_project.smarthire.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume_ai_project.smarthire.dto.ParseResult;
import com.resume_ai_project.smarthire.entity.Resume;
import com.resume_ai_project.smarthire.entity.ResumeStatus;
import com.resume_ai_project.smarthire.entity.User;
import com.resume_ai_project.smarthire.mapper.ResumeMapper;
import com.resume_ai_project.smarthire.parser.ResumeParser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public class ResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ResumeParser resumeParser;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 上传并解析简历（不保存，仅返回解析结果）
     */
    @Transactional
    public ParseResult uploadAndParse(MultipartFile file, User user) throws Exception {
        logger.info("开始处理简历文件：{}", file.getOriginalFilename());
        
        // 1. 上传文件到 MinIO（临时存储）
        String fileUrl = fileStorageService.uploadFile(file);
        logger.info("文件上传成功，URL: {}", fileUrl);

        // 2. 提取文本
        String rawText = extractTextFromFile(file);
        logger.info("文件内容提取完成，文本长度：{} 字符", rawText != null ? rawText.length() : 0);
        if (rawText != null && !rawText.isEmpty()) {
            logger.info("提取的文本内容预览:\n{}", rawText.substring(0, Math.min(200, rawText.length())));
        } else {
            logger.error("提取的文本为空！");
        }

        // 3. 调用解析器
        ParseResult parseResult = resumeParser.parse(rawText);
        logger.info("简历解析成功：姓名={}, 电话={}, 邮箱={}", 
            parseResult.getName(), parseResult.getPhone(), parseResult.getEmail());

        // 4. 返回解析结果（不保存到数据库）
        return parseResult;
    }

    /**
     * 保存简历到数据库（用户确认后才保存）
     */
    @Transactional
    public Resume saveResume(User user, ParseResult parseResult, String fileUrl, String rawText, String fileName) throws Exception {
        logger.info("保存简历到数据库：姓名={}", parseResult.getName());
        
        // 创建简历实体
        Resume resume = new Resume();
        resume.setUserId(user.getId());
        resume.setFileName(fileName);
        resume.setFileUrl(fileUrl);
        resume.setRawText(rawText);
        resume.setStatus(ResumeStatus.SUCCESS);

        // 填充解析结果
        resume.setCandidateName(parseResult.getName());
        resume.setCandidatePhone(parseResult.getPhone());
        resume.setCandidateEmail(parseResult.getEmail());
        resume.setSkills(String.join(",", parseResult.getSkills()));
        resume.setEducationJson(objectMapper.writeValueAsString(parseResult.getEducationList()));
        resume.setWorkJson(objectMapper.writeValueAsString(parseResult.getWorkExperienceList()));
        resume.setExpectedJob(parseResult.getExpectedJob());

        // 保存
        resumeMapper.insert(resume);
        return resume;
    }

    /**
     * 从文件中提取文本（支持 PDF、Word）
     */
    private String extractTextFromFile(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        logger.info("开始从文件提取文本：{}", originalFilename);
        
        if (originalFilename == null) {
            throw new RuntimeException("文件名为空");
        }

        try (InputStream inputStream = file.getInputStream()) {
            if (originalFilename.toLowerCase().endsWith(".pdf")) {
                logger.info("检测到 PDF 文件，使用 PDFBox 解析");
                return extractTextFromPDF(inputStream);
            } else if (originalFilename.toLowerCase().endsWith(".docx") || 
                       originalFilename.toLowerCase().endsWith(".doc")) {
                logger.info("检测到 Word 文件，使用 POI 解析");
                return extractTextFromWord(inputStream);
            } else {
                throw new RuntimeException("不支持的文件格式：" + originalFilename);
            }
        }
    }

    /**
     * 从 PDF 文件中提取文本
     */
    private String extractTextFromPDF(InputStream inputStream) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document).trim();
        }
    }

    /**
     * 从 Word 文件中提取文本（增强版）
     */
    private String extractTextFromWord(InputStream inputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder text = new StringBuilder();
            
            // 1. 提取段落
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            logger.info("Word 文档段落数量：{}", paragraphs.size());
            
            for (XWPFParagraph paragraph : paragraphs) {
                // 尝试从 Run 中提取文本（更可靠）
                List<XWPFRun> runs = paragraph.getRuns();
                if (runs != null && !runs.isEmpty()) {
                    for (XWPFRun run : runs) {
                        String runText = run.getText(0);
                        if (runText != null && !runText.trim().isEmpty()) {
                            text.append(runText).append(" ");
                        }
                    }
                    text.append("\n");
                } else {
                    // 回退到直接获取段落文本
                    String paragraphText = paragraph.getText();
                    if (!paragraphText.trim().isEmpty()) {
                        text.append(paragraphText).append("\n");
                    }
                }
            }
            
            // 2. 提取表格中的文本
            List<XWPFTable> tables = document.getTables();
            if (tables != null && !tables.isEmpty()) {
                logger.info("Word 文档表格数量：{}", tables.size());
                tables.forEach(table -> {
                    table.getRows().forEach(row -> {
                        row.getTableCells().forEach(cell -> {
                            cell.getParagraphs().forEach(para -> {
                                String cellText = para.getText();
                                if (!cellText.trim().isEmpty()) {
                                    text.append(cellText).append(" ");
                                }
                            });
                        });
                    });
                    text.append("\n");
                });
            }
            
            String result = text.toString().trim();
            logger.info("Word 文档提取完成，总文本长度：{} 字符", result.length());
            return result;
        }
    }

    public Page<Resume> findByUser(User user, int pageNum, int pageSize) {
        Page<Resume> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resume::getUserId, user.getId())
                .orderByDesc(Resume::getCreateTime);
        return resumeMapper.selectPage(page, wrapper);
    }
    
    /**
     * 分页查询简历（根据简历 ID 列表）
     */
    public Page<Resume> findResumesByIds(List<Long> resumeIds, int pageNum, int pageSize) {
        Page<Resume> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Resume> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Resume::getId, resumeIds)
                .orderByDesc(Resume::getCreateTime);
        return resumeMapper.selectPage(page, wrapper);
    }

    public Resume getById(Long id) {
        return resumeMapper.selectById(id);
    }

    /**
     * 根据 ID 获取简历（供 Controller 调用）
     */
    public Resume getResumeById(Long id) {
        return getById(id);
    }

    /**
     * 获取所有简历
     */
    public List<Resume> findAll() {
        return resumeMapper.selectList(null);
    }

    @Transactional
    public void updateResume(Resume resume) {
        resumeMapper.updateById(resume);
    }
}