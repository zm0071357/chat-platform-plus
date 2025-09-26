package chat.platform.plus.api;

import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;

public interface RAGService {

    /**
     * 上传知识库
     * @param upLoadRAGReqDTO
     * @return
     */
    Response<UpLoadRAGResDTO> uploadRAG(UpLoadRAGReqDTO upLoadRAGReqDTO) throws Exception;

    /**
     * 获取知识库标签列表
     * @return
     */
    Response<List<String>> getRAGTagList();

    /**
     * 解析Git仓库
     * @param gitReqDTO
     * @return
     */
    Response<GitResDTO> analyzeGitRepository(GitReqDTO gitReqDTO) throws Exception;

    /**
     * RAG对话
     * @param ragChatReqDTO
     * @return
     * @throws Exception
     */
    ResponseBodyEmitter chat(RAGChatReqDTO ragChatReqDTO) throws Exception;

    /**
     * 删除知识库
     * @param ragName
     * @return
     */
    Response<DeleteKnowledgeResDTO> deleteKnowledge(String ragName);

}
