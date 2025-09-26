package chat.platform.plus.domain.chat.service;

import chat.platform.plus.domain.chat.model.entity.DeleteKnowledgeEntity;
import chat.platform.plus.domain.chat.model.entity.GitResEntity;
import chat.platform.plus.domain.chat.model.entity.RAGMessageEntity;
import chat.platform.plus.domain.chat.model.entity.UploadRAGEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

public interface RAGChatService {

    /**
     * 上传知识库
     * @param userId
     * @param fileList
     * @param ragName
     * @return
     */
    UploadRAGEntity uploadRAG(String userId, List<MultipartFile> fileList, String ragName) throws Exception;

    /**
     * 解析Git代码库
     * @param ragName
     * @param repoUrl
     * @return
     */
    GitResEntity analyzeGitRepository(String userId, String ragName, String repoUrl) throws Exception;

    /**
     * RAG对话
     * @param ragMessageEntity
     * @return
     */
    ResponseBodyEmitter chat(RAGMessageEntity ragMessageEntity, ResponseBodyEmitter responseBodyEmitter) throws Exception;

    /**
     * 返回失败信息
     * @param result
     * @return
     */
    ResponseBodyEmitter fail(String result, ResponseBodyEmitter responseBodyEmitter);

    /**
     * 删除知识库
     * @param userId
     * @param ragName
     * @return
     */
    DeleteKnowledgeEntity deleteKnowledge(String userId, String ragName);
}
