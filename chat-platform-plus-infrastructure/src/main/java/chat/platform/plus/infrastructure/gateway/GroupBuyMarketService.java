package chat.platform.plus.infrastructure.gateway;

import chat.platform.plus.infrastructure.gateway.dto.*;
import chat.platform.plus.infrastructure.gateway.response.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    /**
     * 拼团营销服务 - 结算
     * @param refundOrderRequestDTO
     * @return
     */
    @POST("/trade/refund_order")
    Call<Response<RefundOrderResponseDTO>> refundOrder(@Body RefundOrderRequestDTO refundOrderRequestDTO);

    /**
     * 拼团营销服务 - 查询拼团进度
     * @param teamId 拼团组队ID
     * @return
     */
    @POST("/trade/team_progress/{teamId}")
    Call<Response<TeamProgressResponseDTO>> getTeamProgress(@Path("teamId") String teamId);

}
