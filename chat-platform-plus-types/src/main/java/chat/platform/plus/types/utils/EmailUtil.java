package chat.platform.plus.types.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮箱工具类
 */
@Component
public class EmailUtil {

    /**
     * 校验邮箱是否合法
     * @param email 邮箱
     * @return
     */
    public static Boolean check(String email) {
        // 非空校验
        if (email == null || email.isEmpty()) {
            return false;
        }
        // 正则表达式 - 邮箱格式校验
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
