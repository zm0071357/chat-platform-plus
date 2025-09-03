package chat.platform.plus.trigger.http;

import chat.platform.plus.api.RAGService;
import chat.platform.plus.api.dto.UpLoadRAGReqDTO;
import chat.platform.plus.api.dto.UpLoadRAGResDTO;
import chat.platform.plus.api.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/rag")
public class RAGController implements RAGService {

    @PostMapping("/upload/knowledge")
    @Override
    public Response<UpLoadRAGResDTO> uploadRAG(@ModelAttribute UpLoadRAGReqDTO upLoadRAGReqDTO) {
        return null;
    }

}
