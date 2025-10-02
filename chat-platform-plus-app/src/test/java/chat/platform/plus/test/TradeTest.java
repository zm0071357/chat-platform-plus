package chat.platform.plus.test;

import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;
import chat.platform.plus.domain.trade.service.goods.GoodsService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class TradeTest {

    @Resource
    private GoodsService tradeService;

    @Test
    public void test_goods() {
        List<GoodsEntity> goodsEntityListList = tradeService.getGoodsList();
        for (GoodsEntity goodsEntity : goodsEntityListList) {
            GoodsDetailEntity goodsDetailEntity = tradeService.getGoodsByID(goodsEntity.getGoodsId());
            log.info("goodDetailEntity:{}", JSON.toJSONString(goodsDetailEntity));
        }
    }

}
