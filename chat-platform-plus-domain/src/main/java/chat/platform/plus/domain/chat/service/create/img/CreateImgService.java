package chat.platform.plus.domain.chat.service.create.img;

import chat.platform.plus.domain.chat.model.entity.CreateEntity;
import chat.platform.plus.domain.chat.model.entity.CreateImgEntity;

public interface CreateImgService {

    /**
     * 图片创作
     * @param createImgEntity
     * @return
     */
    CreateEntity createImg(CreateImgEntity createImgEntity) throws Exception;


}
