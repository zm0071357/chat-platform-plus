package chat.platform.plus.domain.chat.model.valobj;

public class ResultConstant {
    public static final String Fail_IsBlack = "调用失败，黑名单用户不可使用功能，如要申诉请找管理员";
    public static final String Fail_Count = "调用失败，可用次数不足，请购买次数或升级为VIP用户后重试";
    public static final String Fail_File_Type = "调用失败，错误的文件类型";
    public static final String Success_File = "上传文件成功";
    public static final String Fail_File = "上传文件失败";
    public static final String Fail_Illegal_Param = "调用失败，参数非法";
    public static final String Fail_MultiType = "调用失败，多模态不支持多种文件类型，目前支持：" +
            "\n" + "图片+文本输入\n" +
            "\n" + "音频+文本输入\n" +
            "视频+文本输入";
    public static final String Fail = "处理失败，请稍后再试";
    public static final String Fail_Lack_Param = "处理失败，缺少必要参数";
}
