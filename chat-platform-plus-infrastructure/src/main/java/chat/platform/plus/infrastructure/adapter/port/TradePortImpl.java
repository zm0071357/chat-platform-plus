package chat.platform.plus.infrastructure.adapter.port;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.model.entity.GroupBuyLockOrderEntity;
import chat.platform.plus.infrastructure.gateway.GroupBuyMarketService;
import chat.platform.plus.infrastructure.gateway.dto.*;
import chat.platform.plus.infrastructure.gateway.response.Response;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.payments.nativepay.impl.NativePayServiceImpl;
import ltzf.payments.nativepay.model.order.OrderRequest;
import ltzf.payments.nativepay.model.order.OrderResponse;
import ltzf.payments.nativepay.model.order.RefundOrderRequest;
import ltzf.payments.nativepay.model.order.RefundOrderResponse;
import ltzf.payments.nativepay.model.prepay.PrepayRequest;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;
import ltzf.payments.nativepay.model.refund.RefundRequest;
import ltzf.payments.nativepay.model.refund.RefundResponse;
import org.apache.commons.lang3.StringUtils;
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
    public PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName, GroupBuyLockOrderEntity groupBuyLockOrderEntity) throws IOException{
        // 封装请求
        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setMchId(mchId);
        prepayRequest.setOutTradeNo(orderId);
        prepayRequest.setTotalFee(groupBuyLockOrderEntity != null ? groupBuyLockOrderEntity.getPayPrice().toString() : orderPrice.toString());
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
    public RefundResponse doRefundOrder(String orderId, String refundOrderId, BigDecimal refundOrderPrice, String refundDesc) throws IOException {
        // 封装请求
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setMchId(mchId);
        refundRequest.setOutTradeNo(orderId);
        refundRequest.setOutRefundNo(refundOrderId);
        refundRequest.setRefundFee(refundOrderPrice.toString());
        refundRequest.setRefundDesc(StringUtils.isBlank(refundDesc) ? "无退款原因" : refundDesc);
        refundRequest.setNotifyUrl(ltzfNotifyUrl);
        // 发起请求
        return nativePayService.refund(refundRequest);
    }

    @Override
    public RefundOrderResponse getRefundOrderResponse(String refundOrderId) throws IOException {
        // 封装请求
        RefundOrderRequest refundOrderRequest = new RefundOrderRequest();
        refundOrderRequest.setMchId(mchId);
        refundOrderRequest.setOutRefundNo(refundOrderId);
        // 发送请求
        RefundOrderResponse refundOrderResponse = nativePayService.getRefundOrder(refundOrderRequest);
        log.info("退单订单ID：{}，退款结果：{}", refundOrderId, JSON.toJSONString(refundOrderResponse));
        return refundOrderResponse;
    }

    @Override
    public GroupBuyLockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId, String inviteId) {
        // 请求参数
        LockOrderRequestDTO requestDTO = new LockOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setTeamId(teamId);
        requestDTO.setGoodsId(goodsId);
        requestDTO.setActivityId(activityId);
        requestDTO.setSource(source);
        requestDTO.setChannel(channel);
        requestDTO.setInviteId(inviteId);
        requestDTO.setOutTradeNo(orderId);
        //requestDTO.setNotifyUrl(groupBuyNotifyUrl);
        requestDTO.setNotifyMQ();
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
            return GroupBuyLockOrderEntity.builder()
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

    @Override
    public void refundOrder(String userId, String orderId) {
        RefundOrderRequestDTO requestDTO = new RefundOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setOutTradeNo(orderId);
        requestDTO.setSource(source);
        requestDTO.setChannel(channel);
        try {
            log.info("请求拼团退单接口开始：{}", JSON.toJSONString(requestDTO));
            Call<Response<RefundOrderResponseDTO>> call = groupBuyMarketService.refundOrder(requestDTO);
            Response<RefundOrderResponseDTO> response = call.execute().body();
            log.info("请求拼团退单接口结果：{}", JSON.toJSONString(response));
            if (response == null) {
                return;
            }
            if (!response.getCode().equals("0000")) {
                throw new RuntimeException(response.getInfo());
            }
        } catch (Exception e) {
            log.info("请求拼团退单接口失败：{}", JSON.toJSONString(requestDTO), e);
        }
    }

    @Override
    public Integer getTeamProgress(String teamId) {
        try {
            log.info("请求拼团进度接口开始：{}", teamId);
            Call<Response<TeamProgressResponseDTO>> call = groupBuyMarketService.getTeamProgress(teamId);
            Response<TeamProgressResponseDTO> response = call.execute().body();
            log.info("请求拼团退单接口结果：{}", JSON.toJSONString(response));
            if (response == null) {
                return null;
            }
            if (!response.getCode().equals("0000")) {
                throw new RuntimeException(response.getInfo());
            }
            return response.getData().getStatus();
        } catch (Exception e) {
            log.info("请求拼团退单接口失败：{}", teamId, e);
        }
        return null;
    }

}

