package chat.platform.plus.domain.trade.service.goods;

import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;

import java.util.List;


public interface GoodsService {

    /**
     * 获取商品集合
     * @return
     */
    List<GoodsEntity> getGoodsList();


    /**
     * 根据商品ID查询商品信息
     * @return
     */
    GoodsDetailEntity getGoodsByID(String goodsId);

}
