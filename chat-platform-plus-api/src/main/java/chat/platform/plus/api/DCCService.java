package chat.platform.plus.api;


import chat.platform.plus.api.response.Response;

public interface DCCService {

    /**
     * 更新配置
     * @param key
     * @param value
     * @return
     */
    Response<Boolean> updateConfig(String key, String value);

}
