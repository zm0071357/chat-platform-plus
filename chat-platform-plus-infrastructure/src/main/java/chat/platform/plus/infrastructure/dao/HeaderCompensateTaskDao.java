package chat.platform.plus.infrastructure.dao;

import chat.platform.plus.infrastructure.dao.po.HeaderCompensateTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HeaderCompensateTaskDao {

    /**
     * 新增团长退单补偿任务
     * @param headerCompensateTask
     */
    void insert(HeaderCompensateTask headerCompensateTask);

    /**
     * 获取未补偿完成的团长退单补偿任务集合
     * @return
     */
    List<HeaderCompensateTask> getUnCompleteHeaderRefundCompensateTaskList();

    /**
     * 更新补偿状态为已完成
     * @param headerCompensateTask
     * @return
     */
    Integer updateTaskStatusComplete(HeaderCompensateTask headerCompensateTask);
}
