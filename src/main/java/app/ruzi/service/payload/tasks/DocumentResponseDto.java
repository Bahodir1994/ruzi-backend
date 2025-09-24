package app.ruzi.service.payload.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentResponseDto {
    private String docName;
    private String docType;
    private String docNameUni;
    private String type;
    private MultipartFile multipartFile;
    private byte[] fileBytes;

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
