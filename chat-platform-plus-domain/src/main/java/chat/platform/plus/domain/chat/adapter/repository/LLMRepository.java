package chat.platform.plus.domain.chat.adapter.repository;

import chat.platform.plus.domain.chat.model.entity.*;

import java.util.List;

public interface LLMRepository {

    /**
     * 根据用户ID获取校验信息
     * @param userId
     * @return
     */
    UserEntity getUserByUserId(String userId);

    /**
     * 判断服务是否降级
     * @return
     */
    boolean downgradeSwitch();

    /**
     * 判断用户ID是否在切量范围内
     * @return
     */
    boolean cutRange(String userId);

    /**
     * 上传文件
     * @param uploadFileEntity
     * @return
     */
    UpLoadFileResEntity uploadFile(UploadFileEntity uploadFileEntity);

    /**
     * 获取历史记录
     * @param userId
     * @param historyCode
     * @return
     */
    HistoryEntity getHistory(String userId, String historyCode);

    /**
     * 获取历史记录编码集合
     * @param userId
     * @return
     */
    List<HistoryCodeEntity> getHistoryCodeList(String userId);

    /**
     * 更新历史记录
     * @param historyEntity
     */
    void updateHistory(HistoryEntity historyEntity);
}
