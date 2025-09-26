package chat.platform.plus.trigger.http;

import chat.platform.plus.api.RAGService;
import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.domain.chat.model.entity.DeleteKnowledgeEntity;
import chat.platform.plus.domain.chat.model.entity.GitResEntity;
import chat.platform.plus.domain.chat.model.entity.RAGMessageEntity;
import chat.platform.plus.domain.chat.model.entity.UploadRAGEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageTypeEnum;
import chat.platform.plus.domain.chat.model.valobj.ResultConstant;
import chat.platform.plus.domain.chat.service.RAGChatService;
import chat.platform.plus.types.common.Constants;
import chat.platform.plus.types.enums.CommonEnum;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin
@SaCheckLogin
@RestController
@RequestMapping("/rag")
public class RAGController implements RAGService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RAGChatService ragChatService;

    @PostMapping("/upload/knowledge")
    @Override
    public Response<UpLoadRAGResDTO> uploadRAG(@ModelAttribute UpLoadRAGReqDTO upLoadRAGReqDTO) throws Exception {
        // 参数校验
        if ((upLoadRAGReqDTO.getFileList() == null || upLoadRAGReqDTO.getFileList().isEmpty()) ||
                StringUtils.isBlank(upLoadRAGReqDTO.getRagName()) || upLoadRAGReqDTO.getMessageType() == null) {
            return Response.<UpLoadRAGResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        if (!upLoadRAGReqDTO.getMessageType().equals(MessageTypeEnum.RAG.getType())) {
            return Response.<UpLoadRAGResDTO>builder()
                    .code(CommonEnum.ILLEGAL.getCode())
                    .info(CommonEnum.ILLEGAL.getInfo())
                    .build();
        }
        UploadRAGEntity uploadRAGEntity = ragChatService.uploadRAG(StpUtil.getLoginIdAsString(), upLoadRAGReqDTO.getFileList(), upLoadRAGReqDTO.getRagName());
        return Response.<UpLoadRAGResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(UpLoadRAGResDTO.builder()
                        .isSuccess(uploadRAGEntity.getIsSuccess())
                        .message(uploadRAGEntity.getMessage())
                        .ragName(upLoadRAGReqDTO.getRagName())
                        .count(uploadRAGEntity.getCount())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @GetMapping("/tag_list")
    @Override
    public Response<List<String>> getRAGTagList() {
        String key = StpUtil.getLoginIdAsString() + Constants.KEY_SUFFIX;
        RList<String> ragTagList = redissonClient.getList(key);
        return Response.<List<String>>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(new ArrayList<>(ragTagList))
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @PostMapping("/analyze/git")
    @Override
    public Response<GitResDTO> analyzeGitRepository(@RequestBody GitReqDTO gitReqDTO) throws Exception {
        // 参数校验
        if (StringUtils.isBlank(gitReqDTO.getRagName()) || StringUtils.isBlank(gitReqDTO.getRepoUrl())) {
            return Response.<GitResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        GitResEntity gitResEntity = ragChatService.analyzeGitRepository(StpUtil.getLoginIdAsString(), gitReqDTO.getRagName(), gitReqDTO.getRepoUrl());
        return Response.<GitResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(GitResDTO.builder()
                        .isSuccess(gitResEntity.getIsSuccess())
                        .message(gitResEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @PostMapping("/chat")
    @Override
    public ResponseBodyEmitter chat(@RequestBody RAGChatReqDTO ragChatReqDTO) throws Exception {
        ResponseBodyEmitter responseBodyEmitter = new ResponseBodyEmitter(10 * 60 * 1000L);
        // 参数校验
        if (ragChatReqDTO.getHistoryCode() == null || StringUtils.isBlank(ragChatReqDTO.getRagTag()) ||
                StringUtils.isBlank(ragChatReqDTO.getContent()) || ragChatReqDTO.getMessageType() == null) {
            return ragChatService.fail(ResultConstant.Fail_Lack_Param, responseBodyEmitter);
        }
        List<String> ragTagList = redissonClient.getList(StpUtil.getLoginIdAsString() + Constants.KEY_SUFFIX);
        if (!ragTagList.contains(ragChatReqDTO.getRagTag()) || !ragChatReqDTO.getMessageType().equals(MessageTypeEnum.RAG.getType())) {
            return ragChatService.fail(ResultConstant.Fail_Illegal_Param, responseBodyEmitter);
        }
        RAGMessageEntity ragMessageEntity = RAGMessageEntity.builder()
                .userId(StpUtil.getLoginIdAsString())
                .historyCode(ragChatReqDTO.getHistoryCode())
                .ragTag(ragChatReqDTO.getRagTag())
                .content(ragChatReqDTO.getContent())
                .build();
        return ragChatService.chat(ragMessageEntity, responseBodyEmitter);
    }

    @PostMapping("/delete/knowledge/{ragName}")
    @Override
    public Response<DeleteKnowledgeResDTO> deleteKnowledge(@PathVariable("ragName") String ragName) {
        if (StringUtils.isBlank(ragName)) {
            return Response.<DeleteKnowledgeResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        DeleteKnowledgeEntity deleteKnowledgeEntity = ragChatService.deleteKnowledge(StpUtil.getLoginIdAsString(), ragName);
        return Response.<DeleteKnowledgeResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(DeleteKnowledgeResDTO.builder()
                        .isSuccess(deleteKnowledgeEntity.getIsSuccess())
                        .message(deleteKnowledgeEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

}