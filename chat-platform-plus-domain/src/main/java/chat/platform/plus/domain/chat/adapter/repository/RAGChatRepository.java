package chat.platform.plus.domain.chat.adapter.repository;

import chat.platform.plus.domain.chat.model.entity.DeleteKnowledgeEntity;
import chat.platform.plus.domain.chat.model.entity.GitResEntity;
import chat.platform.plus.domain.chat.model.entity.HistoryEntity;
import chat.platform.plus.domain.chat.model.entity.UploadRAGEntity;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.multipart.MultipartFile;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.io.IOException;
import java.util.List;

public interface RAGChatRepository {

    /**
     * 上传RAG知识库
     * @param userId
     * @param fileList
     * @param ragName
     * @return
     */
    UploadRAGEntity uploadRAG(String userId, List<MultipartFile> fileList, String ragName);

    /**
     * 解析Git代码库
     * @param ragName
     * @param repoUrl
     * @return
     */
    GitResEntity analyzeGitRepository(String userId, String ragName, String repoUrl) throws IOException, GitAPIException;

    /**
     * 删除知识库
     * @param userId
     * @param ragName
     * @return
     */
    DeleteKnowledgeEntity deleteKnowledge(String userId, String ragName);
}
