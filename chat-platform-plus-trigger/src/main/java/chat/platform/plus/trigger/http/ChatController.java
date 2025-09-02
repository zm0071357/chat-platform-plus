package chat.platform.plus.trigger.http;

import chat.platform.plus.api.ChatService;
import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.domain.chat.model.entity.HistoryCodeEntity;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.model.entity.UpLoadFileResEntity;
import chat.platform.plus.domain.chat.model.entity.UploadFileEntity;
import chat.platform.plus.domain.chat.service.LLMService;
import chat.platform.plus.types.enums.CommonEnum;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@CrossOrigin
@SaCheckLogin
@RestController
@RequestMapping("/llm")
public class ChatController implements ChatService {

    @Resource
    private LLMService llmService;

    @Resource
    private ResponseBodyEmitter responseBodyEmitter;

    @PostMapping("/chat")
    @Override
    public ResponseBodyEmitter chat(@RequestBody ChatReqDTO chatReqDTO) throws Exception {
        // 参数校验
        if (StringUtils.isBlank(chatReqDTO.getHistoryCode()) || StringUtils.isBlank(chatReqDTO.getContent())
                || chatReqDTO.getIsSearch() == null || chatReqDTO.getMessageType() == null) {
            return responseBodyEmitter;
        }
        MessageEntity messageEntity = MessageEntity.builder()
                .userId(StpUtil.getLoginIdAsString())
                .historyCode(chatReqDTO.getHistoryCode())
                .content(chatReqDTO.getContent())
                .messageType(chatReqDTO.getMessageType())
                .isSearch(chatReqDTO.getIsSearch())
                .fileList(chatReqDTO.getUploadFile().getFileList())
                .fileListSize(chatReqDTO.getUploadFile().getFileListSize())
                .build();
        return llmService.chat(messageEntity, responseBodyEmitter);
    }

    @PostMapping("/upload_file")
    @Override
    public Response<UpLoadFileResDTO> uploadFile(@ModelAttribute UpLoadFileReqDTO upLoadFileReqDTO) throws Exception {
        // 参数校验
        if (upLoadFileReqDTO.getFile() == null || upLoadFileReqDTO.getMessageType() == null) {
            return Response.<UpLoadFileResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        UploadFileEntity uploadFileEntity = UploadFileEntity.builder()
                .userId(StpUtil.getLoginIdAsString())
                .file(upLoadFileReqDTO.getFile())
                .messageType(upLoadFileReqDTO.getMessageType())
                .build();
        UpLoadFileResEntity upLoadFileResEntity = llmService.uploadFile(uploadFileEntity);
        return Response.<UpLoadFileResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(UpLoadFileResDTO.builder()
                        .isSuccess(upLoadFileResEntity.getIsSuccess())
                        .message(upLoadFileResEntity.getMessage())
                        .userId(upLoadFileResEntity.getUserId())
                        .file(UpLoadFileResDTO.File.builder()
                                .url(upLoadFileResEntity.getUrl())
                                .size(upLoadFileResEntity.getSize())
                                .fileType(upLoadFileResEntity.getFileType())
                                .build())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @GetMapping("/history_list")
    @Override
    public Response<List<HistoryCodeResDTO>> getHistoryList() {
       List<HistoryCodeEntity> historyCodeEntityList = llmService.getHistoryCodeList(StpUtil.getLoginIdAsString());
       List<HistoryCodeResDTO> list = new ArrayList<>();
        for (HistoryCodeEntity historyCodeEntity : historyCodeEntityList) {
            list.add(HistoryCodeResDTO.builder()
                            .historyName(historyCodeEntity.getHistoryName())
                            .historyCode(historyCodeEntity.getHistoryCode())
                    .build());
        }
        return Response.<List<HistoryCodeResDTO>>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(list)
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @GetMapping("/history/{historyCode}")
    @Override
    public Response<List<ChatRequest.Input.Message>> getHistory(@PathVariable("historyCode") String historyCode) {
        // 参数校验
        if (StringUtils.isBlank(historyCode)) {
            return Response.<List<ChatRequest.Input.Message>>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        List<ChatRequest.Input.Message> history = llmService.getHistory(StpUtil.getLoginIdAsString(), historyCode);
        return Response.<List<ChatRequest.Input.Message>>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(history)
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }



}
