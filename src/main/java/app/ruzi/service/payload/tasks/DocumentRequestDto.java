package app.ruzi.service.payload.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentRequestDto {
    private List<String> idList;

    @Nullable
    private MultipartFile multipartFile;

    public String getMultipartFileHash() {
        String hashCode = "";
        try {
            hashCode = DigestUtils.md5DigestAsHex(multipartFile.getBytes());
        } catch (IOException e) {
            return "";
        }
        return hashCode;
    }
}
