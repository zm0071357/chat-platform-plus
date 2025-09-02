package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.model.entity.*;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.model.valobj.ResultConstant;
import chat.platform.plus.domain.chat.model.valobj.RoleEnum;
import chat.platform.plus.infrastructure.dao.HistoryDao;
import chat.platform.plus.infrastructure.dao.UserDao;
import chat.platform.plus.infrastructure.dao.po.History;
import chat.platform.plus.infrastructure.dao.po.User;
import chat.platform.plus.infrastructure.dcc.DCCServiceImpl;
import chat.platform.plus.types.utils.AliOSSUtil;
import chat.platform.plus.types.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class LLMRepositoryImpl implements LLMRepository {

    @Resource
    private UserDao userDao;

    @Resource
    private DCCServiceImpl dccServiceImpl;

    @Resource
    private AliOSSUtil aliOSSUtil;

    @Resource
    private FileUtil fileUtil;

    @Resource
    private HistoryDao historyDao;

    @Override
    public UserEntity getUserByUserId(String userId) {
        User user = userDao.getUserByUserId(userId);
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .userId(user.getUserId())
                .isBlack(user.getIsBlack())
                .isVIP(user.getIsVip())
                .count(user.getCount())
                .build();
    }

    @Override
    public boolean downgradeSwitch() {
        return dccServiceImpl.isDowngradeSwitch();
    }

    @Override
    public boolean cutRange(String userId) {
        return dccServiceImpl.isCutRange(userId);
    }

    @Override
    public UpLoadFileResEntity uploadFile(UploadFileEntity uploadFileEntity) {
        try {
            String url = aliOSSUtil.upload(uploadFileEntity.getFile());
            Long size = uploadFileEntity.getFile().getSize();
            Integer fileType = fileUtil.getFileTypeByMult(uploadFileEntity.getFile());
            return UpLoadFileResEntity.builder()
                    .isSuccess(true)
                    .message(ResultConstant.Success_File)
                    .userId(uploadFileEntity.getUserId())
                    .url(url)
                    .size(size)
                    .fileType(fileType)
                    .build();
        } catch (IOException e) {
            return UpLoadFileResEntity.builder()
                    .isSuccess(false)
                    .message(ResultConstant.Fail_File)
                    .userId(uploadFileEntity.getUserId())
                    .build();
        }
    }

    @Override
    public HistoryEntity getHistory(String userId, String historyCode) {
        History history = historyDao.getHistory(userId, historyCode);
        if (history == null) {
            log.info("创建历史记录：{}", userId);
            List<ChatRequest.Input.Message> historyMessages = new ArrayList<>();
            List<ChatRequest.Input.Message> requestMessages = new ArrayList<>();
            // 系统默认消息
            List<ChatRequest.Input.Message.Content> systemContent = new ArrayList<>();
            systemContent.add(ChatRequest.Input.Message.Content.builder().text(MessageConstant.DEFAULT_MESSAGE).build());
            requestMessages.add(ChatRequest.Input.Message.builder()
                    .role(RoleEnum.SYSTEM.getRole())
                    .content(systemContent)
                    .build());
            historyDao.insert(History.builder()
                    .userId(userId)
                    .historyCode(historyCode)
                    .historyJson(JSON.toJSONString(historyMessages))
                    .requestJson(JSON.toJSONString(requestMessages))
                    .build());
            return HistoryEntity.builder()
                    .historyMessages(historyMessages)
                    .requestMessages(requestMessages)
                    .build();
        }
        log.info("获取历史记录：{}", userId);
        return HistoryEntity.builder()
                .historyMessages(JSON.parseArray(history.getHistoryJson(), ChatRequest.Input.Message.class))
                .requestMessages(JSON.parseArray(history.getRequestJson(), ChatRequest.Input.Message.class))
                .build();
    }

    @Override
    public List<HistoryCodeEntity> getHistoryCodeList(String userId) {
        List<String> historyCodeList = historyDao.getHistoryCodeList(userId);
        List<HistoryCodeEntity> list = new ArrayList<>();
        for (String historyCode : historyCodeList) {
            int count = 1;
            list.add(HistoryCodeEntity.builder()
                            .historyName("历史记录" + count++)
                            .historyCode(historyCode)
                    .build());
        }
        return list;
    }
}
