package chat.platform.plus.domain.trade.service.goods;

import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.GoodsDetailEntity;
import chat.platform.plus.domain.trade.model.entity.GoodsEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private TradeRepository tradeRepository;

    @Override
    public List<GoodsEntity> getGoodsList() {
        return tradeRepository.getGoodsList();
    }

    @Override
    public GoodsDetailEntity getGoodsByID(String goodsId) {
        return tradeRepository.getGoodsByID(goodsId);
    }

}
