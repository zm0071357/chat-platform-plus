package chat.platform.plus.types.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * OSS工具类
 */
@Slf4j
@Component
public class AliOSSUtil {
    @Value("${aliyun.oss.endpoint}")
    private static String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private static String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private static String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private static String bucketName;

    /**
     * 上传文件
     * @param file
     * @return
     * @throws IOException
     */
    public static String upload(MultipartFile file) throws IOException {
        // 获取文件输入流
        InputStream inputStream = file.getInputStream();
        // 文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        // 开启OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);
        // 文件访问路径
        String url = "https://" + bucketName + "." + endpoint.substring(endpoint.lastIndexOf("/")+1) + "/" + fileName;
        // 关闭OSS客户端
        ossClient.shutdown();
        log.info("上传完成:{},{}", file.getOriginalFilename(), url);
        return url;
    }

    /**
     * 上传url类型的文件
     * @param fileUrl
     * @return
     * @throws IOException
     */
    public static String uploadStringUrl(String fileUrl) throws IOException {
        // 创建HTTP连接
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OSS ossClient = null;

        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000); // 视频文件可能需要更长时间

            // 获取输入流
            inputStream = connection.getInputStream();

            // 从URL中提取文件名和扩展名
            String originalFileName;
            String fileExtension = null;

            // 处理带有查询参数的URL
            String path = url.getPath();
            if (path.contains(".")) {
                originalFileName = path.substring(path.lastIndexOf("/") + 1);
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } else {
                String contentType = connection.getContentType();
                if (contentType != null) {
                    if (contentType.contains("image/png")) {
                        fileExtension = ".png";
                    } else if (contentType.contains("video/mp4")) {
                        fileExtension = ".mp4";
                    }
                }
            }
            // 生成唯一的文件名
            String fileName = UUID.randomUUID().toString() + fileExtension;
            // 创建OSS客户端并上传文件
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, fileName, inputStream);
            // 构建访问URL
            String ossUrl = "https://" + bucketName + "." + endpoint.substring(endpoint.lastIndexOf("/") + 1) + "/" + fileName;
            log.info("URL文件上传完成: {}, {}", fileUrl, ossUrl);
            return ossUrl;
        } catch (Exception e) {
            log.error("上传URL文件失败: {}", fileUrl, e);
            throw new IOException("上传URL文件失败", e);
        } finally {
            // 关闭资源
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭输入流失败", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
