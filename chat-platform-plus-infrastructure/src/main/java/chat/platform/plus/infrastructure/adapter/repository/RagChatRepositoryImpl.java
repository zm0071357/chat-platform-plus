package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.chat.adapter.repository.RAGChatRepository;
import chat.platform.plus.domain.chat.model.entity.DeleteKnowledgeEntity;
import chat.platform.plus.domain.chat.model.entity.GitResEntity;
import chat.platform.plus.domain.chat.model.entity.HistoryEntity;
import chat.platform.plus.domain.chat.model.entity.UploadRAGEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.model.valobj.RoleEnum;
import chat.platform.plus.infrastructure.dao.HistoryDao;
import chat.platform.plus.infrastructure.dao.po.History;
import chat.platform.plus.types.common.Constants;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class RagChatRepositoryImpl implements RAGChatRepository {

    @Resource
    private TokenTextSplitter tokenTextSplitter;

    @Resource
    private PgVectorStore pgVectorStore;

    @Resource
    private RedissonClient redissonClient;

    @Value("${github.username}")
    private String userName;

    @Value("${github.token}")
    private String token;

    @Override
    public UploadRAGEntity uploadRAG(String userId, List<MultipartFile> fileList, String ragName) {
        log.info("上传知识库:{}", ragName);
        Integer count = 0;
        for (MultipartFile file : fileList) {
            try {
                log.info("上传知识库开始:{},{}", ragName, file.getOriginalFilename());
                TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
                List<Document> documents = documentReader.get();
                List<Document> documentSplitterList = tokenTextSplitter.apply(documents);
                // 添加知识库标签
                documents.forEach(doc -> doc.getMetadata().put("knowledge", ragName));
                documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", ragName));
                pgVectorStore.accept(documentSplitterList);
                // 添加知识库到 Redis
                RList<String> elements = redissonClient.getList(userId + Constants.KEY_SUFFIX);
                if (!elements.contains(ragName)) {
                    elements.add(ragName);
                }
                count ++;
                log.info("上传知识库完成:{},{}", ragName, file.getOriginalFilename());
            } catch (Exception e) {
                log.info("上传知识库失败:{},{},{}", ragName, file.getOriginalFilename(), e.getMessage());
            }
        }
        log.info("上传知识库完成:{}", ragName);
        return UploadRAGEntity.builder()
                .isSuccess(true)
                .message(MessageConstant.Success)
                .count(count)
                .build();
    }

    @Override
    public GitResEntity analyzeGitRepository(String userId, String ragName, String repoUrl) throws IOException, GitAPIException {
        // 使用系统临时目录避免权限问题
        String localPath = System.getProperty("java.io.tmpdir") + "/clone-repo-" + UUID.randomUUID();
        File localDir = new File(localPath);

        try {
            log.info("知识库标签: {}", ragName);
            log.info("克隆路径: {}", localDir.getAbsolutePath());

            // 确保目录存在
            FileUtils.forceMkdir(localDir);

            // 使用try-with-resources确保Git资源正确关闭
            try (Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(localDir)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, token))
                    .call()) {

                Files.walkFileTree(Paths.get(localPath), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        try {
                            log.info("文件路径:{}", file.toString());
                            PathResource resource = new PathResource(file);
                            TikaDocumentReader reader = new TikaDocumentReader(resource);
                            List<Document> documents = reader.get();

                            if (documents == null || documents.isEmpty()) {
                                log.warn("文件内容为空，跳过处理: {}", file);
                                return FileVisitResult.CONTINUE;
                            }

                            List<Document> documentSplitterList = tokenTextSplitter.apply(documents);
                            documents.forEach(doc -> doc.getMetadata().put("knowledge", ragName));
                            documentSplitterList.forEach(doc -> doc.getMetadata().put("knowledge", ragName));
                            pgVectorStore.accept(documentSplitterList);

                        } catch (IllegalArgumentException e) {
                            log.error("内容异常文件: {} | 错误信息: {}", file, e.getMessage());
                        } catch (Exception e) {
                            log.error("处理文件失败: {} | 错误类型: {}", file, e.getClass().getSimpleName());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            // 更新用户知识库列表
            RList<String> elements = redissonClient.getList(userId + Constants.KEY_SUFFIX);
            if (!elements.contains(ragName)) {
                elements.add(ragName);
            }

            log.info("遍历解析路径上传知识库完成：{}", ragName);
            return GitResEntity.builder()
                    .isSuccess(true)
                    .message(MessageConstant.Success)
                    .build();

        } finally {
            // 确保最终删除临时目录（增加重试机制）
            deleteDirectoryWithRetry(localDir);
        }
    }

    /**
     * 删除目录
     * @param directory
     */
    private void deleteDirectoryWithRetry(File directory) {
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                FileUtils.deleteDirectory(directory);
                log.info("成功删除目录: {}", directory.getAbsolutePath());
                return;
            } catch (IOException e) {
                retryCount++;
                log.warn("删除目录失败(尝试 {} / {}), 原因: {}", retryCount, 3, e.getMessage());
                if (retryCount < 3) {
                    try {
                        Thread.sleep(1000L * retryCount); // 等待时间递增
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("无法删除目录: {}", directory.getAbsolutePath(), e);
                }
            }
        }
    }

    @Override
    public DeleteKnowledgeEntity deleteKnowledge(String userId, String ragName) {
        RList<Object> list = redissonClient.getList(userId + Constants.KEY_SUFFIX);
        if (list == null || list.isEmpty()) {
            return DeleteKnowledgeEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_KnowledgeList_NoExist)
                    .build();
        }
        try {
            log.info("删除知识库开始: {}", ragName);
            List<Document> documents = pgVectorStore.similaritySearch(SearchRequest.query("delete")
                    .withFilterExpression("knowledge == '" + ragName + "'"));
            // 提取文档ID
            List<String> documentIds = documents.stream()
                    .map(Document::getId)
                    .collect(Collectors.toList());
            // 批量删除文档
            if (!documentIds.isEmpty()) {
                pgVectorStore.delete(documentIds);
            }
            // 删除知识库标签
            list.remove(ragName);
            log.info("删除知识库完成: {}", ragName);
            return DeleteKnowledgeEntity.builder()
                    .isSuccess(true)
                    .message(MessageConstant.Success)
                    .build();
        } catch (Exception e) {
            log.error("删除知识库失败: {}", ragName, e);
            return DeleteKnowledgeEntity.builder()
                    .isSuccess(false)
                    .message("删除失败: " + e.getMessage())
                    .build();
        }
    }

}
