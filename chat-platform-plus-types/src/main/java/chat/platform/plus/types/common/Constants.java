package chat.platform.plus.types.common;

public class Constants {
    public static final String SPLIT = ",";
    public static final String KEY_SUFFIX = "_ragTagList";
    public static final String SYSTEM_PROMPT = """
                Your name is Shallow Browse,Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
                If unsure, simply state that you don't know.
                Another thing you need to note is that your reply must be in Chinese!
                DOCUMENTS:
                    {documents}
                """;
    public static final String OrderNotifyJobLock = "lztf_order_notify_job_lock";
    public static final String RefundOrderNotifyJobLock = "lztf_refund_order_notify_job_lock";
    public static final String OrderLock = "lztf_order_lock_";
    public static final String CloseOrderJobLock = "lztf_order_close_job_lock";
    public static final String HeaderRefundCompensateJobLock = "header_refund_compensate_job_lock";
}
