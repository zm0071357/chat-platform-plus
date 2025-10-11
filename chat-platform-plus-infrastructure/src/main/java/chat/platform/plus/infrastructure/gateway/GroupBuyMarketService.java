package chat.platform.plus.infrastructure.gateway;

import chat.platform.plus.infrastructure.gateway.dto.LockOrderRequestDTO;
import chat.platform.plus.infrastructure.gateway.dto.LockOrderResponseDTO;
import chat.platform.plus.infrastructure.gateway.dto.SettleOrderRequestDTO;
import chat.platform.plus.infrastructure.gateway.dto.SettleOrderResponseDTO;
import chat.platform.plus.infrastructure.gateway.response.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GroupBuyMarketService {

    /**
     * 拼团营销服务 - 锁单
     * @param lockOrderRequestDTO
     * @return
     */
    @POST("/trade/lock_order")
    Call<Response<LockOrderResponseDTO>> lockOrder(@Body LockOrderRequestDTO lockOrderRequestDTO);

    /**
     * 拼团营销服务 - 结算
     * @param settleOrderRequestDTO
     * @return
     */
    @POST("/trade/settle_order")
    Call<Response<SettleOrderResponseDTO>> settleOrder(@Body SettleOrderRequestDTO settleOrderRequestDTO);

}
