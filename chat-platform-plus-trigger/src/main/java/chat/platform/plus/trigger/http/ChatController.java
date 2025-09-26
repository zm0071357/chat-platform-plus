package chat.platform.plus.trigger.http;

import chat.platform.plus.api.ChatService;
import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import chat.platform.plus.domain.chat.model.entity.*;
import chat.platform.plus.domain.chat.model.valobj.*;
import chat.platform.plus.domain.chat.service.LLMService;
import chat.platform.plus.domain.chat.service.create.img.CreateImgService;
import chat.platform.plus.domain.chat.service.create.vid.CreateVidService;
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
import java.util.Map;

@Slf4j
@CrossOrigin
@SaCheckLogin
@RestController
@RequestMapping("/llm")
public class ChatController implements ChatService {

    @Resource
    private LLMService llmService;

    @Resource
    private Map<String, CreateImgService> createImgServiceMap;

    @Resource
    private Map<String, CreateVidService> createVidServiceMap;

    @PostMapping("/chat")
    @Override
    public ResponseBodyEmitter chat(@RequestBody ChatReqDTO chatReqDTO) throws Exception {
        ResponseBodyEmitter responseBodyEmitter = new ResponseBodyEmitter(10 * 60 * 1000L);
        // 参数校验
        if (StringUtils.isBlank(chatReqDTO.getContent()) || chatReqDTO.getHistoryCode() == null ||
                chatReqDTO.getIsSearch() == null || chatReqDTO.getMessageType() == null) {
            return llmService.fail(ResultConstant.Fail_Lack_Param, responseBodyEmitter);
        }
        boolean fileList = false;
        boolean fileListSize = false;
        if (chatReqDTO.getUploadFile() != null) {
            fileList = (chatReqDTO.getUploadFile().getFileList() != null);
            fileListSize = (chatReqDTO.getUploadFile().getFileListSize() != null);
        }
        MessageEntity messageEntity = MessageEntity.builder()
                .userId(StpUtil.getLoginIdAsString())
                .historyCode(chatReqDTO.getHistoryCode())
                .content(chatReqDTO.getContent())
                .messageType(chatReqDTO.getMessageType())
                .isSearch(chatReqDTO.getIsSearch())
                .fileList(fileList ? chatReqDTO.getUploadFile().getFileList() : null)
                .fileListSize(fileListSize ? chatReqDTO.getUploadFile().getFileListSize() : null)
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

    @PostMapping("/create/img")
    @Override
    public Response<CreateResDTO> createImg(@RequestBody CreateImgReqDTO createImgReqDTO) throws Exception {
        // 参数校验
        if (StringUtils.isBlank(createImgReqDTO.getHistoryCode()) || StringUtils.isBlank(createImgReqDTO.getContent()) ||
                createImgReqDTO.getMessageType() == null || createImgReqDTO.getFunctionType() == null) {
            return Response.<CreateResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        String function = ImgFunctionEnum.getFunction(createImgReqDTO.getFunctionType());
        String size = null;
        if (createImgReqDTO.getSizeType() != null) {
            size = ImgSizeEnum.getSize(createImgReqDTO.getSizeType());
        }
        if (function == null || !createImgReqDTO.getMessageType().equals(MessageTypeEnum.CREATE_IMAGE.getType()) ||
                (function.equals(ImgFunctionEnum.CREATE_IMAGE.getFunction()) && size == null) ||
                (!function.equals(ImgFunctionEnum.CREATE_IMAGE.getFunction()) && size != null)) {
            return Response.<CreateResDTO>builder()
                    .code(CommonEnum.ILLEGAL.getCode())
                    .info(CommonEnum.ILLEGAL.getInfo())
                    .build();
        }
        // 根据功能获取对应服务
        CreateImgService createImgService = createImgServiceMap.get(function);
        CreateImgEntity createImgEntity = CreateImgEntity.builder()
                .userId(StpUtil.getLoginIdAsString())
                .historyCode(createImgReqDTO.getHistoryCode())
                .content(createImgReqDTO.getContent())
                .messageType(createImgReqDTO.getMessageType())
                .imgFunction(function)
                .size(size)
                .fileList(createImgReqDTO.getFileList())
                .fileListSize(createImgReqDTO.getFileListSize())
                .build();
        CreateEntity createEntity = createImgService.createImg(createImgEntity);
        return Response.<CreateResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(CreateResDTO.builder()
                        .userId(createImgEntity.getUserId())
                        .isSuccess(createEntity.getIsSuccess())
                        .type(createEntity.getType())
                        .url(createEntity.getUrl())
                        .message(createEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @PostMapping("/create/vid")
    @Override
    public Response<CreateResDTO> createVid(@RequestBody CreateVidReqDTO createVidReqDTO) throws Exception {
        // 参数校验
        if (StringUtils.isBlank(createVidReqDTO.getHistoryCode()) || StringUtils.isBlank(createVidReqDTO.getContent()) ||
                createVidReqDTO.getVidFunction() == null || createVidReqDTO.getMessageType() == null) {
            return Response.<CreateResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        String function = VidFunctionEnum.getFunction(createVidReqDTO.getVidFunction());
        if (function == null || !createVidReqDTO.getMessageType().equals(MessageTypeEnum.CREATE_VIDEO.getType())) {
            return Response.<CreateResDTO>builder()
                    .code(CommonEnum.ILLEGAL.getCode())
                    .info(CommonEnum.ILLEGAL.getInfo())
                    .build();
        }
        // 根据功能获取对应服务
        CreateVidService createVidService = createVidServiceMap.get(function);
        CreateVidEntity createVidEntity = CreateVidEntity.builder()
                .userId(StpUtil.getLoginIdAsString())
                .historyCode(createVidReqDTO.getHistoryCode())
                .content(createVidReqDTO.getContent())
                .messageType(createVidReqDTO.getMessageType())
                .fileList(createVidReqDTO.getFileList())
                .fileListSize(createVidReqDTO.getFileListSize())
                .vidFunction(function)
                .build();
        CreateEntity createEntity = createVidService.createVid(createVidEntity);
        return Response.<CreateResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(CreateResDTO.builder()
                        .userId(createVidEntity.getUserId())
                        .isSuccess(createEntity.getIsSuccess())
                        .type(createEntity.getType())
                        .url(createEntity.getUrl())
                        .message(createEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

    @PostMapping("/delete/history/{historyCode}")
    @Override
    public Response<DeleteHistoryResDTO> deleteHistory(@PathVariable("historyCode") String historyCode) {
        if (StringUtils.isBlank(historyCode)) {
            Response.<DeleteHistoryResDTO>builder()
                    .code(CommonEnum.LACK_PARAM.getCode())
                    .info(CommonEnum.LACK_PARAM.getInfo())
                    .build();
        }
        DeleteHistoryResEntity deleteHistoryResEntity = llmService.deleteHistory(StpUtil.getLoginIdAsString(), historyCode);
        return Response.<DeleteHistoryResDTO>builder()
                .code(CommonEnum.SUCCESS.getCode())
                .data(DeleteHistoryResDTO.builder()
                        .isSuccess(deleteHistoryResEntity.getIsSuccess())
                        .message(deleteHistoryResEntity.getMessage())
                        .build())
                .info(CommonEnum.SUCCESS.getInfo())
                .build();
    }

}
