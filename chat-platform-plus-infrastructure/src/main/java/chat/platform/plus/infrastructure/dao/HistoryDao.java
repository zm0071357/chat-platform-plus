package chat.platform.plus.infrastructure.dao;

import chat.platform.plus.infrastructure.dao.po.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryDao {

    History getHistory(String userId, String historyCode);

    int insert(History history);

    void update(History history);

    List<String> getHistoryCodeList(String userId);

    void ragUpdate(History history);

    int delete(String userId, String historyCode);
}
