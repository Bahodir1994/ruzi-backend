package app.ruzi.service.payload.tasks;

import app.ruzi.configuration.annotation.notfoundcolumn.NotFoundId;
import app.ruzi.configuration.annotation.validfile.ValidFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentRequestDto {
    private String parentId;

    private String type;

    @ValidFile(minSize = 0, maxSize = 5, format = {"pdf", "xlsx"})
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
