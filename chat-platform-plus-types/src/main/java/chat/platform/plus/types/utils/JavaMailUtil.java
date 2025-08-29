package chat.platform.plus.types.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

/**
 * 发送邮件工具类
 */
@Component
public class JavaMailUtil {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.application_name}")
    private String applicationName;

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 发送邮件到指定邮箱
     * @param receiverMail  接收者邮箱
     * @param title 标题
     * @param content 内容
     * @param isHtml 是否HTML
     * @return
     */
    public Boolean sendMimeMessage(String receiverMail, String title, String content, Boolean isHtml) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(username, applicationName);
            helper.setTo(receiverMail);
            helper.setSubject(title);
            helper.setText(content, isHtml);
            javaMailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取发送验证码文本
     * @param VC 验证码
     * @return
     */
    public String getSendVCContent(String VC) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>验证码邮件</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 15px;\">\n" +
                "    <div style=\"text-align: center; padding: 15px 0; border-bottom: 2px solid #4a90e2; margin-bottom: 25px;\">\n" +
                "        <div style=\"font-size: 24px; font-weight: bold; color: #4a90e2;\">浅度浏览AI服务平台</div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <p>您好！欢迎登录浅度浏览AI服务平台，请使用以下验证码完成验证：</p>\n" +
                "    \n" +
                "    <div style=\"background-color: #f5f9ff; border: 2px dashed #4a90e2; border-radius: 8px; padding: 20px; text-align: center; margin: 25px 0;\">\n" +
                "        <div style=\"font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #4a90e2; margin: 15px 0; padding: 10px; display: inline-block;\">" + VC + "</div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <div style=\"background-color: #fff9e6; border-left: 4px solid #ffc107; padding: 12px; margin: 20px 0;\">\n" +
                "        <strong>请注意：</strong>此验证码 <strong>3分钟内有效</strong>，请尽快使用。如非本人操作，请忽略此邮件。\n" +
                "    </div>\n" +
                "    \n" +
                "    <p>感谢您使用我们的服务！</p>\n" +
                "    \n" +
                "    <div style=\"margin-top: 30px; padding-top: 15px; border-top: 1px solid #ddd; font-size: 12px; color: #777; text-align: center;\">\n" +
                "        <p>此为系统自动发送邮件，请勿直接回复。</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * 获取发送注册成功欢迎使用文本
     * @param userId 用户名
     * @param userName 账号
     * @param password 密码
     * @return
     */
    public String getSendWelContent(String userId, String userName, String password) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>欢迎邮件</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 15px;\">\n" +
                "    <div style=\"text-align: center; padding: 15px 0; border-bottom: 2px solid #4a90e2; margin-bottom: 25px;\">\n" +
                "        <div style=\"font-size: 24px; font-weight: bold; color: #4a90e2;\">浅度浏览AI服务平台</div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <p>您好，欢迎使用浅度浏览AI服务平台！</p>\n" +
                "    \n" +
                "    <p>您的注册信息如下：</p>\n" +
                "    \n" +
                "    <div style=\"background-color: #f5f9ff; border: 2px solid #4a90e2; border-radius: 8px; padding: 20px; margin: 25px 0;\">\n" +
                "        <div style=\"margin-bottom: 10px;\"><strong>用户名：</strong>" + userName + "</div>\n" +
                "        <div style=\"margin-bottom: 10px;\"><strong>用户账号：</strong>" + userId + "</div>\n" +
                "        <div><strong>用户密码：</strong>" + password + "</div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <div style=\"background-color: #fff9e6; border-left: 4px solid #ffc107; padding: 12px; margin: 20px 0;\">\n" +
                "        <strong>温馨提示：</strong>请妥善保管好您的个人信息，感谢您的使用！\n" +
                "    </div>\n" +
                "    \n" +
                "    <div style=\"margin-top: 30px; padding-top: 15px; border-top: 1px solid #ddd; font-size: 12px; color: #777; text-align: center;\">\n" +
                "        <p>此为系统自动发送邮件，请勿直接回复。</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * 获取发送黑名单文本
     * @return
     */
    public String getSendBlackContent(String userName) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>黑名单通知</title>\n" +
                "</head>\n" +
                "<body style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 15px;\">\n" +
                "    <div style=\"text-align: center; padding: 15px 0; border-bottom: 2px solid #e74c3c; margin-bottom: 25px;\">\n" +
                "        <div style=\"font-size: 24px; font-weight: bold; color: #e74c3c;\">浅度浏览AI服务平台</div>\n" +
                "    </div>\n" +
                "    \n" +
                "    <p>" + userName + "，您好：</p>\n" +
                "    \n" +
                "    <div style=\"background-color: #fef2f2; border: 2px solid #e74c3c; border-radius: 8px; padding: 20px; margin: 25px 0;\">\n" +
                "        <p style=\"color: #e74c3c; font-weight: bold; margin: 0;\">检测到您同一时间内多次调用接口，现拉入黑名单处理，不允许使用功能</p>\n" +
                "    </div>\n" +
                "    \n" +
                "    <div style=\"background-color: #fff9e6; border-left: 4px solid #ffc107; padding: 12px; margin: 20px 0;\">\n" +
                "        <strong>请注意：</strong>如有疑问，请联系平台客服进行处理。\n" +
                "    </div>\n" +
                "    \n" +
                "    <div style=\"margin-top: 30px; padding-top: 15px; border-top: 1px solid #ddd; font-size: 12px; color: #777; text-align: center;\">\n" +
                "        <p>此为系统自动发送邮件，请勿直接回复。</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

}
