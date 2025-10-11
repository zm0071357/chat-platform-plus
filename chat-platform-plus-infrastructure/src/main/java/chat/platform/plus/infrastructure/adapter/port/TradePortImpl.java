package chat.platform.plus.infrastructure.adapter.port;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.model.entity.LockOrderEntity;
import chat.platform.plus.infrastructure.gateway.GroupBuyMarketService;
import chat.platform.plus.infrastructure.gateway.dto.LockOrderRequestDTO;
import chat.platform.plus.infrastructure.gateway.dto.LockOrderResponseDTO;
import chat.platform.plus.infrastructure.gateway.dto.SettleOrderRequestDTO;
import chat.platform.plus.infrastructure.gateway.dto.SettleOrderResponseDTO;
import chat.platform.plus.infrastructure.gateway.response.Response;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.payments.nativepay.impl.NativePayServiceImpl;
import ltzf.payments.nativepay.model.order.OrderRequest;
import ltzf.payments.nativepay.model.order.OrderResponse;
import ltzf.payments.nativepay.model.prepay.PrepayRequest;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class TradePortImpl implements TradePort {

    @Resource
    private NativePayServiceImpl nativePayService;

    @Resource
    private GroupBuyMarketService groupBuyMarketService;

    @Value("${ltzf.sdk.config.mch_id}")
    private String mchId;

    @Value("${ltzf.sdk.config.notify_url}")
    private String ltzfNotifyUrl;

    @Value("${app.config.groupbuy-market-plus.notify_url}")
    private String groupBuyNotifyUrl;

    @Value("${app.config.groupbuy-market-plus.source}")
    private String source;

    @Value("${app.config.groupbuy-market-plus.channel}")
    private String channel;

    @Override
    public PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName, LockOrderEntity lockOrderEntity) throws IOException{
        // 封装请求
        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setMchId(mchId);
        prepayRequest.setOutTradeNo(orderId);
        prepayRequest.setTotalFee(lockOrderEntity != null ? lockOrderEntity.getPayPrice().toString() : orderPrice.toString());
        prepayRequest.setBody(goodsName);
        prepayRequest.setNotifyUrl(ltzfNotifyUrl);
        // 发起请求
        return nativePayService.prePay(prepayRequest);
    }

    @Override
    public OrderResponse getOrderResponse(String orderId) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setMchId(mchId);
        orderRequest.setOutTradeNo(orderId);
        OrderResponse orderResponse = nativePayService.getOrder(orderRequest);
        log.info("订单ID：{}，订单状态：{}", orderId, JSON.toJSONString(orderResponse));
        return orderResponse;
    }

    @Override
    public LockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId) {
        // 请求参数
        LockOrderRequestDTO requestDTO = new LockOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setTeamId(teamId);
        requestDTO.setGoodsId(goodsId);
        requestDTO.setActivityId(activityId);
        requestDTO.setSource(source);
        requestDTO.setChannel(channel);
        requestDTO.setOutTradeNo(orderId);
        requestDTO.setNotifyUrl(groupBuyNotifyUrl);
        try {
            log.info("请求拼团锁单接口开始：{}", JSON.toJSONString(requestDTO));
            Call<Response<LockOrderResponseDTO>> call = groupBuyMarketService.lockOrder(requestDTO);
            Response<LockOrderResponseDTO> response = call.execute().body();
            log.info("请求拼团锁单接口结果：{}", JSON.toJSONString(response));
            if (response == null) {
                return null;
            }
            if (!response.getCode().equals("0000")) {
                throw new RuntimeException(response.getInfo());
            }
            LockOrderResponseDTO lockOrderResponseDTO = response.getData();
            return LockOrderEntity.builder()
                    .originalPrice(lockOrderResponseDTO.getOriginalPrice())
                    .deductionPrice(lockOrderResponseDTO.getDeductionPrice())
                    .payPrice(lockOrderResponseDTO.getPayPrice())
                    .build();
        } catch (Exception e) {
            log.info("请求拼团锁单接口失败：{}", JSON.toJSONString(requestDTO), e);
            return null;
        }
    }

    @Override
    public void settleOrder(String userId, String orderId, Date orderPayTime) {
        // 请求参数
        SettleOrderRequestDTO requestDTO = new SettleOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setOutTradeNo(orderId);
        requestDTO.setOutTradeNoPayTime(orderPayTime);
        requestDTO.setSource(source);
        requestDTO.setChannel(channel);
        try {
            log.info("请求拼团结算接口开始：{}", JSON.toJSONString(requestDTO));
            Call<Response<SettleOrderResponseDTO>> call = groupBuyMarketService.settleOrder(requestDTO);
            Response<SettleOrderResponseDTO> response = call.execute().body();
            log.info("请求拼团结算接口结果：{}", JSON.toJSONString(response));
            if (response == null) {
                return;
            }
            if (!response.getCode().equals("0000")) {
                throw new RuntimeException(response.getInfo());
            }
        } catch (Exception e) {
            log.info("请求拼团结算接口失败：{}", JSON.toJSONString(requestDTO), e);
        }
    }


}

