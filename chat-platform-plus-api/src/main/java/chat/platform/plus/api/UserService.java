package chat.platform.plus.api;

import chat.platform.plus.api.dto.DeleteHistoryResDTO;
import chat.platform.plus.api.dto.LoginResDTO;
import chat.platform.plus.api.dto.UserResDTO;
import chat.platform.plus.api.response.Response;

public interface UserService {

    /**
     * 获取用户信息
     * @return
     */
    Response<UserResDTO> userInfo();

    /**
     * 退出登录
     * @return
     */
    Response<LoginResDTO> logout();

    /**
     * 是否登录
     * @return
     */
    Response<Boolean> isLogin();

}
