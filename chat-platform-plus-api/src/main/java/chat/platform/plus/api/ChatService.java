package chat.platform.plus.api;

import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

public interface ChatService {

     /**
      * 流式对话
      * @param chatReqDTO
      * @return
      * @throws Exception
      */
     ResponseBodyEmitter chat(ChatReqDTO chatReqDTO) throws Exception;

     /**
      * 上传文件
      * @param upLoadFileReqDTO
      * @return
      * @throws Exception
      */
     Response<UpLoadFileResDTO> uploadFile(UpLoadFileReqDTO upLoadFileReqDTO) throws Exception;

     /**
      * 获取历史记录编码集合
      * @return
      */
     Response<List<HistoryCodeResDTO>> getHistoryList();

     /**
      * 根据历史记录编码获取历史记录
      * @param historyCode
      * @return
      */
     Response<List<ChatRequest.Input.Message>> getHistory(String historyCode);

     /**
      * 图片创作
      * @param createImgReqDTO
      * @return
      */
     Response<CreateResDTO> createImg(CreateImgReqDTO createImgReqDTO) throws Exception;

     /**
      * 视频创作
      * @param createVidReqDTO
      * @return
      */
     Response<CreateResDTO> createVid(CreateVidReqDTO createVidReqDTO) throws Exception;

     /**
      * 删除历史记录
      * @param historyCode
      * @return
      */
     Response<DeleteHistoryResDTO> deleteHistory(String historyCode);

}
