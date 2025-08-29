package chat.platform.plus.api;

import chat.platform.plus.api.dto.*;
import chat.platform.plus.api.response.Response;

/**
 * 首页服务
 */
public interface IndexService {

    /**
     * 账密登录
     * @param loginByPWReqDTO
     * @return
     */
    Response<LoginResDTO> loginByPW(LoginByPWReqDTO loginByPWReqDTO);

    /**
     * 邮箱验证码登录
     * @param loginByVCReqDTO
     * @return
     */
    Response<LoginResDTO> loginByVC(LoginByVCReqDTO loginByVCReqDTO);

    /**
     * 注册
     * @param registerReqDTO
     * @return
     */
    Response<RegisterResDTO> register(RegisterReqDTO registerReqDTO);

    /**
     * 获取验证码
     * @param userEmail
     * @return
     */
    Response<VCResDTO> getVC(String userEmail);
}
