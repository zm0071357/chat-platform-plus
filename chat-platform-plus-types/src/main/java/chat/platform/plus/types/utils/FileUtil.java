package chat.platform.plus.types.utils;

import chat.platform.plus.types.enums.FileTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * 文件工具类
 */
@Slf4j
@Component
public class FileUtil {

    @Resource
    private TokenTextSplitter tokenTextSplitter;

    /**
     * MultipartFile - 获取文件类型
     * @param file
     * @return
     * @throws IOException
     */
    public Integer getFileTypeByMult(MultipartFile file) throws IOException {
        if (file == null) {
            return FileTypeEnum.DEFAULT.getType();
        }
        Tika tika = new Tika();
        String type = tika.detect(file.getBytes());
        if (type.contains("image")) {
            return FileTypeEnum.IMAGE.getType();
        } else if (type.contains("video")) {
            return FileTypeEnum.VIDEO.getType();
        } else if (type.contains("audio")) {
            return FileTypeEnum.AUDIO.getType();
        } else {
            return FileTypeEnum.OTHER.getType();
        }
    }

    /**
     * String - 获取文件类型
     * @param fileUrl
     * @return
     */
    public Integer getFileTypeByStr(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return FileTypeEnum.DEFAULT.getType();
        }
        try {
            URI uri = new URI(fileUrl);
            String path = uri.getPath();
            // 处理没有扩展名的情况
            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex == -1 || lastDotIndex == path.length() - 1) {
                return FileTypeEnum.OTHER.getType();
            }
            // 截取文件后缀名，确保忽略查询参数部分
            String fileExtension = path.substring(lastDotIndex + 1).toLowerCase();// 可能的文件类型后缀列表
            List<String> imageTypes = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp", "svg");
            List<String> videoTypes = Arrays.asList("mp4", "avi", "mkv", "mov", "flv", "wmv", "mpeg", "webm");
            List<String> audioTypes = Arrays.asList("mp3", "wav", "ogg", "flac", "aac", "wma");
            // 根据文件后缀判断类型
            if (imageTypes.contains(fileExtension)) {
                return FileTypeEnum.IMAGE.getType();
            } else if (videoTypes.contains(fileExtension)) {
                return FileTypeEnum.VIDEO.getType();
            } else if (audioTypes.contains(fileExtension)) {
                return FileTypeEnum.AUDIO.getType();
            } else  {
                return FileTypeEnum.OTHER.getType();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FileTypeEnum.DEFAULT.getType();
        }
    }

    /**
     * 获取文件的文本并进行分段
     * @param fileUrl
     * @return
     */
    public List<Document> getFileContent(String fileUrl) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(fileUrl);
        List<Document> documents = tikaDocumentReader.get();
        return tokenTextSplitter.apply(documents);
    }
}
