package chat.platform.plus.api;

import chat.platform.plus.api.dto.UpLoadRAGReqDTO;
import chat.platform.plus.api.dto.UpLoadRAGResDTO;
import chat.platform.plus.api.response.Response;

public interface RAGService {

    /**
     * 上传RAG知识库
     * @param upLoadRAGReqDTO
     * @return
     */
    Response<UpLoadRAGResDTO> uploadRAG(UpLoadRAGReqDTO upLoadRAGReqDTO);
}
