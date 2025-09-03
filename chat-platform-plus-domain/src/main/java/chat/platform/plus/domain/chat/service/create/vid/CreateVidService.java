package chat.platform.plus.domain.chat.service.create.vid;

import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import chat.platform.plus.domain.chat.model.entity.CreateVidEntity;

public interface CreateVidService {

    /**
     * 视频创作
     * @param createVidEntity
     * @return
     */
    CreateEntity createVid(CreateVidEntity createVidEntity) throws Exception;

}
